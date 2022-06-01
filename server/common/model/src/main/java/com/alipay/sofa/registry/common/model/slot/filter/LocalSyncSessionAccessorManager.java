/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;

import javax.annotation.PostConstruct;

/**
 *
 * @author xiaojian.xj
 * @version : LocalSyncSessionAccessorManager.java, v 0.1 2022年05月24日 21:56 xiaojian.xj Exp $
 */
public class LocalSyncSessionAccessorManager extends BaseSyncSlotAcceptorManager {

    // todo xiaojian.xj
    private final static SyncSlotAcceptor groupAcceptor = new SyncSlotGroupAcceptor(Sets.newHashSet(), Sets.newHashSet());
    public LocalSyncSessionAccessorManager() {
        super(Sets.newConcurrentHashSet());
    }

    @PostConstruct
    public void init() {
        register(groupAcceptor);
    }
}
