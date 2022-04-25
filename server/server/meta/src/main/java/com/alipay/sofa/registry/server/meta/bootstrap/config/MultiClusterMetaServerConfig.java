/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.bootstrap.config;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterMetaServerConfig.java, v 0.1 2022年04月21日 22:04 xiaojian.xj Exp $
 */
public interface MultiClusterMetaServerConfig {

    int getRemoteClusterRpcTimeoutMillis();

    int getRemoteMetaServerPort();

    int getRemoteSlotSyncerMillis();

    int getRemoteSlotSyncerExecutorQueueSize();

    int getRemoteSlotSyncerExecutorPoolSize();

    int getMultiClusterConfigReloadMillis();

    int getMultiClusterConfigReloadWorkerSize();

    int getMultiClusterConfigReloadMaxBufferSize();

    int getRemoteClusterHandlerCoreSize();

    int getRemoteClusterHandlerMaxSize();

    int getRemoteClusterHandlerMaxBufferSize();
}
