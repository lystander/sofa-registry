/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model;

/**
 *
 * @author xiaojian.xj
 * @version : SegmentPushInfo.java, v 0.1 2022年07月19日 14:11 xiaojian.xj Exp $
 */
public class SegmentPushInfo {

    private final String segment;

    private int dataCount;

    private String encode;

    private int encodeSize;

    public SegmentPushInfo(String segment) {
        this.segment = segment;
    }

    public SegmentPushInfo(String segment, int dataCount) {
        this.segment = segment;
        this.dataCount = dataCount;
    }

    public SegmentPushInfo(String segment, int dataCount, String encode, int encodeSize) {
        this.segment = segment;
        this.dataCount = dataCount;
        this.encode = encode;
        this.encodeSize = encodeSize;
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
     * Getter method for property <tt>dataCount</tt>.
     *
     * @return property value of dataCount
     */
    public int getDataCount() {
        return dataCount;
    }

    /**
     * Setter method for property <tt>dataCount</tt>.
     *
     * @param dataCount value to be assigned to property dataCount
     */
    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    /**
     * Getter method for property <tt>encode</tt>.
     *
     * @return property value of encode
     */
    public String getEncode() {
        return encode;
    }

    /**
     * Setter method for property <tt>encode</tt>.
     *
     * @param encode value to be assigned to property encode
     */
    public void setEncode(String encode) {
        this.encode = encode;
    }

    /**
     * Getter method for property <tt>encodeSize</tt>.
     *
     * @return property value of encodeSize
     */
    public int getEncodeSize() {
        return encodeSize;
    }

    /**
     * Setter method for property <tt>encodeSize</tt>.
     *
     * @param encodeSize value to be assigned to property encodeSize
     */
    public void setEncodeSize(int encodeSize) {
        this.encodeSize = encodeSize;
    }
}
