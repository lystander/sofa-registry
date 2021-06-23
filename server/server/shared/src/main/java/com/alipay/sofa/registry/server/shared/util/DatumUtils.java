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
package com.alipay.sofa.registry.server.shared.util;

import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.dataserver.Datum;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.store.*;
import com.alipay.sofa.registry.core.model.DataBox;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author xuanbei
 * @since 2019/2/12
 */
public final class DatumUtils {
  private DatumUtils() {}

  public static Map<String, DatumVersion> intern(Map<String, DatumVersion> versionMap) {
    Map<String, DatumVersion> ret = Maps.newHashMapWithExpectedSize(versionMap.size());
    versionMap.forEach((k, v) -> ret.put(WordCache.getWordCache(k), v));
    return ret;
  }

  public static Map<String, Long> getVersions(Map<String, Datum> datumMap) {
    Map<String, Long> versions = Maps.newHashMapWithExpectedSize(datumMap.size());
    datumMap.forEach((k, v) -> versions.put(k, v.getVersion()));
    return versions;
  }

  public static SubDatum newEmptySubDatum(Subscriber subscriber, String datacenter, long version) {
    SubDatum datum =
        new SubDatum(
            subscriber.getDataInfoId(),
            datacenter,
            version,
            Collections.emptyList(),
            subscriber.getDataId(),
            subscriber.getInstanceId(),
            subscriber.getGroup());
    return datum;
  }

  public static SubDatum of(Datum datum) {
    List<SubPublisher> publishers = Lists.newArrayListWithCapacity(datum.publisherSize());
    for (Publisher publisher : datum.getPubMap().values()) {
      final URL srcAddress = publisher.getSourceAddress();
      // temp publisher the srcAddress maybe null
      final String srcAddressString = srcAddress == null ? null : srcAddress.getAddressString();
      publishers.add(
          new SubPublisher(
              publisher.getRegisterId(),
              publisher.getCell(),
              publisher.getDataList(),
              publisher.getClientId(),
              publisher.getVersion(),
              srcAddressString,
              publisher.getRegisterTimestamp(),
              publisher.getPublishSource()));
    }
    return new SubDatum(
        datum.getDataInfoId(),
        datum.getDataCenter(),
        datum.getVersion(),
        publishers,
        datum.getDataId(),
        datum.getInstanceId(),
        datum.getGroup(),
        datum.getRecentVersions());
  }

  public static long DataBoxListSize(List<DataBox> boxes) {
    if (CollectionUtils.isEmpty(boxes)) {
      return 0;
    }
    long sum = 0;
    for (DataBox box : boxes) {
      if (box == null || box.getData() == null) {
        continue;
      }
      sum += box.getData().length();
    }
    return sum;
  }

  public static long ServerDataBoxListSize(List<ServerDataBox> boxes) {
    if (CollectionUtils.isEmpty(boxes)) {
      return 0;
    }
    long sum = 0;
    for (ServerDataBox box : boxes) {
      if (box == null) {
        continue;
      }
      box.object2bytes();
      sum += box.byteSize();
    }
    return sum;
  }
}
