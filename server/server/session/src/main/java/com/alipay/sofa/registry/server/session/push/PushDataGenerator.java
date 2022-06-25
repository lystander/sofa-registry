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
package com.alipay.sofa.registry.server.session.push;

import com.alipay.sofa.registry.common.model.SubscriberUtils;
import com.alipay.sofa.registry.common.model.client.pb.ReceivedDataPb;
import com.alipay.sofa.registry.common.model.store.*;
import com.alipay.sofa.registry.compress.Compressor;
import com.alipay.sofa.registry.core.model.ReceivedConfigData;
import com.alipay.sofa.registry.core.model.ReceivedData;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.converter.ReceivedDataConverter;
import com.alipay.sofa.registry.server.session.converter.pb.ReceivedDataConvertor;
import com.alipay.sofa.registry.server.session.predicate.ZonePredicate;
import com.alipay.sofa.registry.server.session.providedata.CompressPushService;
import com.google.common.collect.Lists;
import java.util.*;
import java.util.function.Predicate;
import javax.annotation.Resource;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

public class PushDataGenerator {

  @Autowired protected SessionServerConfig sessionServerConfig;

  @Resource protected CompressPushService compressPushService;

  public PushData createPushData(MultiSubDatum unzipDatum, Map<String, Subscriber> subscriberMap) {
    unzipDatum.mustUnzipped();
    if (subscriberMap.size() > 1) {
      SubscriberUtils.getAndAssertHasSameScope(subscriberMap.values());
      SubscriberUtils.getAndAssertAcceptedEncodes(subscriberMap.values());
      SubscriberUtils.getAndAssertAcceptMulti(subscriberMap.values());
    }
    // only supported 4.x
    SubscriberUtils.assertClientVersion(subscriberMap.values(), BaseInfo.ClientVersion.StoreData);

    final Subscriber subscriber = subscriberMap.values().iterator().next();
    String dataId = subscriber.getDataId();
    String clientCell = sessionServerConfig.getClientCell(subscriber.getCell());

    // todo xiaojian.xj multi switch.
    boolean multiSwitch = true;
    boolean multi = multiSwitch && subscriber.acceptMulti();

    Predicate<String> pushDataPredicate =
        ZonePredicate.pushDataPredicate(
            dataId, clientCell, subscriber.getScope(), multi, sessionServerConfig);

    Predicate<String> localSegmentZonePredicate =
        ZonePredicate.localSegmentZonesPredicate(
            dataId, clientCell, subscriber.getScope(), multi, sessionServerConfig);

    // todo xiaojian.xj get all localSegmentZones.
    Set<String> allLocalSegmentZones = Sets.newHashSet();

    PushData<ReceivedData> pushData =
        ReceivedDataConverter.getReceivedDataMulti(
            unzipDatum,
            multi,
            subscriber.getScope(),
            Lists.newArrayList(subscriberMap.keySet()),
            sessionServerConfig.getSessionServerDataCenter(),
            clientCell,
            allLocalSegmentZones,
            pushDataPredicate,
            localSegmentZonePredicate);

    final Byte serializerIndex = subscriber.getSourceAddress().getSerializerIndex();
    if (serializerIndex == null || URL.PROTOBUF != serializerIndex) {
      return pushData;
    }
    Compressor compressor =
        compressPushService.getCompressor(
            pushData.getPayload(),
            subscriber.getAcceptEncodes(),
            subscriber.getSourceAddress().getIpAddress());
    if (compressor == null) {
      ReceivedDataPb receivedDataPb = ReceivedDataConvertor.convert2Pb(pushData.getPayload());
      return new PushData<>(receivedDataPb, pushData.getDataCountMap());
    } else {
      ReceivedDataPb receivedDataPb =
          ReceivedDataConvertor.convert2CompressedPb(pushData.getPayload(), compressor, multi);
      return new PushData<>(
          receivedDataPb,
          pushData.getDataCountMap(),
          compressor.getEncoding(),
          receivedDataPb.getBody().size());
    }
  }

  public PushData createPushData(Watcher watcher, ReceivedConfigData data) {
    URL url = watcher.getSourceAddress();
    Object o = data;
    if (url.getSerializerIndex() != null && URL.PROTOBUF == url.getSerializerIndex()) {
      o = ReceivedDataConvertor.convert2Pb(data);
    }
    return new PushData(
        o, Collections.singletonMap(sessionServerConfig.getSessionServerDataCenter(), 1));
  }
}
