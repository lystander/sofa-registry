/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.Node.NodeType;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.meta.bootstrap.ExecutorManager;
import com.alipay.sofa.registry.server.meta.remoting.handler.BaseMetaServerHandler;
import com.alipay.sofa.registry.server.shared.remoting.AbstractServerHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteClusterSlotSyncHandler.java, v 0.1 2022年04月21日 21:54 xiaojian.xj Exp $
 */
public class RemoteClusterSlotSyncHandler extends AbstractServerHandler<RemoteClusterSlotSyncRequest> {

    @Autowired
    private ExecutorManager executorManager;

    @Override
    public Class interest() {
        return RemoteClusterSlotSyncRequest.class;
    }

    @Override
    protected NodeType getConnectNodeType() {
        return NodeType.META;
    }

    @Override
    public Object doHandle(Channel channel, RemoteClusterSlotSyncRequest request) {
        return null;
    }

    @Override
    public Executor getExecutor() {
        return executorManager.getRemoteClusterHandlerExecutor();
    }
}
