/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.task.KeyedTask;

/**
 *
 * @author xiaojian.xj
 * @version : MetadataLoadState.java, v 0.1 2022年05月31日 22:28 xiaojian.xj Exp $
 */
public final class MetadataLoadState {

    private final int slotId;

    private MetadataLoadStatus status = MetadataLoadStatus.INIT;

    private long lastSuccessSyncTime = -1L;

    private KeyedTask<SyncMetadataTask> syncMetadataTask;

    public MetadataLoadState(int slotId) {
        this.slotId = slotId;
    }

    public static MetadataLoadState initState(int slotId) {
        return new MetadataLoadState(slotId);
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
