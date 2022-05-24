/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.slot;

import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.SlotAccess;

/**
 *
 * @author xiaojian.xj
 * @version : SlotAccessor.java, v 0.1 2022年05月23日 16:31 xiaojian.xj Exp $
 */
public interface SlotAccessor {

    int slotOf(String dataInfoId);

    Slot getSlot(String dataCenter, int slotId);

    SlotAccess checkSlotAccess(String dataCenter, int slotId, long srcSlotEpoch, long srcLeaderEpoch);

    boolean isLeader(String dataCenter, int slotId);

    boolean isFollower(String dataCenter, int slotId);
}
