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
 * @version : SyncSlotDataInfoIdAcceptor.java, v 0.1 2022年05月14日 14:48 xiaojian.xj Exp $
 */
public class SyncSlotDataInfoIdAcceptor implements SyncSlotAcceptor {

    private final String NAME = "SyncSlotDataInfoIdAcceptor";

    private static final String ACCEPT_ALL = "ACCEPT_ALL";
    private final Set<String> acceptDataInfoIds;

    public SyncSlotDataInfoIdAcceptor(Set<String> acceptDataInfoIds) {
        this.acceptDataInfoIds = acceptDataInfoIds;
    }

    @Override
    public boolean accept(String dataInfoId) {
        if (CollectionUtils.isEmpty(acceptDataInfoIds)) {
            return false;
        }

        return acceptDataInfoIds.contains(ACCEPT_ALL) || acceptDataInfoIds.contains(dataInfoId);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        SyncSlotDataInfoIdAcceptor that = (SyncSlotDataInfoIdAcceptor) o;
        return Objects.equal(NAME, that.NAME);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(NAME);
    }
}
