/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.dataserver.handler;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.loggers.Loggers;
import com.alipay.sofa.registry.server.data.remoting.dataserver.handler.BaseSlotDiffPublisherRequestHandler;
import com.alipay.sofa.registry.server.data.remoting.dataserver.handler.SlotFollowerDiffPublisherRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSlotDiffPublisherRequestHandler.java, v 0.1 2022年05月16日 21:51 xiaojian.xj Exp $
 */
public class MultiClusterSlotDiffPublisherRequestHandler extends BaseSlotDiffPublisherRequestHandler {

    private static final Logger LOGGER = Loggers.MULTI_CLUSTER_SRV_LOGGER;

    @Autowired
    private MultiClusterExecutorManager multiClusterExecutorManager;

    public MultiClusterSlotDiffPublisherRequestHandler() {
        super(LOGGER);
    }

    /**
     * specify executor for processor handler
     *
     * @return
     */
    @Override
    public Executor getExecutor() {
        return multiClusterExecutorManager.getRemoteSlotSyncProcessorExecutor();
    }

}
