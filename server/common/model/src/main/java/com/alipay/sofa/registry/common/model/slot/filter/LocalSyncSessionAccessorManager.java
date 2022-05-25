/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : LocalSyncSessionAccessorManager.java, v 0.1 2022年05月24日 21:56 xiaojian.xj Exp $
 */
public class LocalSyncSessionAccessorManager extends BaseSyncSlotAcceptorManager {

    public LocalSyncSessionAccessorManager() {
        super(Sets.newConcurrentHashSet());
    }
}
