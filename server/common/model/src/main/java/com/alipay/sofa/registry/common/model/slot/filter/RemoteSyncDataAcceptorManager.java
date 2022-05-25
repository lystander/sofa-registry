/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteSyncSlotAcceptorManager.java, v 0.1 2022年05月13日 20:04 xiaojian.xj Exp $
 */
public class RemoteSyncDataAcceptorManager extends BaseSyncSlotAcceptorManager {

  public RemoteSyncDataAcceptorManager() {
    super(Sets.newConcurrentHashSet());
  }
}
