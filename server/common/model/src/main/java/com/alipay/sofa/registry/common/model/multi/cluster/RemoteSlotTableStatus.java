/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.multi.cluster;

import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.util.ParaCheckUtil;

import java.io.Serializable;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteSlotTableStatus.java, v 0.1 2022年05月06日 15:31 xiaojian.xj Exp $
 */
public class RemoteSlotTableStatus implements Serializable {
    private static final long serialVersionUID = 1023079253275268831L;

    /** slotTableEpoch */
    private final long slotTableEpoch;

    /** when data.slotTableEpoch > meta.slotTableEpoch, slotTableEpochConflict=true */
    private final boolean slotTableEpochConflict;

    /** if slot table upgrade */
    private final boolean slotTableUpgrade;

    /** slot table will be null if syncOnLeader=false or slotTableUpgrade=false */
    private final SlotTable slotTable;

    public RemoteSlotTableStatus(long slotTableEpoch, boolean slotTableUpgrade, boolean slotTableEpochConflict, SlotTable slotTable) {
        this.slotTableEpoch = slotTableEpoch;
        this.slotTableUpgrade = slotTableUpgrade;
        this.slotTableEpochConflict = slotTableEpochConflict;
        this.slotTable = slotTable;
    }

    public static RemoteSlotTableStatus conflict(SlotTable slotTable) {
        return new RemoteSlotTableStatus(slotTable.getEpoch(), false, true, slotTable);
    }

    public static RemoteSlotTableStatus notUpgrade(long slotTableEpoch) {
        return new RemoteSlotTableStatus(slotTableEpoch, false, false, null);
    }

    public static RemoteSlotTableStatus upgrade(SlotTable slotTable) {
        ParaCheckUtil.checkNotNull(slotTable, "slotTable");
        return new RemoteSlotTableStatus(slotTable.getEpoch(), true, false, slotTable);
    }

    /**
     * Getter method for property <tt>slotTableEpoch</tt>.
     *
     * @return property value of slotTableEpoch
     */
    public long getSlotTableEpoch() {
        return slotTableEpoch;
    }

    public boolean isSlotTableUpgrade() {
        return slotTableUpgrade;
    }

    public boolean isSlotTableEpochConflict() {
        return slotTableEpochConflict;
    }

    /**
     * Getter method for property <tt>slotTable</tt>.
     *
     * @return property value of slotTable
     */
    public SlotTable getSlotTable() {
        return slotTable;
    }

    @Override
    public String toString() {
        return "RemoteSlotTableStatus{" +
                "slotTableEpoch=" + slotTableEpoch +
                ", slotTableEpochConflict=" + slotTableEpochConflict +
                ", slotTableUpgrade=" + slotTableUpgrade +
                ", slotTable=" + slotTable +
                '}';
    }
}
