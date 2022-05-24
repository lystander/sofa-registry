/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.remoting.dataserver.handler;

import com.alipay.sofa.registry.common.model.GenericResponse;
import com.alipay.sofa.registry.common.model.Node;
import com.alipay.sofa.registry.common.model.dataserver.DatumDigest;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffDigestRequest;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffDigestRequest.SlotDiffAcceptType;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffDigestResult;
import com.alipay.sofa.registry.common.model.slot.DataSlotDiffUtils;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.exception.UnSupportOperationException;
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
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : BaseSlotDiffDigestRequestHandler.java, v 0.1 2022年05月14日 20:50 xiaojian.xj Exp $
 */
public abstract class BaseSlotDiffDigestRequestHandler extends AbstractServerHandler<DataSlotDiffDigestRequest> {

    private final Logger logger;

    @Resource
    private DatumStorageDelegate datumStorageDelegate;

    @Autowired private SlotManager slotManager;

    @Autowired private SlotAccessor slotAccessor;

    @Autowired private DataServerConfig dataServerConfig;

    public BaseSlotDiffDigestRequestHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Object doHandle(Channel channel, DataSlotDiffDigestRequest request) {
        try {
            slotManager.triggerUpdateSlotTable(request.getSlotTableEpoch());
            final int slotId = request.getSlotId();
            if (!slotAccessor.isLeader(dataServerConfig.getLocalDataCenter(), slotId)) {
                logger.warn("sync slot request from {}, not leader of {}", request.getLocalDataCenter(), slotId);
                return new GenericResponse().fillFailed("not leader of " + slotId);
            }

            Map<String, Map<String, Publisher>> existingPublishers;
            if (request.getAcceptType() == SlotDiffAcceptType.ALL) {
                existingPublishers = datumStorageDelegate.getPublishers(dataServerConfig.getLocalDataCenter(),
                        request.getSlotId());
            } else if (request.getAcceptType() == SlotDiffAcceptType.ACCEPTORS) {
                existingPublishers = datumStorageDelegate.getPublishers(dataServerConfig.getLocalDataCenter(),
                        request.getSlotId(), request.getAcceptorManager());
            } else {
                throw new UnSupportOperationException("DiffSyncDigest unsupport request.slotDiffAcceptType: " + request.getAcceptType());
            }

            DataSlotDiffDigestResult result =
                    calcDiffResult(
                            slotId,
                            request.getDatumDigest(),
                            existingPublishers);
            result.setSlotTableEpoch(slotManager.getSlotTableEpoch());
            return new GenericResponse().fillSucceed(result);
        } catch (Throwable e) {
            String msg =
                    StringFormatter.format("DiffSyncDigest request from {} error for slot {},{}", request.getLocalDataCenter(), request.getLocalDataCenter(), request.getSlotId());
            logger.error(msg, e);
            return new GenericResponse().fillFailed(msg);
        }
    }

    private DataSlotDiffDigestResult calcDiffResult(
            int targetSlot,
            Map<String, DatumDigest> targetDigestMap,
            Map<String, Map<String, Publisher>> existingPublishers) {
        DataSlotDiffDigestResult result =
                DataSlotDiffUtils.diffDigestResult(targetDigestMap, existingPublishers);
        DataSlotDiffUtils.logDiffResult(result, targetSlot, logger);
        return result;
    }

    @Override
    protected Node.NodeType getConnectNodeType() {
        return Node.NodeType.DATA;
    }

    @Override
    public Class interest() {
        return DataSlotDiffDigestRequest.class;
    }

    @Override
    public void checkParam(DataSlotDiffDigestRequest request) {
        ParaCheckUtil.checkNonNegative(request.getSlotId(), "request.slotId");
        ParaCheckUtil.checkNotNull(request.getDatumDigest(), "request.datumDigest");
    }

    @Override
    public Object buildFailedResponse(String msg) {
        return new GenericResponse().fillFailed(msg);
    }
}
