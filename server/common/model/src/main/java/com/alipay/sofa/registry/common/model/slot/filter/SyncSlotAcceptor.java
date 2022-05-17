/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

/**
 *
 * @author xiaojian.xj
 * @version : SyncSlotAcceptor.java, v 0.1 2022年05月13日 19:51 xiaojian.xj Exp $
 */
public interface SyncSlotAcceptor {

    boolean accept(String dataInfoId);

    String name();
}
