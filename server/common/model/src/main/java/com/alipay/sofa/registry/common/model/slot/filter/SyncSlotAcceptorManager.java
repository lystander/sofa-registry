/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model.slot.filter;

/**
 * @author xiaojian.xj
 * @version : SyncSlotAcceptorManager.java, v 0.1 2022年05月24日 21:39 xiaojian.xj Exp $
 */
public interface SyncSlotAcceptorManager {

  boolean accept(String dataInfoId);

  void register(SyncSlotAcceptor acceptor);

}
