/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.bootstrap;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterDataServerConfig.java, v 0.1 2022年05月09日 17:44 xiaojian.xj Exp $
 */
public interface MultiClusterDataServerConfig {

    int getSyncRemoteSlotLeaderIntervalSecs();

    int getSyncRemoteSlotLeaderTimeoutMillis();

    int getSyncRemoteSlotLeaderPort();

    int getSyncRemoteSlotLeaderConnNum();

    int getRemoteSyncSlotLeaderExecutorThreadSize();

    int getRemoteSyncSlotLeaderExecutorQueueSize();

    int getSyncSlotLowWaterMark();

    int getSyncSlotHighWaterMark();

    int getRemoteSlotSyncRequestExecutorMinPoolSize();

    int getRemoteSlotSyncRequestExecutorMaxPoolSize();

    int getRemoteSlotSyncRequestExecutorQueueSize();

    int getSyncAppRevisionExecutorThreadSize();

    int getSyncAppRevisionExecutorQueueSize();

    int getSyncServiceMappingExecutorThreadSize();

    int getSyncServiceMappingExecutorQueueSize();
}
