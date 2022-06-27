/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.dataserver.BatchRequest;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.Slot.Role;
import com.alipay.sofa.registry.common.model.slot.SlotAccessGenericResponse;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.func.MD5HashFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.common.model.store.UnPublisher;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.cache.DatumStorageDelegate;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.remoting.sessionserver.handler.BatchPutDataHandler;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.task.KeyedTask;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * @author xiaojian.xj
 * @version : MetadataSlotChangeListener.java, v 0.1 2022年05月31日 10:40 xiaojian.xj Exp $
 */
public abstract class MetadataSlotChangeListener<T extends MetadataVersion>
    implements SlotChangeListener {

  protected final Logger LOGGER =
      LoggerFactory.getLogger(
          "METADATA-LOAD", StringFormatter.format("[{}]", getClass().getSimpleName()));

  protected final Map<Integer, MetadataLoadState> slotLeaderStatus = Maps.newConcurrentMap();

  @Autowired protected DataServerConfig dataServerConfig;

  @Autowired protected DatumStorageDelegate datumStorageDelegate;

  @Autowired protected MultiClusterExecutorManager multiClusterExecutorManager;

  @Resource protected BatchPutDataHandler batchPutDataHandler;

  protected final SlotFunction slotFunction = SlotFunctionRegistry.getFunc();

  protected static final MD5HashFunction md5HashFunction = new MD5HashFunction();

  private final WatchDog watchDog = new WatchDog();

  @PostConstruct
  public void init() {
    ConcurrentUtils.createDaemonThread(getClass().getSimpleName() + "WatchDog", watchDog).start();
  }

  @Override
  public void onSlotAdd(
      String dataCenter, long slotTableEpoch, int slotId, long slotLeaderEpoch, Slot.Role role) {
    if (role != Role.Leader) {
      LOGGER.info("[onSlotAdd]skip dataCenter={}, slotId={}, role={}", dataCenter, slotId, role);
    }

    // reset state
    MetadataLoadState status =
        slotLeaderStatus.put(
            slotId, MetadataLoadState.initState(slotTableEpoch, slotId, slotLeaderEpoch));
    if (status == null) {
      watchDog.wakeup();
    }
  }

  @Override
  public void onSlotRemove(String dataCenter, int slotId, Role role) {
    slotLeaderStatus.remove(slotId);
    LOGGER.info("[onSlotRemove]skip dataCenter={}, slotId={}, role={}", dataCenter, slotId, role);
  }

  protected abstract int waitingMillis();

  protected abstract SyncSlotAcceptorManager getSlotAcceptorManager();

  private final class WatchDog extends WakeUpLoopRunnable {

    @Override
    public void runUnthrowable() {
      try {
        for (Entry<Integer, MetadataLoadState> stateEntry : slotLeaderStatus.entrySet()) {
          syncMetadata(stateEntry.getKey(), stateEntry.getValue());
        }
      } catch (Throwable throwable) {
        LOGGER.error("[syncMetadata]failed to do sync.", throwable);
      }
    }

    @Override
    public int getWaitingMillis() {
      return 200;
    }
  }

  protected void syncMetadata(int slotId, MetadataLoadState state) {
    ParaCheckUtil.checkNotNull(
        state, StringFormatter.format("slotId={} metadataLoadState", slotId));
    KeyedTask<MetadataSlotChangeListener.SyncMetadataTask> syncMetadataTask =
        state.getSyncMetadataTask();

    if (needReload(syncMetadataTask)) {
      // do reload
      SyncMetadataTask task =
          new SyncMetadataTask(state.getSlotTableEpoch(), slotId, state.getSlotLeaderEpoch());
      state.setSyncMetadataTask(getExecutor().execute(slotId, task));
      return;
    }

    if (syncMetadataTask.isFinished()) {
      state.completeSyncMetadataTask();
      return;
    }
    if (System.currentTimeMillis() - syncMetadataTask.getCreateTime() > 5000) {
      // the sync metadata is running more than 5secs, print
      LOGGER.info("slotId={}, sync-metadata running, {}", slotId, syncMetadataTask);
    }
  }

  private boolean needReload(
      KeyedTask<MetadataSlotChangeListener.SyncMetadataTask> syncMetadataTask) {
    if (syncMetadataTask == null || syncMetadataTask.isOverAfter(waitingMillis())) {
      return true;
    }
    return false;
  }

  protected abstract KeyedThreadPoolExecutor getExecutor();
  /**
   * @param summary
   * @param value
   * @return
   */
  protected abstract boolean needRePub(DatumSummary summary, T value);

  protected abstract String buildDataInfoId(String dataInfoId);

  protected abstract Map<String, T> queryMetadata(int slotId);

  public final class SyncMetadataTask implements Runnable {

    final long slotTableEpoch;

    final int slotId;

    final long slotLeaderEpoch;

    public SyncMetadataTask(long slotTableEpoch, int slotId, long slotLeaderEpoch) {
      this.slotTableEpoch = slotTableEpoch;
      this.slotId = slotId;
      this.slotLeaderEpoch = slotLeaderEpoch;
    }

    @Override
    public void run() {
      try {
        reload(slotTableEpoch, slotId, slotLeaderEpoch);
      } catch (Throwable throwable) {
        LOGGER.error("[SyncMetadataTask]reload slotId: {} error.", slotId, throwable);
      }
    }

    @Override
    public String toString() {
      return "SyncMetadataTask{" + "slotId=" + slotId + '}';
    }
  }

  private void reload(long slotTableEpoch, int slotId, long slotLeaderEpoch) {
    // query from datum storage
    Map<String, DatumSummary> datumSummary =
        datumStorageDelegate.getDatumSummary(
            dataServerConfig.getLocalDataCenter(), slotId, getSlotAcceptorManager());

    // query from repository
    Map<String, T> metadataSummary = queryMetadata(slotId);

    Set<T> toBeAdd = Sets.newHashSet();
    Set<String> toBeRemove = Sets.difference(datumSummary.keySet(), metadataSummary.keySet());

    for (Entry<String, T> metadataEntry : metadataSummary.entrySet()) {
      String dataInfoId = metadataEntry.getKey();
      DatumSummary summary = datumSummary.get(dataInfoId);
      if (summary == null || needRePub(summary, metadataEntry.getValue())) {
        toBeAdd.add(metadataEntry.getValue());
      }
    }

    // process unpub
    dealWithUnPub(datumSummary, toBeRemove, slotTableEpoch, slotId, slotLeaderEpoch);

    // process pub
    dealWithPub(toBeAdd, slotTableEpoch, slotId, slotLeaderEpoch);
  }

  protected abstract void dealWithPub(
      Set<T> toBeAdd, long slotTableEpoch, int slotId, long slotLeaderEpoch);

  protected void dealWithUnPub(
      Map<String, DatumSummary> datumSummary,
      Set<String> toBeRemove,
      long slotTableEpoch,
      int slotId,
      long slotLeaderEpoch) {
    if (CollectionUtils.isEmpty(toBeRemove)) {
      return;
    }

    try {
      List<Object> unPubs = Lists.newArrayList();
      for (String dataInfoId : toBeRemove) {
        DatumSummary summary = datumSummary.get(dataInfoId);
        if (CollectionUtils.isEmpty(summary.getPublisherVersions())) {
          // pubs had bean remove, dataInfoIds will not delete.
          continue;
        }
        if (summary.size() > 1) {
          LOGGER.error(
              "[dealWithUnPub]unexpect summary size error: {},{}",
              summary,
              summary.getPublisherVersions());
          continue;
        }
        Entry<String, RegisterVersion> exist =
            summary.getPublisherVersions().entrySet().stream().findFirst().get();
        unPubs.add(
            UnPublisher.of(dataInfoId, exist.getKey(), incRegisterVersion(exist.getValue())));
      }
      BatchRequest request = new BatchRequest(slotId, slotTableEpoch, slotLeaderEpoch, unPubs);
      SlotAccessGenericResponse<Object> response = batchPutDataHandler.handleRequest(request, null);
      if (!response.isSuccess()) {
        LOGGER.error(
            "[dealWithUnPub]put unPub:{} error, access:{}, msg:{}",
            unPubs,
            response.getSlotAccess(),
            response.getMessage());
      }
    } catch (Throwable throwable) {
      LOGGER.error(
          "[dealWithUnPub]put unPub error, toBeRemove:{}, slotTableEpoch:{}, slotId:{}, slotLeaderEpoch:{}",
          toBeRemove,
          slotTableEpoch,
          slotId,
          slotLeaderEpoch,
          throwable);
    }
  }

  // inc version to handle unPub
  private RegisterVersion incRegisterVersion(RegisterVersion exist) {
    return new RegisterVersion(exist.getVersion() + 1, exist.getRegisterTimestamp());
  }

  protected Publisher buildPub(
      String dataInfoId, long version, List<ServerDataBox> serverDataBoxes) {
    Publisher publisher = new Publisher();
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    publisher.setDataInfoId(dataInfoId);
    publisher.setInstanceId(dataInfo.getInstanceId());
    publisher.setGroup(dataInfo.getGroup());
    publisher.setDataId(dataInfo.getDataId());

    publisher.setRegisterId(getRegisterId(dataInfoId));
    publisher.setCell(dataServerConfig.getLocalDataCenter());
    publisher.setVersion(version);
    publisher.setDataList(serverDataBoxes);
    return publisher;
  }

  protected String getRegisterId(String dataInfoId) {
    return String.valueOf(md5HashFunction.hash(dataInfoId));
  }

  protected void pub2Datum(
      long slotTableEpoch, int slotId, long slotLeaderEpoch, List<Object> pubs) {
    BatchRequest request = new BatchRequest(slotId, slotTableEpoch, slotLeaderEpoch, pubs);
    SlotAccessGenericResponse<Object> response = batchPutDataHandler.handleRequest(request, null);
    if (!response.isSuccess()) {
      LOGGER.error(
          "[pub2Datum]put pub:{} error, access:{}, msg:{}",
          pubs,
          response.getSlotAccess(),
          response.getMessage());
    }
  }
}
