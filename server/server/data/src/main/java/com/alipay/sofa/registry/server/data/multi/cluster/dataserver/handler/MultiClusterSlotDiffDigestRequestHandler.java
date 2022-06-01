/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.dataserver.handler;

import com.alipay.sofa.registry.common.model.slot.DataSlotDiffDigestRequest;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.loggers.Loggers;
import com.alipay.sofa.registry.server.data.remoting.dataserver.handler.BaseSlotDiffDigestRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSlotDiffDigestRequestHandler.java, v 0.1 2022年05月16日 20:57 xiaojian.xj Exp $
 */
public class MultiClusterSlotDiffDigestRequestHandler extends BaseSlotDiffDigestRequestHandler {

    private static final Logger LOGGER = Loggers.MULTI_CLUSTER_SRV_LOGGER;

    @Autowired
    private MultiClusterExecutorManager multiClusterExecutorManager;

    public MultiClusterSlotDiffDigestRequestHandler() {
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

    @Override
    protected boolean preCheck(DataSlotDiffDigestRequest request) {
        if (!slotAccessor.isLeader(dataServerConfig.getLocalDataCenter(), request.getSlotId())) {
            LOGGER.warn("sync slot request from {}, not leader of {}", request.getLocalDataCenter(), request.getSlotId());
            return false;
        }

        // todo xiaojian.xj check slot status
        return true;
    }

    @Override
    protected boolean postCheck(DataSlotDiffDigestRequest request) {
        return false;
    }
}
