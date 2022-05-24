/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.slot;

import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.SlotAccess;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.multi.cluster.slot.MultiClusterSlotManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author xiaojian.xj
 * @version : SlotAccessorDelegate.java, v 0.1 2022年05月23日 20:43 xiaojian.xj Exp $
 */
public class SlotAccessorDelegate implements SlotAccessor {

    @Autowired
    private DataServerConfig dataServerConfig;

    @Autowired
    private SlotManager slotManager;

    @Autowired
    private MultiClusterSlotManager multiClusterSlotManager;

    private final SlotFunction slotFunction = SlotFunctionRegistry.getFunc();

    @Override
    public int slotOf(String dataInfoId) {
        return slotFunction.slotOf(dataInfoId);
    }

    @Override
    public Slot getSlot(String dataCenter, int slotId) {
        return accessOf(dataCenter).getSlot(dataCenter, slotId);
    }

    @Override
    public SlotAccess checkSlotAccess(String dataCenter, int slotId, long srcSlotEpoch, long srcLeaderEpoch) {
        return accessOf(dataCenter).checkSlotAccess(dataCenter, slotId, srcSlotEpoch, srcLeaderEpoch);
    }

    @Override
    public boolean isLeader(String dataCenter, int slotId) {
        return accessOf(dataCenter).isLeader(dataCenter, slotId);
    }

    @Override
    public boolean isFollower(String dataCenter, int slotId) {
        return accessOf(dataCenter).isFollower(dataCenter, slotId);
    }

    private SlotAccessor accessOf(String dataCenter) {
        return StringUtils.equalsIgnoreCase(dataServerConfig.getLocalDataCenter(), dataCenter) ? slotManager : multiClusterSlotManager;
    }
}
