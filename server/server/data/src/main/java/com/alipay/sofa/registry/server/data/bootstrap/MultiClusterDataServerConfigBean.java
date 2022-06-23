/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.bootstrap;

import com.alipay.sofa.registry.util.OsUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterDataServerConfigBean.java, v 0.1 2022年05月09日 17:37 xiaojian.xj Exp $
 */
@ConfigurationProperties(prefix = MultiClusterDataServerConfigBean.PREFIX)
public class MultiClusterDataServerConfigBean implements MultiClusterDataServerConfig {

    public static final String PREFIX = "data.remote.server";

    private volatile int syncRemoteSlotLeaderIntervalSecs = 6;

    private volatile int syncRemoteSlotLeaderTimeoutMillis = 3000;

    private volatile int syncRemoteSlotLeaderPort = 9627;

    private volatile int syncRemoteSlotLeaderConnNum = 3;

    private volatile int remoteSyncSlotLeaderExecutorThreadSize = OsUtils.getCpuCount() * 3;

    private volatile int remoteSyncSlotLeaderExecutorQueueSize = 100;

    private volatile int syncAppRevisionExecutorThreadSize = OsUtils.getCpuCount() * 3;

    private volatile int syncAppRevisionExecutorQueueSize = 100;


    private volatile int syncServiceMappingExecutorThreadSize = OsUtils.getCpuCount() * 3;

    private volatile int syncServiceMappingExecutorQueueSize = 100;

    private volatile int syncSlotLowWaterMark = 1024 * 256;
    private volatile int syncSlotHighWaterMark = 1024 * 320;

    private volatile int remoteSlotSyncRequestExecutorMinPoolSize = OsUtils.getCpuCount() * 3;

    private volatile int remoteSlotSyncRequestExecutorMaxPoolSize = OsUtils.getCpuCount() * 5;

    private volatile int remoteSlotSyncRequestExecutorQueueSize = 1000;

    @Override
    public int getSyncRemoteSlotLeaderIntervalSecs() {
        return syncRemoteSlotLeaderIntervalSecs;
    }

    @Override
    public int getSyncRemoteSlotLeaderTimeoutMillis() {
        return syncRemoteSlotLeaderTimeoutMillis;
    }

    @Override
    public int getSyncRemoteSlotLeaderPort() {
        return syncRemoteSlotLeaderPort;
    }

    @Override
    public int getSyncRemoteSlotLeaderConnNum() {
        return syncRemoteSlotLeaderConnNum;
    }

    @Override
    public int getRemoteSyncSlotLeaderExecutorThreadSize() {
        return remoteSyncSlotLeaderExecutorThreadSize;
    }

    @Override
    public int getRemoteSyncSlotLeaderExecutorQueueSize() {
        return remoteSyncSlotLeaderExecutorQueueSize;
    }

    /**
     * Setter method for property <tt>syncRemoteSlotLeaderIntervalSecs</tt>.
     *
     * @param syncRemoteSlotLeaderIntervalSecs value to be assigned to property syncRemoteSlotLeaderIntervalSecs
     */
    public void setSyncRemoteSlotLeaderIntervalSecs(int syncRemoteSlotLeaderIntervalSecs) {
        this.syncRemoteSlotLeaderIntervalSecs = syncRemoteSlotLeaderIntervalSecs;
    }

    /**
     * Setter method for property <tt>syncRemoteSlotLeaderTimeoutMillis</tt>.
     *
     * @param syncRemoteSlotLeaderTimeoutMillis value to be assigned to property syncRemoteSlotLeaderTimeoutMillis
     */
    public void setSyncRemoteSlotLeaderTimeoutMillis(int syncRemoteSlotLeaderTimeoutMillis) {
        this.syncRemoteSlotLeaderTimeoutMillis = syncRemoteSlotLeaderTimeoutMillis;
    }

