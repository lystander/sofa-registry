/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.remoting.dataserver.handler;

import com.alipay.sofa.registry.common.model.GenericResponse;
import com.alipay.sofa.registry.common.model.Node;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffPublisherRequest;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffPublisherResult;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffUtils;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.cache.DatumStorageDelegate;
import com.alipay.sofa.registry.server.data.slot.SlotAccessor;
import com.alipay.sofa.registry.server.data.slot.SlotManager;
import com.alipay.sofa.registry.server.shared.remoting.AbstractServerHandler;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : BaseSlotDiffPublisherRequestHandler.java, v 0.1 2022年05月16日 21:16 xiaojian.xj Exp $
 */
public abstract class BaseSlotDiffPublisherRequestHandler
        extends AbstractServerHandler<DataSlotDiffPublisherRequest> {

    private final Logger logger;

    @Resource
    private DatumStorageDelegate datumStorageDelegate;

    @Autowired private DataServerConfig dataServerConfig;

    @Autowired private SlotManager slotManager;

    @Autowired private SlotAccessor slotAccessor;

    public BaseSlotDiffPublisherRequestHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void checkParam(DataSlotDiffPublisherRequest request) {
        ParaCheckUtil.checkNonNegative(request.getSlotId(), "request.slotId");
        ParaCheckUtil.checkNotNull(request.getDatumSummaries(), "request.datumSummaries");
    }

    @Override
    public Object doHandle(Channel channel, DataSlotDiffPublisherRequest request) {
        try {
            slotManager.triggerUpdateSlotTable(request.getSlotTableEpoch());
            final int slotId = request.getSlotId();
            if (!slotAccessor.isLeader(dataServerConfig.getLocalDataCenter(), slotId)) {
                logger.warn("sync slot request from {}, not leader of {}", request.getLocalDataCenter(), slotId);
                return new GenericResponse().fillFailed("not leader of " + slotId);
            }
            DataSlotDiffPublisherResult result =
                    calcDiffResult(
                            slotId,
                            request.getDatumSummaries(),
                            datumStorageDelegate.getPublishers(dataServerConfig.getLocalDataCenter(), request.getSlotId()));
            result.setSlotTableEpoch(slotManager.getSlotTableEpoch());
            return new GenericResponse().fillSucceed(result);
        } catch (Throwable e) {
            String msg =
                    StringFormatter.format(
                            "DiffSyncPublisher request from {} error for slot {}", request.getLocalDataCenter(), request.getSlotId());
            logger.error(msg, e);
            return new GenericResponse().fillFailed(msg);
        }
    }

    private DataSlotDiffPublisherResult calcDiffResult(
            int targetSlot,
            List<DatumSummary> datumSummaries,
            Map<String, Map<String, Publisher>> existingPublishers) {
        DataSlotDiffPublisherResult result =
                DataSlotDiffUtils.diffPublishersResult(
                        datumSummaries, existingPublishers, dataServerConfig.getSlotSyncPublisherMaxNum());
        DataSlotDiffUtils.logDiffResult(result, targetSlot, logger);
        return result;
    }

    @Override
    protected Node.NodeType getConnectNodeType() {
        return Node.NodeType.DATA;
    }

    @Override
    public Class interest() {
        return DataSlotDiffPublisherRequest.class;
    }

    @Override
    public Object buildFailedResponse(String msg) {
        return new GenericResponse().fillFailed(msg);
    }

}
