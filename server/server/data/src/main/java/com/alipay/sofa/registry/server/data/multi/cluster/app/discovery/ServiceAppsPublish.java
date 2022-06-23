/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.PublisherGroupType;
import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.appmeta.InterfaceMapping;
import com.alipay.sofa.registry.common.model.dataserver.BatchRequest;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.SlotAccessGenericResponse;
import com.alipay.sofa.registry.common.model.slot.filter.BaseSyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.common.model.store.WordCache;
import com.alipay.sofa.registry.jdbc.domain.InterfaceAppsIndexDomain;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.ServiceAppsPublish.ServiceMappingVersion;
import com.alipay.sofa.registry.store.api.meta.EntryNotify;
import com.alipay.sofa.registry.store.api.repository.InterfaceAppsRepository;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.alipay.sofa.registry.common.model.slot.filter.AcceptorConstants.SERVICE_MAPPING_ACCEPTOR;

/**
 * @author xiaojian.xj
 * @version : ServiceAppsPublish.java, v 0.1 2022年05月25日 20:48 xiaojian.xj Exp $
 */
public class ServiceAppsPublish extends MetadataSlotChangeListener<ServiceMappingVersion>
    implements EntryNotify<InterfaceAppsIndexDomain> {

  @Autowired private InterfaceAppsRepository interfaceAppsRepository;

  @PostConstruct
  public void init() {
    interfaceAppsRepository.registerNotify(this);
  }

  @Override
  public void notify(InterfaceAppsIndexDomain entry) {
    InterfaceMapping mapping = interfaceAppsRepository.getAppNames(entry.getInterfaceName());

    String pubDataInfoId = buildDataInfoId(entry.getInterfaceName());
    int slotId = slotFunction.slotOf(pubDataInfoId);
    MetadataLoadState state = slotLeaderStatus.get(slotId);

    pub2Datum(
        state.getSlotTableEpoch(),
        slotId,
        state.getSlotLeaderEpoch(),
        Collections.singletonList(
            buildPub(
                pubDataInfoId, mapping.getNanosVersion(), buildServerDataBox(mapping.getApps()))));
  }

  @Override
  protected int waitingMillis() {
    return dataServerConfig.getSlotSyncAppRevisionIntervalSecs() * 1000;
  }

  @Override
  protected SyncSlotAcceptorManager getSlotAcceptorManager() {
    return new BaseSyncSlotAcceptorManager(Sets.newHashSet(SERVICE_MAPPING_ACCEPTOR));
  }

  @Override
  protected KeyedThreadPoolExecutor getExecutor() {
    return multiClusterExecutorManager.getSyncServiceMappingExecutor();
  }

  @Override
  protected void dealWithPub(Set<ServiceMappingVersion> toBeAdd, long slotTableEpoch, int slotId, long slotLeaderEpoch) {
    if (CollectionUtils.isEmpty(toBeAdd)) {
      return;
    }

    try {
      List<Object> pubs = Lists.newArrayListWithExpectedSize(toBeAdd.size());
      for (ServiceMappingVersion add : toBeAdd) {
        pubs.add(buildPub(add.dataInfoId, add.version, buildServerDataBox(add.apps)));
      }
      pub2Datum(slotTableEpoch, slotId, slotLeaderEpoch, pubs);
    } catch (Throwable throwable) {
      LOGGER.error("[dealWithPub]pub pubs error, pubs:{}, slotTableEpoch:{}, slotId:{}, slotLeaderEpoch:{}", toBeAdd, slotTableEpoch, slotId, slotLeaderEpoch, throwable);
    }
  }

  /**
   * @param summary
   * @param metadata
   * @return
   */
  @Override
  protected boolean needRePub(DatumSummary summary, ServiceMappingVersion metadata) {
    if (summary.size() == 0) {
      return true;
    }
    if (summary.size() > 1) {
      LOGGER.error("[needRePub]unexpect summary size error: {},{}", summary, summary.getPublisherVersions());
      return false;
    }
    Entry<String, RegisterVersion> first = summary.getPublisherVersions().entrySet().stream().findFirst().get();

    long pubVersion = first.getValue().getVersion();
    if (metadata.version == pubVersion) {
      return false;
    } else if (metadata.version > pubVersion) {
      return true;
    } else {
      LOGGER.error("[needRePub]unexpect pub.version:{} > metadata.version:{}", pubVersion, metadata.version);
      return false;
    }

  }

  @Override
  protected Map<String, ServiceMappingVersion> queryMetadata(int slotId) {
    Set<String> dataInfoIds = interfaceAppsRepository.getAllDataInfoIds();

    Set<String> slotDataInfoIds = Sets.newHashSet();
    for (String dataInfoId : dataInfoIds) {
      if (slotFunction.slotOf(buildDataInfoId(dataInfoId)) == slotId) {
        slotDataInfoIds.add(dataInfoId);
      }
    }

    Map<String, ServiceMappingVersion> ret = Maps.newHashMapWithExpectedSize(slotDataInfoIds.size());
    for (String slotDataInfoId : slotDataInfoIds) {
      InterfaceMapping mapping = interfaceAppsRepository.getAppNames(slotDataInfoId);
      String pubDataInfoId = buildDataInfoId(slotDataInfoId);

      ret.put(pubDataInfoId,
              new ServiceMappingVersion(pubDataInfoId, mapping.getNanosVersion(), mapping.getApps()));
    }
    return ret;
  }

  @Override
  public String buildDataInfoId(String dataInfoId) {
    return WordCache.getWordCache(PublisherGroupType.REGISTRY_MAPPING.getCode() + dataInfoId);
  }

  private List<ServerDataBox> buildServerDataBox(Set<String> apps) {
    ServerDataBox serverDataBox = new ServerDataBox(ServerDataBox.getBytes(apps));
    return Collections.singletonList(serverDataBox);
  }

  static final class ServiceMappingVersion extends MetadataVersion {
    final Set<String> apps;

    public ServiceMappingVersion(String dataInfoId, long version, Set<String> apps) {
      super(dataInfoId, version);
      this.apps = apps;
    }
  }
}
