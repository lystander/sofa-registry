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
package com.alipay.sofa.registry.jraft.repository.impl;

import com.alipay.sofa.registry.common.model.store.AppRevision;
import com.alipay.sofa.registry.store.api.repository.AppRevisionRepository;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaojian.xj
 * @version $Id: AppRevisionRaftRepository.java, v 0.1 2021年01月17日 15:57 xiaojian.xj Exp $
 */
public class AppRevisionRaftRepository implements AppRevisionRepository {

  /** map: <revision, AppRevision> */
  private final Map<String, AppRevision> registry = new ConcurrentHashMap<>();

  @Override
  public void register(AppRevision appRevision) {
    if (this.registry.containsKey(appRevision.getRevision())) {
      return;
    }
  }

  @Override
  public void refresh() {}

  @Override
  public AppRevision queryRevision(String revision) {
    return registry.get(revision);
  }

  @Override
  public AppRevision heartbeat(String revision) {
    AppRevision appRevision = registry.get(revision);
    if (appRevision != null) {
      appRevision.setLastHeartbeat(new Date());
    }
    return appRevision;
  }
}
