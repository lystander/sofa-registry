/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.meta.multi.cluster;

import com.alipay.sofa.registry.common.model.GenericResponse;
import com.alipay.sofa.registry.common.model.elector.LeaderInfo;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.exchange.message.Response;
import com.alipay.sofa.registry.server.meta.MetaLeaderService;
import com.alipay.sofa.registry.server.meta.bootstrap.ExecutorManager;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MultiClusterMetaServerConfig;
import com.alipay.sofa.registry.server.meta.multi.cluster.remote.RemoteClusterMetaExchanger;
import com.alipay.sofa.registry.server.meta.multi.cluster.remote.RemoteClusterSlotSyncRequest;
import com.alipay.sofa.registry.server.meta.multi.cluster.remote.RemoteClusterSlotSyncResponse;
import com.alipay.sofa.registry.task.KeyedTask;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.StringFormatter;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSlotTableSyncer.java, v 0.1 2022年04月15日 16:46 xiaojian.xj Exp $
 */
public class DefaultMultiClusterSlotTableSyncer implements MultiClusterSlotTableSyncer {

  private static final Logger LOGGER =
      LoggerFactory.getLogger("MULTI-CLUSTER", "[SlotTableSyncer]");

  private Map<String, RemoteClusterSlotState> slotStateMap = Maps.newConcurrentMap();

  private SlotTableWatcher watcher = new SlotTableWatcher();

  @Autowired private MetaLeaderService metaLeaderService;

  @Autowired private MultiClusterMetaServerConfig multiClusterMetaServerConfig;

  @Autowired private RemoteClusterMetaExchanger remoteClusterMetaExchanger;

  @Autowired private ExecutorManager executorManager;

  private KeyedThreadPoolExecutor remoteSlotSyncerExecutor;

  private static volatile long LAST_REFRESH_CONFIG_TS = 0;

  private static final long INIT_SLOT_TABLE_EPOCH = -1l;

  static final int MAX_SYNC_FAIL_COUNT = 3;

  @PostConstruct
  public void init() {
    remoteClusterMetaExchanger.refreshClusterInfos();
    ConcurrentUtils.createDaemonThread("multi_cluster_slot_table", watcher).start();
    metaLeaderService.registerListener(this);

    remoteSlotSyncerExecutor =
        new KeyedThreadPoolExecutor(
            "REMOTE_SLOT_SYNCER_EXECUTOR",
                multiClusterMetaServerConfig.getRemoteSlotSyncerExecutorPoolSize(),
                multiClusterMetaServerConfig.getRemoteSlotSyncerExecutorQueueSize());
  }

  @Override
  public void becomeLeader() {}

  @Override
  public void loseLeader() {}

  private final class SlotTableWatcher extends WakeUpLoopRunnable {

    @Override
    public void runUnthrowable() {
      try {
        //if need reload sync info from db
        if (needReloadConfig()) {
          executorManager
              .getMultiClusterConfigReloadExecutor()
              .execute(() -> remoteClusterMetaExchanger.refreshClusterInfos());
          LAST_REFRESH_CONFIG_TS = System.currentTimeMillis();
        }
      } catch (Throwable t) {
        LOGGER.error("refresh multi cluster config error.", t);
      }

      Set<String> remoteClusters = remoteClusterMetaExchanger.getAllRemoteClusters();
      SetView<String> removes = Sets.difference(slotStateMap.keySet(), remoteClusters);
      for (String remove : removes) {
        RemoteClusterSlotState state = slotStateMap.remove(remove);
        LOGGER.info("remove dataCenter: {} sync info: {}.", remove, state);
      }

      for (String dataCenter : remoteClusters) {
        RemoteClusterSlotState slotState =
            slotStateMap.computeIfAbsent(dataCenter, k -> new RemoteClusterSlotState(dataCenter));

        // check if exceed max fail count
        if (checkSyncFailCount(slotState.getFailCount())) {
          remoteSlotSyncerExecutor.execute(dataCenter, () -> {
            slotState.initFailCount();
            remoteClusterMetaExchanger.resetLeader(dataCenter);
          });
          continue;
        }

        // check if need to do sync
        if (needSync(slotState.task)) {
          SlotSyncTask syncTask =
              new SlotSyncTask(
                  slotState.dataCenter,
                  slotState.slotTable == null
                      ? INIT_SLOT_TABLE_EPOCH
                      : slotState.slotTable.getEpoch());
          slotState.task = remoteSlotSyncerExecutor.execute(dataCenter, syncTask);
        }
      }
    }

    private boolean checkSyncFailCount(long failCount) {
      if (failCount >= MAX_SYNC_FAIL_COUNT) {
        LOGGER.error(
                "sync failed [{}] times, prepare to reset leader from rest api.",
                failCount);
        return true;
      }
      return false;
    }

    @Override
    public int getWaitingMillis() {
      return 200;
    }
  }

  /** @return */
  private boolean needReloadConfig() {
    return System.currentTimeMillis() - LAST_REFRESH_CONFIG_TS
        > multiClusterMetaServerConfig.getMultiClusterConfigReloadMillis();
  }

