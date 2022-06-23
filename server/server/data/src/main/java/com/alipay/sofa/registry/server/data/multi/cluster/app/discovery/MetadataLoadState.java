/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.MetadataSlotChangeListener.SyncMetadataTask;
import com.alipay.sofa.registry.task.KeyedTask;

/**
 *
 * @author xiaojian.xj
 * @version : MetadataLoadState.java, v 0.1 2022年05月31日 22:28 xiaojian.xj Exp $
 */
public final class MetadataLoadState {

    private final long slotTableEpoch;

    private final int slotId;

    private final long slotLeaderEpoch;

    private MetadataLoadStatus status = MetadataLoadStatus.INIT;

    private long lastSuccessSyncTime = -1L;

    private KeyedTask<SyncMetadataTask> syncMetadataTask;

    public MetadataLoadState(long slotTableEpoch, int slotId, long slotLeaderEpoch) {
        this.slotId = slotId;
        this.slotTableEpoch = slotTableEpoch;
        this.slotLeaderEpoch = slotLeaderEpoch;
    }

    public static MetadataLoadState initState(long slotTableEpoch, int slotId,  long slotLeaderEpoch) {
        return new MetadataLoadState(slotTableEpoch, slotId, slotLeaderEpoch);
    }
    /**
     * Getter method for property <tt>slotId</tt>.
     *
     * @return property value of slotId
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Getter method for property <tt>status</tt>.
     *
     * @return property value of status
     */
    public MetadataLoadStatus getStatus() {
        return status;
    }

    /**
     * Setter method for property <tt>status</tt>.
     *
     * @param status value to be assigned to property status
     */
    // todo xiaojian.xj
    public void setStatus(MetadataLoadStatus status) {
        this.status = status;
    }

    /**
     * Getter method for property <tt>lastSuccessSyncTime</tt>.
     *
     * @return property value of lastSuccessSyncTime
     */
    public long getLastSuccessSyncTime() {
        return lastSuccessSyncTime;
    }

    /**
     * Setter method for property <tt>lastSuccessSyncTime</tt>.
     *
     * @param lastSuccessSyncTime value to be assigned to property lastSuccessSyncTime
     */
    public void setLastSuccessSyncTime(long lastSuccessSyncTime) {
        this.lastSuccessSyncTime = lastSuccessSyncTime;
    }

    /**
     * Getter method for property <tt>slotTableEpoch</tt>.
     *
     * @return property value of slotTableEpoch
     */
    public long getSlotTableEpoch() {
        return slotTableEpoch;
    }

    /**
     * Getter method for property <tt>slotLeaderEpoch</tt>.
     *
     * @return property value of slotLeaderEpoch
     */
    public long getSlotLeaderEpoch() {
        return slotLeaderEpoch;
    }

    /**
     * Getter method for property <tt>syncMetadataTask</tt>.
     *
     * @return property value of syncMetadataTask
     */
    public KeyedTask<SyncMetadataTask> getSyncMetadataTask() {
        return syncMetadataTask;
    }

    /**
     * Setter method for property <tt>syncMetadataTask</tt>.
     *
     * @param syncMetadataTask value to be assigned to property syncMetadataTask
     */
    public void setSyncMetadataTask(
            KeyedTask<SyncMetadataTask> syncMetadataTask) {
        this.syncMetadataTask = syncMetadataTask;
    }

    public void completeSyncMetadataTask() {
        if (syncMetadataTask != null && syncMetadataTask.isSuccess()) {
            this.lastSuccessSyncTime = syncMetadataTask.getEndTime();
        }
    }
}
