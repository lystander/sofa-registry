/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;


/**
 *
 * @author xiaojian.xj
 * @version : LocalSyncSlotAccessorManager.java, v 0.1 2022年05月24日 21:37 xiaojian.xj Exp $
 */
public class LocalSyncDataAccessorManager extends BaseSyncSlotAcceptorManager {

    public LocalSyncDataAccessorManager() {
        super(Sets.newConcurrentHashSet());
    }
}
