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

import com.alipay.sofa.registry.common.model.PublisherGroupType;
import com.google.common.collect.Sets;
import java.util.Collections;

/**
 * @author xiaojian.xj
 * @version : AcceptorConstants.java, v 0.1 2022年06月08日 20:37 xiaojian.xj Exp $
 */
public class AcceptorConstants {

  public static final SyncSlotAcceptor SERVICE_MAPPING_ACCEPTOR =
      new SyncSlotGroupAcceptor(
          Sets.newHashSet(PublisherGroupType.REGISTRY_MAPPING.getCode()), Collections.emptySet());

  public static final SyncSlotAcceptor APP_REVISION_ACCEPTOR =
      new SyncSlotGroupAcceptor(
          Sets.newHashSet(PublisherGroupType.REGISTRY_MAPPING.getCode()), Collections.emptySet());
}
