/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.multi.cluster.storage;

import com.alipay.sofa.registry.common.model.ConnectId;
import com.alipay.sofa.registry.common.model.ProcessId;
import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.dataserver.Datum;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.exception.UnSupportOperationException;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.cache.BaseDatumStorage;
import com.alipay.sofa.registry.server.data.cache.CleanContinues;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteDatumStorage.java, v 0.1 2022年05月05日 21:21 xiaojian.xj Exp $
 */
public class MultiClusterDatumStorage implements DatumStorage {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MultiClusterDatumStorage.class, "[RemoteStorage]");

  // map<dataCenter, storage>
  private final Map<String, BaseDatumStorage> storageMap = Maps.newConcurrentMap();

  private final MultiClusterSlotListener listener = new MultiClusterSlotListener();

  /**
   * get datum by specific dataInfoId
   *
   * @param dataCenter
   * @param dataInfoId
   * @return
   */
  @Override
  public Datum get(String dataCenter, String dataInfoId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]dataCenter={},dataInfoId={}", dataCenter, dataInfoId);
      return null;
    }
    return storage.get(dataInfoId);
  }

  @Override
  public DatumVersion getVersion(String dataCenter, String dataInfoId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getVersion dataCenter={},dataInfoId={}", dataCenter, dataInfoId);
      return null;
    }
    return storage.getVersion(dataInfoId);
  }

  @Override
  public Map<String, DatumVersion> getVersions(
      String dataCenter, int slotId, Collection<String> targetDatInfoIds) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
          "[nullStorage]getVersions dataCenter={},slotId={}, targetDatInfoIds={}",
          dataCenter,
          slotId,
          targetDatInfoIds);
      return Collections.emptyMap();
    }
    return storage.getVersions(slotId, targetDatInfoIds);
  }

  @Override
  public Map<String, Publisher> getByConnectId(ConnectId connectId) {
    throw new UnSupportOperationException("MultiClusterDatumStorage.getByConnectId");
  }

  @Override
  public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slotId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getPublishers dataCenter={},slotId={}", dataCenter, slotId);
      return Collections.emptyMap();
    }
    return storage.getPublishers(slotId);
  }

  @Override
  public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slotId, SyncSlotAcceptorManager acceptorManager) {
    ParaCheckUtil.checkNotNull(acceptorManager, "acceptorManager");

    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getPublishers dataCenter={},slotId={}", dataCenter, slotId);
      return Collections.emptyMap();
    }
    return storage.getPublishers(slotId, acceptorManager);
  }

  /**
   * get all datum
   *
   * @return
   * @param dataCenter
   */
  @Override
  public Map<String, Datum> getAll(String dataCenter) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getAll dataCenter={}", dataCenter);
      return Collections.emptyMap();
    }
    return storage.getAll();
  }

  @Override
  public Map<String, List<Publisher>> getAllPublisher(String dataCenter) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getAllPublisher dataCenter={}", dataCenter);
      return Collections.emptyMap();
    }
    return storage.getAllPublisher();
  }

  @Override
  public Map<String, Integer> getPubCount(String dataCenter) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn("[nullStorage]getPubCount dataCenter={}", dataCenter);
      return Collections.emptyMap();
    }
    return storage.getPubCount();
  }

  @Override
  public void putPublisherGroups(String dataCenter, int slotId) {
    BaseDatumStorage storage =
        storageMap.computeIfAbsent(dataCenter, k -> new BaseDatumStorage(dataCenter, LOGGER));
    storage.putPublisherGroups(slotId);
  }

  @Override
  public DatumVersion putPublisher(String dataCenter, Publisher publisher) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
          "[nullStorage]putPublisher dataCenter={}, dataInfoId={}, registerId={}",
          dataCenter,
          publisher.getDataInfoId(),
          publisher.getRegisterId());
      return null;
    }
    return storage.putPublisher(publisher);
  }

  @Override
  public DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]createEmptyDatumIfAbsent dataCenter={}, dataInfoId={}",
              dataCenter,
              dataInfoId);
      return null;
    }
    return storage.createEmptyDatumIfAbsent(dataInfoId);
  }

  @Override
  public Map<String, DatumVersion> cleanBySessionId(
      String dataCenter, int slotId, ProcessId sessionProcessId, CleanContinues cleanContinues) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept cleanBySessionId, dataCenter={}, slotId={}, sessionProcessId={}",
            dataCenter, slotId, sessionProcessId);
    throw new UnSupportOperationException("MultiClusterDatumStorage.cleanBySessionId");
  }

  @Override
  public boolean removePublisherGroups(String dataCenter, int slotId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]removePublisherGroups dataCenter={}, slotId={}",
              dataCenter,
              slotId);
      return false;
    }
    return storage.removePublisherGroups(slotId);
  }

  @Override
  public DatumVersion removePublishers(
      String dataCenter, String dataInfoId, ProcessId sessionProcessId) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept removePublishers, dataCenter={}, dataInfoId={}, sessionProcessId={}",
            dataCenter, dataInfoId, sessionProcessId);
    throw new UnSupportOperationException("MultiClusterDatumStorage.removePublishersBySessionId");
  }

  @Override
  public DatumVersion removePublishers(
      String dataCenter,
      String dataInfoId,
      ProcessId sessionProcessId,
      Map<String, RegisterVersion> removedPublishers) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept removePublishers, dataCenter={}, dataInfoId={}, sessionProcessId={}, removedPublishers={}",
            dataCenter, dataInfoId, sessionProcessId, removedPublishers);
    throw new UnSupportOperationException("MultiClusterDatumStorage.removePublishersBySessionId");
  }

  @Override
  public DatumVersion putPublisher(
      String dataCenter, String dataInfoId, List<Publisher> updatedPublishers) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]putPublisher dataCenter={}, dataInfoId={}",
              dataCenter,
              dataInfoId);
      return null;
    }
    return storage.putPublisher(dataInfoId, updatedPublishers);
  }

  @Override
  public Map<String, Map<String, DatumSummary>> getDatumSummary(
      String dataCenter, int slotId, Set<String> sessions) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept getDatumSummary, dataCenter={}, dataInfoId={}, sessions={}",
            dataCenter, slotId, sessions);
    throw new UnSupportOperationException("MultiClusterDatumStorage.getDatumSummaryBySessions");
  }

  @Override
  public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]getDatumSummary dataCenter={}, slotId={}",
              dataCenter,
              slotId);
      return Collections.emptyMap();
    }
    return storage.getDatumSummary(slotId);
  }

  @Override
  public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId, SyncSlotAcceptorManager acceptorManager) {
    ParaCheckUtil.checkNotNull(acceptorManager, "acceptorManager");

    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]getDatumSummary dataCenter={}, slotId={}",
              dataCenter,
              slotId);
      return Collections.emptyMap();
    }
    return storage.getAcceptDatumSummary(slotId, acceptorManager);
  }

  @Override
  public SlotChangeListener getSlotChangeListener() {
    return listener;
  }

  @Override
  public Set<ProcessId> getSessionProcessIds(String dataCenter) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept getSessionProcessIds, dataCenter={}", dataCenter);
    throw new UnSupportOperationException("MultiClusterDatumStorage.getSessionProcessIds");
  }

  @Override
  public Map<String, Integer> compact(String dataCenter, long tombstoneTimestamp) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept getSessionProcessIds, dataCenter={}", dataCenter);
    throw new UnSupportOperationException("MultiClusterDatumStorage.getSessionProcessIds");
  }

  @Override
  public int tombstoneNum(String dataCenter) {
    LOGGER.error("[MultiClusterDatumStorage]UnExcept tombstoneNum, dataCenter={}", dataCenter);
    throw new UnSupportOperationException("MultiClusterDatumStorage.tombstoneNum");
  }

  @Override
  public Map<String, DatumVersion> updateVersion(String dataCenter, int slotId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]updateVersion dataCenter={}, slotId={}",
              dataCenter,
              slotId);
      return Collections.emptyMap();
    }
    return storage.updateVersion(slotId);
  }

  @Override
  public DatumVersion updateVersion(String dataCenter, String dataInfoId) {
    BaseDatumStorage storage = storageMap.get(dataCenter);
    if (storage == null) {
      LOGGER.warn(
              "[nullStorage]updateVersion dataCenter={}, dataInfoId={}",
              dataCenter,
              dataInfoId);
      return null;
    }
    return storage.updateVersion(dataInfoId);
  }

  private final class MultiClusterSlotListener implements SlotChangeListener {

    @Override
    public void onSlotAdd(String dataCenter, int slotId, Slot.Role role) {
      putPublisherGroups(dataCenter, slotId);
      LOGGER.info(
              "{} add publisherGroup {}, role={},",
              dataCenter,
              slotId,
              role);
    }

    @Override
    public void onSlotRemove(String dataCenter, int slotId, Slot.Role role) {
      boolean removed = removePublisherGroups(dataCenter, slotId);
      LOGGER.info(
              "{}, remove publisherGroup {}, removed={}, role={}",
              dataCenter,
              slotId,
              removed,
              role);
    }
  }
}
