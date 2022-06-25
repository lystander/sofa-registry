/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.session.metadata;

import com.alipay.sofa.registry.cache.CacheCleaner;
import com.alipay.sofa.registry.common.model.DataInfoIdGenerator;
import com.alipay.sofa.registry.common.model.appmeta.InterfaceMapping;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.store.MultiSubDatum;
import com.alipay.sofa.registry.common.model.store.SubDatum;
import com.alipay.sofa.registry.common.model.store.SubPublisher;
import com.alipay.sofa.registry.exception.SofaRegistryRuntimeException;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.cache.DatumKey;
import com.alipay.sofa.registry.server.session.cache.EntityType;
import com.alipay.sofa.registry.server.session.cache.Key;
import com.alipay.sofa.registry.server.session.cache.Value;
import com.alipay.sofa.registry.server.session.registry.RegistryScanCallable;
import com.alipay.sofa.registry.server.session.store.Interests.InterestVersionCheck;
import com.alipay.sofa.registry.server.shared.util.DatumUtils;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.alipay.sofa.registry.server.session.metadata.MetadataCacheMetrics.Fetch.APPS_CACHE_HIT_COUNTER;
import static com.alipay.sofa.registry.server.session.metadata.MetadataCacheMetrics.Fetch.APPS_CACHE_MISS_COUNTER;

/**
 * @author xiaojian.xj
 * @version : AppRevisionCacheService.java, v 0.1 2022年06月24日 19:37 xiaojian.xj Exp $
 */
public class ServiceAppMappingCacheService extends BaseMetadataCache<InterfaceMapping> {

  protected static final Logger SCAN_VER_LOGGER = LoggerFactory.getLogger("SCAN-VER", "[scanMapping]");

  private final Map<String/*dataInfoId*/, MappingVersionStorage> versions = Maps.newConcurrentMap();

  private final VersionWatchDog versionWatchDog = new VersionWatchDog();

  @Autowired
  private RegistryScanCallable registryScanCallable;

  public ServiceAppMappingCacheService() {
    this.readWriteCacheMap =
        CacheBuilder.newBuilder()
            .maximumWeight(sessionServerConfig.getCacheServiceAppsMappingMaxWeight())
            .weigher((Weigher<Key, Value>) (key, value) -> key.size() + value.size())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .removalListener(new RemoveMappingListener())
            .build(
                new CacheLoader<Key, Value>() {
                  @Override
                  public Value load(Key key) throws InterruptedException {
                    APPS_CACHE_MISS_COUNTER.inc();
                    return generatePayload(key);
                  }
                });
    CacheCleaner.autoClean(readWriteCacheMap, 10 * 1000);
  }

  @PostConstruct
  public void init() {
    ConcurrentUtils.createDaemonThread("MappingVerWatchDog", versionWatchDog).start();
  }

  @Override
  protected String dataInfoIdOf(String dataInfoId) {
    return DataInfoIdGenerator.serviceAppsId(dataInfoId);
  }

  @Override
  protected void hitMetric() {
    APPS_CACHE_HIT_COUNTER.inc();
  }

  @Override
  protected InterfaceMapping decode(MultiSubDatum datum) throws IOException, ClassNotFoundException {
    if (CollectionUtils.isEmpty(datum.getDatumMap())) {
      throw new SofaRegistryRuntimeException("query revision failed");
    }

    long version = Long.MIN_VALUE;
    Set<String> apps = Sets.newHashSet();

    Map<String, Long> vers = Maps.newHashMapWithExpectedSize(datum.getDatumMap().size());
    for (Entry<String, SubDatum> entry : datum.getDatumMap().entrySet()) {
      // some datacenter may not exist value
      if (entry.getValue() == null) {
        continue;
      }

      String dataCenter = entry.getKey();
      SubDatum unzip = DatumUtils.decompressSubDatum(entry.getValue());
      List<SubPublisher> pubs = unzip.mustGetPublishers();
      ParaCheckUtil.checkEquals(pubs.size(), 1, "revision.pubs");
      ParaCheckUtil.checkEquals(pubs.get(0).getDataList(), 1, "revision.pubs.dataList");
      InterfaceMapping mapping = (InterfaceMapping) pubs.get(0).getDataList().get(0).extract();

      if (mapping.getNanosVersion() > version) {
        version = mapping.getNanosVersion();
      }
      apps.addAll(mapping.getApps());
      vers.put(dataCenter, mapping.getNanosVersion());
    }
    InterfaceMapping ret = new InterfaceMapping(version, apps);
    updateVersions(ret.getNanosVersion(), vers);

    return ret;
  }

  private void updateVersions(long version, Map<String, Long> updates) {
    for (Entry<String, Long> entry : updates.entrySet()) {
      MappingVersionStorage storage = versions.computeIfAbsent(entry.getKey(), k -> new MappingVersionStorage());
      storage.update(version, updates);
    }
  }

  private final class MappingVersionStorage {

    volatile long maxVersion;

