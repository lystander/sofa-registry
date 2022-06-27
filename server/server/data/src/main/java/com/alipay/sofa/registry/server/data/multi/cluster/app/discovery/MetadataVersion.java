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
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.store.WordCache;

/**
 * @author xiaojian.xj
 * @version : MetadataVersion.java, v 0.1 2022年06月27日 14:19 xiaojian.xj Exp $
 */
public class MetadataVersion {

  private static final long INIT = -1L;
  final String dataInfoId;

  final long version;

  public MetadataVersion(String dataInfoId) {
    this.dataInfoId = dataInfoId;
    this.version = INIT;
  }

  public MetadataVersion(String dataInfoId, long version) {
    this.dataInfoId = WordCache.getWordCache(dataInfoId);
    this.version = version;
  }
}
