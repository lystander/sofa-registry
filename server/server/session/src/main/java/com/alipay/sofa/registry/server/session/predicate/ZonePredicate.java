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
package com.alipay.sofa.registry.server.session.predicate;

import com.alipay.sofa.registry.core.model.ScopeEnum;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import java.util.function.Predicate;

/**
 * @author xiaojian.xj
 * @version $Id: ZonePredicate.java, v 0.1 2020年11月12日 21:57 xiaojian.xj Exp $
 */
public final class ZonePredicate {
  private ZonePredicate() {}

  public static Predicate<String> pushDataPredicate(
      String dataId,
      String clientCell,
      ScopeEnum scopeEnum,
      SessionServerConfig sessionServerConfig) {
    Predicate<String> zonePredicate =
        (zone) -> zoneFilter(dataId, clientCell, scopeEnum, sessionServerConfig, zone);
    return zonePredicate;
  }

  private static boolean zoneFilter(
      String dataId,
      String clientCell,
      ScopeEnum scopeEnum,
      SessionServerConfig sessionServerConfig,
      String zone) {
    if (!clientCell.equals(zone)) {
      if (ScopeEnum.zone == scopeEnum) {
        // zone scope subscribe only return zone list
        return true;

      } else if (ScopeEnum.dataCenter == scopeEnum || ScopeEnum.global == scopeEnum) {
        // disable zone config
        if (sessionServerConfig.isInvalidForeverZone(zone)
            && !sessionServerConfig.isInvalidIgnored(dataId)) {
          return true;
        }
      }
    }
    return false;
  }
}
