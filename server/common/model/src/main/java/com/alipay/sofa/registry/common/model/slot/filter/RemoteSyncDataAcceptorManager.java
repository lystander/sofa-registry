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

import com.alipay.sofa.registry.common.model.console.MultiSegmentSyncSwitch;
import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteSyncSlotAcceptorManager.java, v 0.1 2022年05月13日 20:04 xiaojian.xj Exp $
 */
public class RemoteSyncDataAcceptorManager extends BaseSyncSlotAcceptorManager {

  public RemoteSyncDataAcceptorManager() {
    this(Sets.newConcurrentHashSet());

    //init
    updateAcceptor(MultiValueConstants.DATUM_SYNCER_SOURCE_FILTER);
    updateAcceptor(new SyncPublisherGroupAcceptor(Collections.EMPTY_SET));
    updateAcceptor(new SyncSlotDataInfoIdAcceptor(Collections.EMPTY_SET, Collections.EMPTY_SET));
  }

  public RemoteSyncDataAcceptorManager(Set<SyncSlotAcceptor> acceptors) {
    super(acceptors);
  }

  public synchronized void updateFrom(MultiSegmentSyncSwitch syncConfig) {
    // add or update service group acceptor
    this.updateAcceptor(new SyncPublisherGroupAcceptor(syncConfig.getSynPublisherGroups()));

    // add or update dataInfoId group acceptor
    this.updateAcceptor(
        new SyncSlotDataInfoIdAcceptor(
            syncConfig.getSyncDataInfoIds(), syncConfig.getIgnoreDataInfoIds()));
  }

  public Set<SyncSlotAcceptor> getAcceptors() {
    return acceptors;
  }

}
