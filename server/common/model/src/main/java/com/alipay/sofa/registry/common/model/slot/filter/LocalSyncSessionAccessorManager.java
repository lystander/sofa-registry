/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.google.common.collect.Sets;
import javax.annotation.PostConstruct;

/**
 * @author xiaojian.xj
 * @version : LocalSyncSessionAccessorManager.java, v 0.1 2022年05月24日 21:56 xiaojian.xj Exp $
 */
public class LocalSyncSessionAccessorManager extends BaseSyncSlotAcceptorManager {

  // todo xiaojian.xj
  private static final SyncSlotAcceptor groupAcceptor =
      new SyncSlotGroupAcceptor(Sets.newHashSet(), Sets.newHashSet());

  public LocalSyncSessionAccessorManager() {
    super(Sets.newConcurrentHashSet());
  }

  @PostConstruct
  public void init() {
    register(groupAcceptor);
  }
}
