/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.converter;

import com.alipay.sofa.registry.core.model.DataBox;
import com.alipay.sofa.registry.core.model.MultiSegmentData;
import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * @author xiaojian.xj
 * @version : SegmentDataCounter.java, v 0.1 2022年07月19日 10:42 xiaojian.xj Exp $
 */
public final class SegmentDataCounter {

    private final MultiSegmentData segmentData;

    private int dataCount;

    public SegmentDataCounter(MultiSegmentData segmentData) {
        this.segmentData = segmentData;
        this.dataCount = 0;
    }

    public void put(String zone, List<DataBox> datas) {
        if (datas == null) {
            datas = Lists.newArrayList();
        }
        this.segmentData.getUnzipData().put(zone, datas);
        this.dataCount += datas.size();
    }

    /**
     * Getter method for property <tt>segmentData</tt>.
     *
     * @return property value of segmentData
     */
    public MultiSegmentData getSegmentData() {
        return segmentData;
    }

    /**
     * Getter method for property <tt>dataCount</tt>.
     *
     * @return property value of dataCount
     */
    public int getDataCount() {
        return dataCount;
    }
}
