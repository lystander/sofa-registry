/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.session.metadata;

import com.alipay.sofa.registry.cache.CacheCleaner;
import com.alipay.sofa.registry.common.model.DataInfoIdGenerator;
import com.alipay.sofa.registry.common.model.store.AppRevision;
import com.alipay.sofa.registry.common.model.store.MultiSubDatum;
import com.alipay.sofa.registry.common.model.store.SubDatum;
import com.alipay.sofa.registry.common.model.store.SubPublisher;
import com.alipay.sofa.registry.exception.SofaRegistryRuntimeException;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.cache.Key;
import com.alipay.sofa.registry.server.session.cache.Value;
import com.alipay.sofa.registry.server.shared.util.DatumUtils;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static com.alipay.sofa.registry.server.session.metadata.MetadataCacheMetrics.Fetch.REVISION_CACHE_HIT_COUNTER;
import static com.alipay.sofa.registry.server.session.metadata.MetadataCacheMetrics.Fetch.REVISION_CACHE_MISS_COUNTER;

/**
 * @author xiaojian.xj
 * @version : AppRevisionCacheService.java, v 0.1 2022年06月24日 19:37 xiaojian.xj Exp $
 */
public class AppRevisionCacheService extends BaseMetadataCache<AppRevision> {

  @Autowired private SessionServerConfig sessionServerConfig;

  /** map: <revision, AppRevision> */
  private final LoadingCache<Key, Value> registry;

  public AppRevisionCacheService() {
    this.registry =
        CacheBuilder.newBuilder()
            .maximumWeight(sessionServerConfig.getCacheRevisionMaxWeight())
            .weigher((Weigher<Key, Value>) (key, value) -> key.size() + value.size())
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(
                new CacheLoader<Key, Value>() {
                  @Override
                  public Value load(Key key) throws InterruptedException {
                    REVISION_CACHE_MISS_COUNTER.inc();
                    return generatePayload(key);
                  }
                });
    CacheCleaner.autoClean(readWriteCacheMap, 10 * 1000);
  }

  @Override
  protected String dataInfoIdOf(String dataInfoId) {
    return DataInfoIdGenerator.revisionId(dataInfoId);
  }

  @Override
  protected void hitMetric() {
    REVISION_CACHE_HIT_COUNTER.inc();
  }

  @Override
  protected AppRevision decode(MultiSubDatum datum) throws Exception {
    if (CollectionUtils.isEmpty(datum.getDatumMap())) {
      throw new SofaRegistryRuntimeException("query revision failed");
    }

    for (Entry<String, SubDatum> entry : datum.getDatumMap().entrySet()) {
      // only one datacenter exist value
      if (entry.getValue() == null) {
        continue;
      }
      SubDatum unzip = DatumUtils.decompressSubDatum(entry.getValue());
      List<SubPublisher> pubs = unzip.mustGetPublishers();
      ParaCheckUtil.checkEquals(pubs.size(), 1, "revision.pubs");
      ParaCheckUtil.checkEquals(pubs.get(0).getDataList(), 1, "revision.pubs.dataList");
      AppRevision revision = (AppRevision) pubs.get(0).getDataList().get(0).extract();
      return revision;
    }
    return null;
  }
}