    final Map<String/*dataCenter*/, Long> versions = Maps.newHashMapWithExpectedSize(4096);


    public synchronized Long getVersion(String dataCenter) {
      return versions.get(dataCenter);
    }

    public synchronized void update(long version, Map<String, Long> updates) {
      if (maxVersion > version) {
        return;
      } else if (maxVersion == version) {
        checkVersions(Maps.newHashMap(versions), updates);
      } else {
        long preMaxVersion = maxVersion;
        Map<String, Long> preVersions = Maps.newHashMap(versions);
        update(updates);
        LOG.info("[update]prev: {}/{}, current:{}/{}", preMaxVersion, preVersions, maxVersion, versions);
      }

    }

    private void update(Map<String, Long> updates) {
      for (Entry<String, Long> exist : versions.entrySet()) {
        Long update = updates.get(exist.getKey());
        if (exist.getValue().longValue() < update.longValue()) {
          exist.setValue(update);
        }
      }
    }

    private void checkVersions(Map<String, Long> exists, Map<String, Long> updates) {

      ParaCheckUtil.checkEquals(exists.keySet(), updates.keySet(), "mapping.versions");
      for (Entry<String, Long> exist : exists.entrySet()) {
        Long update = updates.get(exist.getKey());
        if (exist.getValue().longValue() != update.longValue()) {
          LOG.error("[checkVersions]maxVersion:{}, exist.versions:{}, update.versions:{}",
                  maxVersion, versions, update);
        }
      }
    }
  }

  private final class VersionWatchDog extends WakeUpLoopRunnable {

    long scanRound;
    long lastScanTimestamp;

    @Override
    public void runUnthrowable() {
      try {

      } catch (Throwable t) {
        SCAN_VER_LOGGER.error("WatchDog failed fetch versions", t);
      }
      final int intervalMillis = sessionServerConfig.getScanServiceAppsMappingIntervalMillis();
      final long now = System.currentTimeMillis();

      if (Math.abs(now - lastScanTimestamp) >= intervalMillis) {
        try {
          scanVersions(scanRound++);
        } finally {
          lastScanTimestamp = System.currentTimeMillis();
        }
      }
    }

    @Override
    public int getWaitingMillis() {
      return 1000;
    }
  }


  private void scanVersions(long round) {

    Set<String> dataCenters = Sets.newLinkedHashSet();
    dataCenters.add(sessionServerConfig.getSessionServerDataCenter());
    // todo xiaojian.xj datacenters list
    dataCenters.addAll(Collections.EMPTY_SET);
    final long start = System.currentTimeMillis();

    for (String dataCenter : dataCenters) {
      Map<String, DatumVersion> interest = interestVersions(dataCenter);
      SCAN_VER_LOGGER.info("[scan]round={}, dataInfoIdSize={}", round, interest.size());
      registryScanCallable.scanVersions(
          round,
          dataCenter,
          interest,
          callableInfo -> {
            if (checkInterest(callableInfo.getDataCenter(), callableInfo.getDataInfoId(), callableInfo.getVersion().getValue()).interested) {
              query(callableInfo.getDataInfoId());
              SCAN_VER_LOGGER.info(
                  "[fetchSlotVerNotify]round={},{},{},{},{}",
                  callableInfo.getRound(),
                  callableInfo.getVersion(),
                  callableInfo.getDataInfoId(),
                  callableInfo.getDataCenter(),
                  callableInfo.getVersion().getValue());
            }
          });
    }

  }

  private InterestVersionCheck checkInterest(String dataCenter, String dataInfoId, long version) {
    MappingVersionStorage storage = versions.get(dataInfoId);
    if (storage == null) {
      return InterestVersionCheck.NoSub;
    }
    Long exist = storage.getVersion(dataCenter);
    if (exist == null || exist.longValue() < version) {
      return InterestVersionCheck.Interested;
    }
    return InterestVersionCheck.Obsolete;
  }

  private Map<String, DatumVersion> interestVersions(String dataCenter) {
    Map<String, DatumVersion> ret = Maps.newHashMapWithExpectedSize(4096);


    for (Entry<String, MappingVersionStorage> entry : versions.entrySet()) {
      String dataInfoId = entry.getKey();
      Long version = entry.getValue().getVersion(dataCenter);
      ret.put(dataInfoId, new DatumVersion(version != null ? version.longValue() : -1L));
    }
    return ret;
  }

  private final class RemoveMappingListener implements RemovalListener<Key, Value> {

    /**
     * Notifies the listener that a removal occurred at some point in the past.
     * @param notification
     */
    @Override
    public void onRemoval(RemovalNotification<Key, Value> notification) {

      EntityType entityType = notification.getKey().getEntityType();
      if (entityType instanceof DatumKey) {
        DatumKey datumKey = (DatumKey) entityType;
        MappingVersionStorage remove = versions.remove(datumKey.getDataInfoId());
        LOG.info("onRemoval dataInfoId:{}, storage:{}", datumKey.getDataInfoId(), remove);
      }
    }
  }
}
