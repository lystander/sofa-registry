/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.core.model;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : SegmentMetadata.java, v 0.1 2022年06月28日 10:26 xiaojian.xj Exp $
 */
public class SegmentMetadata {

    private final boolean localSegment;

    private final String segment;

    private final Set<String> zones;

    public SegmentMetadata(boolean localSegment, String segment, Set<String> zones) {
        this.localSegment = localSegment;
        this.segment = segment;
        this.zones = zones;
    }

    public boolean isLocalSegment() {
        return this.localSegment;
    }

    /**
     * Getter method for property <tt>segment</tt>.
     *
     * @return property value of segment
     */
    public String getSegment() {
        return segment;
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
        return "SegmentMetadata{" +
                "localSegment=" + localSegment +
                ", segment='" + segment + '\'' +
                ", zones=" + zones +
                '}';
    }
}
