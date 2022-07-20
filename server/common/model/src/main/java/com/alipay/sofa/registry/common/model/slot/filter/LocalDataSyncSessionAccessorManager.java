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

import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.google.common.collect.Sets;

import java.util.Collections;

/**
 * @author xiaojian.xj
 * @version : LocalDataSyncSessionAccessorManager.java, v 0.1 2022年05月24日 21:56 xiaojian.xj Exp $
 */
public class LocalDataSyncSessionAccessorManager extends BaseSyncSlotAcceptorManager {

  // todo xiaojian.xj  accept = all, filter = metadata
  private static final SyncSlotAcceptor groupAcceptor =
      new SyncServiceGroupAcceptor(
              Collections.singleton());

  public LocalDataSyncSessionAccessorManager() {
    // only group acceptor
    super(Collections.singleton(groupAcceptor));
  }

}
