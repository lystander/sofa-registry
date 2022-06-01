/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.google.common.base.Objects;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : SyncSlotGroupAcceptor.java, v 0.1 2022年05月14日 14:48 xiaojian.xj Exp $
 */
public class SyncSlotGroupAcceptor implements SyncSlotAcceptor {

    private final String NAME = "SyncSlotGroupAcceptor";
    private final Set<String> acceptGroups;
    private final Set<String> filterGroups;


    public SyncSlotGroupAcceptor(Set<String> acceptGroups, Set<String> filterGroups) {
        this.acceptGroups = acceptGroups;
        this.filterGroups = filterGroups;
    }

    @Override
    public boolean accept(String dataInfoId) {
        DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
        return !CollectionUtils.isEmpty(acceptGroups) && acceptGroups.contains(dataInfo.getGroup());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        SyncSlotGroupAcceptor that = (SyncSlotGroupAcceptor) o;
        return Objects.equal(NAME, that.NAME);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(NAME);
    }
}