  /**
   * need sync slot table from remote cluster
   *
   * @param task
   * @return
   */
  private boolean needSync(KeyedTask<SlotSyncTask> task) {
    return task == null || task.isOverAfter(multiClusterMetaServerConfig.getRemoteSlotSyncerMillis());
  }

  private static final class RemoteClusterSlotState {
    final String dataCenter;

    volatile SlotTable slotTable;

    volatile KeyedTask<SlotSyncTask> task;

    final AtomicLong failCount = new AtomicLong(0);

    public RemoteClusterSlotState(String dataCenter) {
      this.dataCenter = dataCenter;
    }

    public long incrementAndGetFailCount() {
      return failCount.incrementAndGet();
    }

    public void initFailCount() {
      failCount.set(0);
    }

    public long getFailCount() {
      return failCount.get();
    }

    @Override
    public String toString() {
      return "RemoteClusterSlotState{"
          + "dataCenter='"
          + dataCenter
          + '\''
          + ", slotTable="
          + slotTable
          + ", task="
          + task
          + '}';
    }
  }

  private final class SlotSyncTask implements Runnable {
    final long startTimestamp = System.currentTimeMillis();

    final String dataCenter;

    final long slotTableEpoch;

    public SlotSyncTask(String dataCenter, long slotTableEpoch) {
      this.dataCenter = dataCenter;
      this.slotTableEpoch = slotTableEpoch;
    }

    @Override
    public void run() {
      boolean success = false;
      RemoteClusterSlotSyncRequest request =
          new RemoteClusterSlotSyncRequest(dataCenter, slotTableEpoch);
      try {
        Response response = remoteClusterMetaExchanger.sendRequest(dataCenter, request);

        // learn latest meta leader and slot table
        handleSyncResponse(request, response);
        success = true;
      } catch (Throwable t) {
        handleSyncFail(request, t);
      } finally {
        LOGGER.info(
            "{},{},{},span={}",
            success ? 'Y' : 'N',
            dataCenter,
            slotTableEpoch,
            System.currentTimeMillis() - startTimestamp);
      }
    }
  }

  private void handleSyncFail(RemoteClusterSlotSyncRequest request, Throwable t) {
    RemoteClusterSlotState state = slotStateMap.get(request.getDataCenter());
    state.incrementAndGetFailCount();
    LOGGER.error("[syncRemoteMeta]sync request: {} error.", request, t);
  }

  private void handleSyncResponse(RemoteClusterSlotSyncRequest request, Response response) {
    RemoteClusterSlotState state = slotStateMap.get(request.getDataCenter());
    if (!(response instanceof GenericResponse)) {
      state.incrementAndGetFailCount();
      throw new RuntimeException(
          String.format("sync request: %s fail, resp: %s", request, response));
    }
    GenericResponse<RemoteClusterSlotSyncResponse> syncRest =
        (GenericResponse<RemoteClusterSlotSyncResponse>) response.getResult();
    RemoteClusterSlotSyncResponse data = syncRest.getData();

    if (syncRest.isSuccess()) {
      remoteClusterMetaExchanger.learn(
          request.getDataCenter(), new LeaderInfo(data.getMetaLeaderEpoch(), data.getMetaLeader()));
      handleSyncResult(state, data);
      state.initFailCount();
    } else {
      if (data == null) {
        // could no get data, trigger the counter inc
        state.incrementAndGetFailCount();

        throw new RuntimeException(
            String.format("sync request: %s fail, resp.data is null, msg: %s", request, response));
      }
      // heartbeat on follow, refresh leader;
      // it will sync on leader next time;
      if (!data.isSyncOnLeader()) {
        remoteClusterMetaExchanger.learn(
            request.getDataCenter(),
            new LeaderInfo(data.getMetaLeaderEpoch(), data.getMetaLeader()));
        // refresh the leader from follower, but the info maybe is incorrect
        // throw the exception to trigger the counter inc
        // if the info is correct, the counter would be reset
        throw new RuntimeException(
            String.format(
                "sync dataCenter: %s on metaServer.follower, leader is: %s ",
                request.getDataCenter(),
                new LeaderInfo(data.getMetaLeaderEpoch(), data.getMetaLeader())));
      } else {
        throw new RuntimeException(
            StringFormatter.format(
                "sync dataCenter: %s on metaServer.leader error, msg={}, data={}", syncRest.getMessage(), data));
      }
    }
  }

  private void handleSyncResult(RemoteClusterSlotState state, RemoteClusterSlotSyncResponse data) {
    long epoch = state.slotTable.getEpoch();
    if (data.isSlotTableUpgrade() && data.getMetaLeaderEpoch() > epoch) {
      state.slotTable = data.getSlotTable();
      LOGGER.info(
          "slotTable update from {} to {}, data: {}",
          epoch,
          data.getMetaLeaderEpoch(),
          data.getSlotTable());
    }
  }
}
