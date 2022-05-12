/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.multi.cluster.slot;

import com.alipay.sofa.registry.common.model.multi.cluster.RemoteSlotTableStatus;
import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.bootstrap.MultiClusterDataServerConfig;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.change.DataChangeEventCenter;
import com.alipay.sofa.registry.server.data.multi.cluster.exchanger.RemoteDataNodeExchanger;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.loggers.Loggers;
import com.alipay.sofa.registry.server.data.slot.SlotDiffSyncer;
import com.alipay.sofa.registry.server.data.slot.SyncContinues;
import com.alipay.sofa.registry.server.data.slot.SyncLeaderTask;
import com.alipay.sofa.registry.server.shared.env.ServerEnv;
import com.alipay.sofa.registry.task.KeyedTask;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSlotManagerImpl.java, v 0.1 2022年05月05日 21:23 xiaojian.xj Exp $
 */
public class MultiClusterSlotManagerImpl implements MultiClusterSlotManager {

  private static final Logger MULTI_CLUSTER_SLOT_TABLE = Loggers.MULTI_CLUSTER_SLOT_TABLE;

  private static final Logger MULTI_CLUSTER_CLIENT_LOGGER = Loggers.MULTI_CLUSTER_CLIENT_LOGGER;

  private static final Logger MULTI_CLUSTER_SYNC_DIGEST_LOGGER = Loggers.MULTI_CLUSTER_SYNC_DIGEST_LOGGER;

  @Autowired private DataServerConfig dataServerConfig;

  @Autowired private MultiClusterDataServerConfig multiClusterDataServerConfig;

  @Autowired private DataChangeEventCenter dataChangeEventCenter;

  @Resource private DatumStorage multiClusterDatumStorage;

  @Autowired private RemoteDataNodeExchanger remoteDataNodeExchanger;

  @Autowired private MultiClusterExecutorManager multiClusterExecutorManager;

  private static final Map<String, RemoteSlotTableStates> remoteSlotTableStates =
      Maps.newConcurrentMap();

  private static final Map<String, AtomicReference<SlotTable>> updatingSlotTable =
      Maps.newConcurrentMap();

  private final RemoteSyncingWatchDog watchDog = new RemoteSyncingWatchDog();

  @PostConstruct
  public void init() {
    initSlotChangeListener();
    initExecutors();
    ConcurrentUtils.createDaemonThread("RemoteSyncingWatchDog", watchDog).start();
  }

  private void initExecutors() {}

  void initSlotChangeListener() {}

  private static final class RemoteSlotTableStates {
    private final ReentrantReadWriteLock updateLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = updateLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = updateLock.readLock();

    final String dataCenter;
    // save only slot belong to us
    volatile SlotTable slotTable = SlotTable.INIT;

    // map<slotId, RemoteSlotStates>
    final Map<Integer, RemoteSlotStates> slotStates = Maps.newConcurrentMap();

    public RemoteSlotTableStates(String dataCenter) {
      this.dataCenter = dataCenter;
    }

    boolean updateSlotState(SlotTable update) {
      writeLock.lock();
      try {
        for (Slot slot : update.getSlots()) {
          RemoteSlotStates state =
              slotStates.computeIfAbsent(
                  slot.getId(),
                  k -> {
                    MULTI_CLUSTER_SLOT_TABLE.info("[updateSlotState]add slot={}", dataCenter, slot);
                    return new RemoteSlotStates(dataCenter, slot);
                  });
          state.update(slot);
        }

        final Iterator<Entry<Integer, RemoteSlotStates>> it = slotStates.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<Integer, RemoteSlotStates> e = it.next();
          if (update.getSlot(e.getKey()) == null) {
            final Slot slot = e.getValue().slot;
            it.remove();
            // first remove the slot for GetData Access check, then clean the data
            listenRemove(slot);
            MultiClusterSlotMetrics.observeRemoteLeaderSyncingFinish(dataCenter, slot.getId());
            MULTI_CLUSTER_SLOT_TABLE.info("dataCenter={} remove slot, slot={}", dataCenter, slot);
          }
        }
        this.slotTable = update;
        MultiClusterSlotMetrics.observeRemoteLeaderAssignGauge(
            dataCenter, this.slotTable.getLeaderNum(ServerEnv.IP));
      } catch (Throwable t) {
        MULTI_CLUSTER_SLOT_TABLE.error("[updateSlotTable]update slot table:{} error.", update, t);
        return false;
      } finally {
        writeLock.unlock();
      }
      return true;
    }

