/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.filter.slot;

import com.alipay.sofa.registry.common.model.slot.filter.SyncAcceptorRequest;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptor;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;

/**
 *
 * @author xiaojian.xj
 * @version : SyncSlotAcceptAllManager.java, v 0.1 2022年07月20日 20:06 xiaojian.xj Exp $
 */
public class SyncSlotAcceptAllManager implements SyncSlotAcceptorManager {

    @Override
    public void updateAcceptor(SyncSlotAcceptor acceptor) {}

    @Override
    public boolean accept(SyncAcceptorRequest request) {
        return true;
    }
}