    /**
     * Setter method for property <tt>syncRemoteSlotLeaderPort</tt>.
     *
     * @param syncRemoteSlotLeaderPort value to be assigned to property syncRemoteSlotLeaderPort
     */
    public void setSyncRemoteSlotLeaderPort(int syncRemoteSlotLeaderPort) {
        this.syncRemoteSlotLeaderPort = syncRemoteSlotLeaderPort;
    }

    /**
     * Setter method for property <tt>syncRemoteSlotLeaderConnNum</tt>.
     *
     * @param syncRemoteSlotLeaderConnNum value to be assigned to property syncRemoteSlotLeaderConnNum
     */
    public void setSyncRemoteSlotLeaderConnNum(int syncRemoteSlotLeaderConnNum) {
        this.syncRemoteSlotLeaderConnNum = syncRemoteSlotLeaderConnNum;
    }

    /**
     * Setter method for property <tt>remoteSyncSlotLeaderExecutorThreadSize</tt>.
     *
     * @param remoteSyncSlotLeaderExecutorThreadSize value to be assigned to property remoteSyncSlotLeaderExecutorThreadSize
     */
    public void setRemoteSyncSlotLeaderExecutorThreadSize(int remoteSyncSlotLeaderExecutorThreadSize) {
        this.remoteSyncSlotLeaderExecutorThreadSize = remoteSyncSlotLeaderExecutorThreadSize;
    }

    /**
     * Setter method for property <tt>remoteSyncSlotLeaderExecutorQueueSize</tt>.
     *
     * @param remoteSyncSlotLeaderExecutorQueueSize value to be assigned to property remoteSyncSlotLeaderExecutorQueueSize
     */
    public void setRemoteSyncSlotLeaderExecutorQueueSize(int remoteSyncSlotLeaderExecutorQueueSize) {
        this.remoteSyncSlotLeaderExecutorQueueSize = remoteSyncSlotLeaderExecutorQueueSize;
    }

    /**
     * Getter method for property <tt>syncSlotLowWaterMark</tt>.
     *
     * @return property value of syncSlotLowWaterMark
     */
    @Override
    public int getSyncSlotLowWaterMark() {
        return syncSlotLowWaterMark;
    }

    /**
     * Setter method for property <tt>syncSlotLowWaterMark</tt>.
     *
     * @param syncSlotLowWaterMark value to be assigned to property syncSlotLowWaterMark
     */
    public void setSyncSlotLowWaterMark(int syncSlotLowWaterMark) {
        this.syncSlotLowWaterMark = syncSlotLowWaterMark;
    }

    /**
     * Getter method for property <tt>syncSlotHighWaterMark</tt>.
     *
     * @return property value of syncSlotHighWaterMark
     */
    @Override
    public int getSyncSlotHighWaterMark() {
        return syncSlotHighWaterMark;
    }

    @Override
    public int getRemoteSlotSyncRequestExecutorMinPoolSize() {
        return remoteSlotSyncRequestExecutorMinPoolSize;
    }

    @Override
    public int getRemoteSlotSyncRequestExecutorMaxPoolSize() {
        return remoteSlotSyncRequestExecutorMaxPoolSize;
    }

    @Override
    public int getRemoteSlotSyncRequestExecutorQueueSize() {
        return remoteSlotSyncRequestExecutorQueueSize;
    }

    @Override
    public int getSyncAppRevisionExecutorThreadSize() {
        return syncAppRevisionExecutorThreadSize;
    }

    @Override
    public int getSyncAppRevisionExecutorQueueSize() {
        return syncAppRevisionExecutorQueueSize;
    }

    @Override
    public int getSyncServiceMappingExecutorThreadSize() {
        return syncServiceMappingExecutorThreadSize;
    }

    @Override
    public int getSyncServiceMappingExecutorQueueSize() {
        return syncServiceMappingExecutorQueueSize;
    }

    /**
     * Setter method for property <tt>syncSlotHighWaterMark</tt>.
     *
     * @param syncSlotHighWaterMark value to be assigned to property syncSlotHighWaterMark
     */
    public void setSyncSlotHighWaterMark(int syncSlotHighWaterMark) {
        this.syncSlotHighWaterMark = syncSlotHighWaterMark;
    }
}