    private void listenRemove(Slot s) {}
  }

  private static final class RemoteSlotStates {
    final String remoteDataCenter;
    final int slotId;
    volatile Slot slot;
    volatile boolean synced;
    volatile long remoteSyncingStartTime = -1L;
    volatile long lastSuccessSyncRemoteTime = -1L;
    volatile KeyedTask<SyncLeaderTask> syncRemoteTask;

    RemoteSlotStates(String remoteDataCenter, Slot slot) {
      this.remoteDataCenter = remoteDataCenter;
      this.slotId = slot.getId();
      this.slot = slot;
    }

    void update(Slot update) {
      ParaCheckUtil.checkEquals(slotId, update.getId(), "slot.id");
      ParaCheckUtil.assertTrue(
          ServerEnv.isLocalServer(slot.getLeader()),
          StringFormatter.format("{} is not equal leader={}", ServerEnv.IP, slot.getLeader()));
      if (slot.getLeaderEpoch() != update.getLeaderEpoch()) {
        this.synced = false;
        // todo xiaojian.xj
        this.remoteSyncingStartTime = -1L;
        this.lastSuccessSyncRemoteTime = -1L;
        this.syncRemoteTask = null;
      }
      this.slot = update;
      MULTI_CLUSTER_SLOT_TABLE.info("remoteDataCenter={}, update slot={}", remoteDataCenter, update);
    }

    void completeSyncRemoteLeaderTask() {
      if (syncRemoteTask != null && syncRemoteTask.isSuccess()) {
        this.lastSuccessSyncRemoteTime = syncRemoteTask.getEndTime();
      }
    }
  }

  /**
   * get remote cluster slotTable epoch
   *
   * @return map<cluster, slotTableEpoch>
   */
  @Override
  public Map<String, Long> getSlotTableEpoch() {
    Map<String, Long> slotTableEpochMap =
        Maps.newHashMapWithExpectedSize(remoteSlotTableStates.size());
    for (Entry<String, RemoteSlotTableStates> entry : remoteSlotTableStates.entrySet()) {
      long epoch =
          entry.getValue() == null
              ? SlotTable.INIT.getEpoch()
              : entry.getValue().slotTable.getEpoch();
      slotTableEpochMap.put(entry.getKey(), epoch);
    }
    return slotTableEpochMap;
  }

  /**
   * 1.add new dataCenter slot table to remoteSlotTableStates 2.update exist dataCenter slot table
   * 3.important: don't remove slot table which not exist in meta, it should be remove trigger by
   * other way
   *
   * @param remoteSlotTableStatus
   */
  @Override
  public void updateSlotTable(Map<String, RemoteSlotTableStatus> remoteSlotTableStatus) {
    if (CollectionUtils.isEmpty(remoteSlotTableStatus)) {
      return;
    }

    boolean wakeup = false;
    for (Entry<String, RemoteSlotTableStatus> statusEntry : remoteSlotTableStatus.entrySet()) {
      RemoteSlotTableStatus slotTableStatus = statusEntry.getValue();
      if (slotTableStatus.isSlotTableEpochConflict()) {
        // local.slotTableEpoch > meta.slotTableEpoch
        // it should noe happen, print error log and restart data server;
        MULTI_CLUSTER_SLOT_TABLE.error(
            "[updateSlotTable]meta remote slot table status conflict: {}", slotTableStatus);
        continue;
      }

      String dataCenter = statusEntry.getKey();
      SlotTable curSlotTable =
          remoteSlotTableStates.computeIfAbsent(
                  dataCenter, k -> new RemoteSlotTableStates(dataCenter))
              .slotTable;
      // upgrade=false, slot table not change
      // upgrade=true, but data had accept a bigger version than return value
      if (!slotTableStatus.isSlotTableUpgrade()
          || curSlotTable.getEpoch() >= slotTableStatus.getSlotTableEpoch()) {
        continue;
      }

      // check updating slot table
      AtomicReference<SlotTable> updating =
          updatingSlotTable.computeIfAbsent(dataCenter, k -> new AtomicReference<>());
      SlotTable updatingSlotTable = updating.get();
      if (updatingSlotTable != null
          && updatingSlotTable.getEpoch() >= slotTableStatus.getSlotTableEpoch()) {
        continue;
      }
      // filter slot belong to me
      SlotTable toBeUpdate = slotTableStatus.getSlotTable().filter(ServerEnv.IP);

      if (!checkSlot(curSlotTable, updatingSlotTable, toBeUpdate)) {
        continue;
      }

      if (updating.compareAndSet(updatingSlotTable, toBeUpdate)) {
        wakeup = true;
        MULTI_CLUSTER_SLOT_TABLE.info(
            "updating slot table, dataCenter={}, new={}, current={}",
            dataCenter,
            toBeUpdate,
            curSlotTable);
      }
    }
    if (wakeup) {
      watchDog.wakeup();
    }
  }

  private boolean checkSlot(SlotTable cur, SlotTable updating, SlotTable update) {
    try {
      cur.assertSlotLessThan(update);
      if (updating != null) {
        update.assertSlotLessThan(update);
      }
      return true;
    } catch (RuntimeException e) {
      MULTI_CLUSTER_SLOT_TABLE.error(
          "[checkSlot]assert slot fail, cur: {}, updating: {}, update: {}",
          cur,
          updating,
          update,
          e);
      return false;
    }
  }

  private final class RemoteSyncingWatchDog extends WakeUpLoopRunnable {

    @Override
    public void runUnthrowable() {
      try {
        doUpdating();
        doSyncRemoteLeader();
      } catch (Throwable t) {
        MULTI_CLUSTER_CLIENT_LOGGER.error("[remoteSyncWatch]failed to do sync watching.", t);
      }
    }

    @Override
    public int getWaitingMillis() {
      return 200;
    }
  }

  /**
   * update remote state
   *
   * @return
   */
  boolean doUpdating() {
    for (Entry<String, AtomicReference<SlotTable>> entry : updatingSlotTable.entrySet()) {
      String dataCenter = entry.getKey();
      SlotTable update = entry.getValue().getAndSet(null);
      if (update == null) {
        continue;
      }
      RemoteSlotTableStates remoteStates =
          MultiClusterSlotManagerImpl.remoteSlotTableStates.get(dataCenter);
      SlotTable current = remoteStates.slotTable;
      if (update.getEpoch() <= current.getEpoch()) {
        MULTI_CLUSTER_SLOT_TABLE.warn(
            "skip remoteDataCenter={}, updating={}, current={}",
            dataCenter,
            update.getEpoch(),
            current.getEpoch());
        continue;
      }
      remoteStates.updateSlotState(update);
    }
    return true;
  }

  void doSyncRemoteLeader() {
    final int remoteSyncLeaderMs =
        multiClusterDataServerConfig.getSyncRemoteSlotLeaderIntervalSecs() * 1000;
    for (Entry<String, RemoteSlotTableStates> entry : remoteSlotTableStates.entrySet()) {
      String remoteDataCenter = entry.getKey();
      RemoteSlotTableStates states = entry.getValue();
      for (RemoteSlotStates state : states.slotStates.values()) {
        try {
          syncRemote(remoteDataCenter, state, remoteSyncLeaderMs, states.slotTable.getEpoch());
        } catch (Throwable t) {
          MULTI_CLUSTER_CLIENT_LOGGER.error(
              "[syncRemoteLeader]remoteDataCenter={}, slotId={} sync error.",
              entry.getKey(),
              state.slotId,
              t);
        }
      }
    }
  }

  void syncRemote(
      String remoteDataCenter,
      RemoteSlotStates state,
      int remoteSyncLeaderMs,
      long slotTableEpoch) {
    final Slot slot = state.slot;
    final KeyedTask<SyncLeaderTask> syncRemoteTask = state.syncRemoteTask;

    if (syncRemoteTask == null || syncRemoteTask.isOverAfter(remoteSyncLeaderMs)) {
      SlotDiffSyncer syncer =
          new SlotDiffSyncer(
              dataServerConfig,
              multiClusterDatumStorage,
              dataChangeEventCenter,
              null,
              MULTI_CLUSTER_CLIENT_LOGGER);
      SyncContinues continues = () -> isLeader(slot.getLeader());
      SyncLeaderTask task =
          new SyncLeaderTask(
              dataServerConfig.getLocalDataCenter(),
              remoteDataCenter,
              slotTableEpoch,
              slot,
              syncer,
              remoteDataNodeExchanger,
              continues,
              MULTI_CLUSTER_SYNC_DIGEST_LOGGER,
              MULTI_CLUSTER_SYNC_DIGEST_LOGGER);
      state.syncRemoteTask =
          multiClusterExecutorManager.getRemoteSyncLeaderExecutor().execute(slot.getId(), task);
      return;
    }

    if (syncRemoteTask.isFinished()) {
      state.completeSyncRemoteLeaderTask();
    } else {
      if (System.currentTimeMillis() - syncRemoteTask.getCreateTime() > 5000) {
        // the sync leader is running more than 5secs, print
        MULTI_CLUSTER_CLIENT_LOGGER.info(
            "remoteDataCenter={}, slotId={}, sync-leader running, {}",
            remoteDataCenter,
            slot.getId(),
            syncRemoteTask);
      }
    }
  }

  private boolean isLeader(String leader) {
    return ServerEnv.isLocalServer(leader);
  }
}
