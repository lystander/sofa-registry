/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.alipay.sofa.registry.common.model.PublishSource;
import com.alipay.sofa.registry.common.model.ServiceGroupType;
import com.alipay.sofa.registry.util.ParaCheckUtil;

/**
 *
 * @author xiaojian.xj
 * @version : SyncAcceptorRequest.java, v 0.1 2022年07月20日 17:33 xiaojian.xj Exp $
 */
public class SyncAcceptorRequest {

    private final String dataInfoId;

    private final ServiceGroupType serviceGroup;

    private final PublishSource source;

    private SyncAcceptorRequest(String dataInfoId, ServiceGroupType serviceGroup, PublishSource source) {
        this.dataInfoId = dataInfoId;
        this.serviceGroup = serviceGroup;
        this.source = source;
    }

    public static SyncAcceptorRequest buildRequest(String dataInfoId) {
        ParaCheckUtil.checkNotBlank(dataInfoId, "dataInfoId");
        return new SyncAcceptorRequest(dataInfoId, ServiceGroupType.of(dataInfoId), null);
    }
    public static SyncAcceptorRequest buildRequest(String dataInfoId, ServiceGroupType serviceGroup) {
        ParaCheckUtil.checkNotBlank(dataInfoId, "dataInfoId");
        ParaCheckUtil.checkNotNull(serviceGroup, "serviceGroup");
        return new SyncAcceptorRequest(dataInfoId, serviceGroup, null);
    }

    public static SyncAcceptorRequest buildRequest(PublishSource source) {
        ParaCheckUtil.checkNotNull(source, "publishSource");
        return new SyncAcceptorRequest(null, null, source);
    }

    /**
     * Getter method for property <tt>dataInfoId</tt>.
     *
     * @return property value of dataInfoId
     */
    public String getDataInfoId() {
        return dataInfoId;
    }

    /**
     * Getter method for property <tt>serviceGroup</tt>.
     *
     * @return property value of serviceGroup
     */
    public ServiceGroupType getServiceGroup() {
        return serviceGroup;
    }

    /**
     * Getter method for property <tt>source</tt>.
     *
     * @return property value of source
     */
    public PublishSource getSource() {
        return source;
    }
}
