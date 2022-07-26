/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.multi.cluster;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : DataCenterMetadata.java, v 0.1 2022年07月21日 11:54 xiaojian.xj Exp $
 */
public class DataCenterMetadata implements Serializable {
    private static final long serialVersionUID = 5438256870784168278L;

    private final String dataCenter;

    private final boolean stopPush;

    private final Set<String> zones;

    public DataCenterMetadata(String dataCenter, boolean stopPush, Set<String> zones) {
        this.dataCenter = dataCenter;
        this.stopPush = stopPush;
        this.zones = zones;
    }

    public boolean isStopPush() {
        return stopPush;
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
     * Getter method for property <tt>zones</tt>.
     *
     * @return property value of zones
     */
    public Set<String> getZones() {
        return zones;
    }

    @Override
    public String toString() {
        return "DataCenterMetadata{" +
                "dataCenter='" + dataCenter + '\'' +
                ", stopPush=" + stopPush +
                ", zones=" + zones +
                '}';
    }
}
