/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.Slot.Role;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptor;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.cache.DatumStorageDelegate;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.MetadataSlotChangeListener.MetadataVersion;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.task.KeyedTask;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

  @Autowired private DatumStorageDelegate datumStorageDelegate;

  private final WatchDog watchDog = new WatchDog();

  @PostConstruct
  public void init() {
    ConcurrentUtils.createDaemonThread(getClass().getSimpleName() + "WatchDog", watchDog).start();
  }

  @Override
  public void onSlotAdd(String dataCenter, int slotId, Role role) {
    if (role != Role.Leader) {
      LOGGER.info("[onSlotAdd]skip dataCenter={}, slotId={}, role={}", dataCenter, slotId, role);
    }
    MetadataLoadState status =
        slotLeaderStatus.putIfAbsent(slotId, MetadataLoadState.initState(slotId));
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

  protected abstract List<SyncSlotAcceptor> getSyncSlotAcceptor();

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
    ParaCheckUtil.checkNotNull(state, StringFormatter.format("slotId={} metadataLoadState", slotId));
    KeyedTask<SyncMetadataTask> syncMetadataTask = state.getSyncMetadataTask();

    if (needReload(syncMetadataTask)) {
      //do reload
      reload(slotId, state);
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

  private void reload(int slotId, MetadataLoadState state) {

    Map<String, DatumSummary> datumSummary = datumStorageDelegate.getDatumSummary(dataServerConfig.getLocalDataCenter(), slotId);
    Map<String, MetadataVersion>

  }

  private boolean needReload(KeyedTask<SyncMetadataTask> syncMetadataTask) {
    if (syncMetadataTask == null || syncMetadataTask.isOverAfter(waitingMillis())) {
      return true;
    }
    return false;
  }

  protected static class MetadataVersion {

    final String dataInfoId;

    final long version;

    public MetadataVersion(String dataInfoId, long version) {
      this.dataInfoId = dataInfoId;
      this.version = version;
    }
  }
}
