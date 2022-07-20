/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.converter;

import com.alipay.sofa.registry.core.model.DataBox;
import com.alipay.sofa.registry.core.model.MultiSegmentData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 *
 * @author xiaojian.xj
 * @version : LocalDataCenterPushData.java, v 0.1 2022年07月18日 18:57 xiaojian.xj Exp $
 */
public class LocalDataCenterPushData {

    private SegmentDataCounter localSegmentDatas;

    private Map<String, SegmentDataCounter> remoteSegmentDatas;

    public void from(Map<String, List<DataBox>> pushData, String localDataCenter,
                     Predicate<String> pushdataPredicate, Set<String> segmentZones) {

        SegmentDataCounter local = new SegmentDataCounter(new MultiSegmentData(localDataCenter));
        Map<String, SegmentDataCounter> remotes = Maps.newHashMap();

        for (String zone : segmentZones) {
            if (pushdataPredicate.test(zone)) {
                SegmentDataCounter counter = remotes.computeIfAbsent(zone, k -> new SegmentDataCounter(new MultiSegmentData(zone)));
                counter.put(zone, pushData.get(zone));
            } else {
                local.put(zone, pushData.get(zone));
            }
        }
        this.localSegmentDatas = local;
        this.remoteSegmentDatas = remotes;
        return;
    }

    /**
     * Getter method for property <tt>localSegmentDatas</tt>.
     *
     * @return property value of localSegmentDatas
     */
    public SegmentDataCounter getLocalSegmentDatas() {
        return localSegmentDatas;
    }

    /**
     * Getter method for property <tt>remoteSegmentDatas</tt>.
     *
     * @return property value of remoteSegmentDatas
     */
    public Map<String, SegmentDataCounter> getRemoteSegmentDatas() {
        return remoteSegmentDatas;
    }
}


