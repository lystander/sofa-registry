/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.store.api.meta;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;

import java.util.List;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSyncRepository.java, v 0.1 2022年04月13日 16:53 xiaojian.xj Exp $
 */
public interface MultiClusterSyncRepository {

    /**
     * save or update with cas
     * @param syncInfo
     * @return
     */
    boolean put(MultiClusterSyncInfo syncInfo);


    /** query MultiClusterSyncInfo */
    public List<MultiClusterSyncInfo> queryAll();

    /**
     * remove provideData
     *
     * @param remoteDataCenter
     * @param dataVersion
     */
    public int remove(String remoteDataCenter,
            long dataVersion);
}
