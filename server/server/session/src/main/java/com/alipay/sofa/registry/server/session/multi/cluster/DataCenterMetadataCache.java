/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.multi.cluster;

import com.alipay.sofa.registry.common.model.multi.cluster.RemoteSlotTableStatus;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : DataCenterMetadataCache.java, v 0.1 2022年07月19日 20:09 xiaojian.xj Exp $
 */
public interface DataCenterMetadataCache {

    /**
     * get zones of dataCenter
     * @param dataCenter
     * @return
     */
    Set<String> dataCenterZonesOf(String dataCenter);

    Map<String, Set<String>> dataCenterZonesOf(Set<String> dataCenters);

    boolean saveDataCenterZones(Map<String, RemoteSlotTableStatus> remoteSlotTableStatus);
}
