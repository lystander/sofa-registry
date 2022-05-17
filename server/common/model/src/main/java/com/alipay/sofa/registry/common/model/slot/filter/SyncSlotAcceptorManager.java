/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : SyncSlotFilterManager.java, v 0.1 2022年05月13日 20:04 xiaojian.xj Exp $
 */
public class SyncSlotAcceptorManager {

  private final Set<SyncSlotAcceptor> acceptors;

  public SyncSlotAcceptorManager(Set<SyncSlotAcceptor> acceptors) {
    this.acceptors = acceptors;
  }

  public boolean accept(String dataInfoId) {

    for (SyncSlotAcceptor acceptor : Optional.ofNullable(acceptors).orElse(Collections.emptySet())) {
      if (acceptor.accept(dataInfoId)) {
        return true;
      }
    }
    return false;
  }

  // todo xiaojian.xj
  public void register(SyncSlotAcceptor acceptor) {
    acceptors.add(acceptor);
  }

  /**
   * Getter method for property <tt>acceptors</tt>.
   *
   * @return property value of acceptors
   */
  public Set<SyncSlotAcceptor> getAcceptors() {
    return Sets.newConcurrentHashSet(acceptors);
  }
}
