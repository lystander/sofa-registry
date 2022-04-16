/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.jdbc.mapper;

import com.alipay.sofa.registry.jdbc.domain.MultiClusterSyncDomain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSyncMapper.java, v 0.1 2022年04月13日 15:00 xiaojian.xj Exp $
 */
public interface MultiClusterSyncMapper {

    /**
     * insert data
     *
     * @param data
     */
    public int save(MultiClusterSyncDomain data);

    /**
     * update with exceptVersion cas
     * @param data
     * @param exceptVersion
     * @return
     */
    public int update(
            @Param("data") MultiClusterSyncDomain data, @Param("exceptVersion") long exceptVersion);

    /**
     * query MultiClusterSyncInfo
     * @param dataCenter
     * @return
     */
    public List<MultiClusterSyncDomain> queryByCluster(@Param("dataCenter") String dataCenter);

    /**
     * query MultiClusterSyncInfo
     * @param dataCenter
     * @return
     */
    public MultiClusterSyncDomain query(@Param("dataCenter") String dataCenter, @Param("remoteDataCenter") String remoteDataCenter);

    /**
     * remove provideData
     *
     * @param dataCenter
     * @param remoteDataCenter
     * @param dataVersion
     */
    public int remove(
            @Param("dataCenter") String dataCenter,
            @Param("remoteDataCenter") String remoteDataCenter,
            @Param("dataVersion") long dataVersion);
}
