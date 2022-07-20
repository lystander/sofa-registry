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
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.common.model.store.DataInfo;
import org.apache.commons.lang.StringUtils;

/**
 * @author xiaojian.xj
 * @version : ServiceGroupType.java, v 0.1 2022年05月24日 22:21 xiaojian.xj Exp $
 */
public enum ServiceGroupType {
  REGISTRY_MAPPING("REGISTRY_MAPPING."),
  REGISTRY_REVISION("REGISTRY_REVISION"),
  REGISTRY_SERVICE("REGISTRY_SERVICE"),
  ;

  private final String code;

  ServiceGroupType(String code) {
    this.code = code;
  }

  public static boolean isServiceMapping(String dataInfoId) {
    return StringUtils.startsWith(dataInfoId, REGISTRY_MAPPING.code);
  }

  public static boolean isRevision(String dataInfoId) {
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    return StringUtils.equals(dataInfo.getGroup(), REGISTRY_REVISION.code);
  }

  public static ServiceGroupType of(String dataInfoId) {
    if (isRevision(dataInfoId)) {
      return REGISTRY_REVISION;
    } else if (isServiceMapping(dataInfoId)) {
      return REGISTRY_MAPPING;
    }
    return REGISTRY_SERVICE;
  }

  /**
   * Getter method for property <tt>code</tt>.
   *
   * @return property value of code
   */
  public String getCode() {
    return code;
  }
}
