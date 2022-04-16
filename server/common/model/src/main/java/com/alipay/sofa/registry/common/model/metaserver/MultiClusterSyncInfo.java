/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.metaserver;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSyncInfo.java, v 0.1 2022年04月13日 17:09 xiaojian.xj Exp $
 */
public class MultiClusterSyncInfo {

    /** local data center */
    private String dataCenter;

    /** sync remote data center */
    private String remoteDataCenter;

    /** remote meta address, use to get meta leader */
    private String remoteMetaAddress;

    /** data version */
    private long dataVersion;

    public MultiClusterSyncInfo() {
    }

    public MultiClusterSyncInfo(String remoteDataCenter, String remoteMetaAddress, long dataVersion) {
        this.remoteDataCenter = remoteDataCenter;
        this.remoteMetaAddress = remoteMetaAddress;
        this.dataVersion = dataVersion;
    }

    public MultiClusterSyncInfo(String dataCenter, String remoteDataCenter, String remoteMetaAddress, long dataVersion) {
        this.dataCenter = dataCenter;
        this.remoteDataCenter = remoteDataCenter;
        this.remoteMetaAddress = remoteMetaAddress;
        this.dataVersion = dataVersion;
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
     * Setter method for property <tt>dataCenter</tt>.
     *
     * @param dataCenter value to be assigned to property dataCenter
     */
    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    /**
     * Getter method for property <tt>remoteDataCenter</tt>.
     *
     * @return property value of remoteDataCenter
     */
    public String getRemoteDataCenter() {
        return remoteDataCenter;
    }

    /**
     * Setter method for property <tt>remoteDataCenter</tt>.
     *
     * @param remoteDataCenter value to be assigned to property remoteDataCenter
     */
    public void setRemoteDataCenter(String remoteDataCenter) {
        this.remoteDataCenter = remoteDataCenter;
    }

    /**
     * Getter method for property <tt>remoteMetaAddress</tt>.
     *
     * @return property value of remoteMetaAddress
     */
    public String getRemoteMetaAddress() {
        return remoteMetaAddress;
    }

    /**
     * Setter method for property <tt>remoteMetaAddress</tt>.
     *
     * @param remoteMetaAddress value to be assigned to property remoteMetaAddress
     */
    public void setRemoteMetaAddress(String remoteMetaAddress) {
        this.remoteMetaAddress = remoteMetaAddress;
    }

    /**
     * Getter method for property <tt>dataVersion</tt>.
     *
     * @return property value of dataVersion
     */
    public long getDataVersion() {
        return dataVersion;
    }

    /**
     * Setter method for property <tt>dataVersion</tt>.
     *
     * @param dataVersion value to be assigned to property dataVersion
     */
    public void setDataVersion(long dataVersion) {
        this.dataVersion = dataVersion;
    }

    @Override
    public String toString() {
        return "MultiClusterSyncInfo{" +
                "dataCenter='" + dataCenter + '\'' +
                ", remoteDataCenter='" + remoteDataCenter + '\'' +
                ", remoteMetaAddress='" + remoteMetaAddress + '\'' +
                ", dataVersion=" + dataVersion +
                '}';
    }
}
