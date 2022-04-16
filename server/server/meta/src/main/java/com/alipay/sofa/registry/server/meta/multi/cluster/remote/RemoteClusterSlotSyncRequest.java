/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import java.io.Serializable;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteClusterSlotSyncRequest.java, v 0.1 2022年04月15日 21:29 xiaojian.xj Exp $
 */
public class RemoteClusterSlotSyncRequest implements Serializable {

    private static final long serialVersionUID = -7873925175337400773L;

    /** remote data center */
    private final String dataCenter;

    /** slot table epoch */
    private final long slotTableEpoch;

    public RemoteClusterSlotSyncRequest(String dataCenter, long slotTableEpoch) {
        this.dataCenter = dataCenter;
        this.slotTableEpoch = slotTableEpoch;
    }

    /**
     * Getter method for property <tt>dataCenter</tt>.
     *
     * @return property value of dataCenter
     */
    public String getDataCenter() {
        return dataCenter;
    }

    /**
     * Getter method for property <tt>slotTableEpoch</tt>.
     *
     * @return property value of slotTableEpoch
     */
    public long getSlotTableEpoch() {
        return slotTableEpoch;
    }

    @Override
    public String toString() {
        return "RemoteClusterSlotSyncRequest{" +
                "dataCenter='" + dataCenter + '\'' +
                ", slotTableEpoch=" + slotTableEpoch +
                '}';
    }
}
