/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.slot;

import com.alipay.sofa.registry.common.model.multi.cluster.RemoteSlotTableStatus;
import com.alipay.sofa.registry.server.data.slot.SlotAccessor;

import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSlotManager.java, v 0.1 2022年05月06日 15:04 xiaojian.xj Exp $
 */
public interface MultiClusterSlotManager extends SlotAccessor {

    /**
     * get remote cluster slotTable epoch
     * @return map<cluster, slotTableEpoch>
     */
    public Map<String, Long> getSlotTableEpoch();

    /**
     * update remote slot table
     * @param remoteSlotTableStatus
     */
    void updateSlotTable(Map<String, RemoteSlotTableStatus> remoteSlotTableStatus);
}
