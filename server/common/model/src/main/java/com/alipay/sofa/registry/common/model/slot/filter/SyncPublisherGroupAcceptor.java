/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.alipay.sofa.registry.common.model.ServiceGroupType;
import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : SyncPublisherGroupAcceptor.java, v 0.1 2022年07月22日 17:40 xiaojian.xj Exp $
 */
public class SyncPublisherGroupAcceptor implements SyncSlotAcceptor {

    private final String NAME = MultiValueConstants.SYNC_PUBLISHER_GROUP_ACCEPTOR;

    private final Set<String> acceptGroups;

    public SyncPublisherGroupAcceptor(Set<String> acceptGroups) {
        this.acceptGroups = acceptGroups;
    }

    @Override
    public boolean accept(SyncAcceptorRequest request) {
        if (CollectionUtils.isEmpty(acceptGroups)) {
            return false;
        }
        DataInfo dataInfo = DataInfo.valueOf(request.getDataInfoId());
        return acceptGroups.contains(dataInfo.getGroup());
    }

    @Override
    public boolean filterOut(SyncAcceptorRequest request) {
        return false;
    }

    @Override
    public String name() {
        return NAME;
    }
}
