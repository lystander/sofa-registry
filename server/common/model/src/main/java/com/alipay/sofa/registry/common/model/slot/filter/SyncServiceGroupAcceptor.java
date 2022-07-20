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

import com.alipay.sofa.registry.common.model.ServiceGroupType;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.google.common.base.Objects;
import java.util.Set;
import org.springframework.util.CollectionUtils;

/**
 * @author xiaojian.xj
 * @version : SyncServiceGroupAcceptor.java, v 0.1 2022年05月14日 14:48 xiaojian.xj Exp $
 */
public class SyncServiceGroupAcceptor implements SyncSlotAcceptor {

  private final String NAME = "SyncServiceGroupAcceptor";
  private final Set<ServiceGroupType> acceptGroups;

  public SyncServiceGroupAcceptor(Set<ServiceGroupType> acceptGroups) {
    this.acceptGroups = acceptGroups;
  }

  @Override
  public boolean accept(SyncAcceptorRequest request) {

    if (CollectionUtils.isEmpty(acceptGroups)) {
      return false;
    }
    return acceptGroups.contains(request.getServiceGroup());
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SyncServiceGroupAcceptor that = (SyncServiceGroupAcceptor) o;
    return Objects.equal(NAME, that.NAME);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(NAME);
  }
}
