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
package com.alipay.sofa.registry.server.session.converter.pb;

import com.alipay.sofa.registry.common.model.client.pb.*;
import com.alipay.sofa.registry.compress.*;
import com.alipay.sofa.registry.core.model.DataBox;
import com.alipay.sofa.registry.core.model.ReceivedConfigData;
import com.alipay.sofa.registry.core.model.ReceivedData;
import com.alipay.sofa.registry.server.session.providedata.CompressPushService;
import com.alipay.sofa.registry.util.SystemUtils;
import com.google.protobuf.UnsafeByteOperations;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author bystander
 * @version $Id: ReceivedDataConvertor.java, v 0.1 2018年03月21日 2:07 PM bystander Exp $
 */
public final class ReceivedDataConvertor {
  private static final String KEY_COMPRESS_PUSH_CACHE_CAPACITY = "registry.compress.push.capacity";
  public static final CompressCachedExecutor<CompressedItem> pushCompressExecutor =
      CompressUtils.newCachedExecutor(
          "compress_push",
          60 * 1000,
          SystemUtils.getSystemInteger(KEY_COMPRESS_PUSH_CACHE_CAPACITY, 1024 * 1024 * 384));

  private ReceivedDataConvertor() {}

  public static ReceivedData convert2Java(ReceivedDataPb receivedDataPb) {

    if (receivedDataPb == null) {
      return null;
    }

    ReceivedData receivedData = new ReceivedData();

    receivedData.setData(DataBoxConvertor.convert2JavaMaps(receivedDataPb.getDataMap()));
    receivedData.setDataId(receivedDataPb.getDataId());
    receivedData.setGroup(receivedDataPb.getGroup());
    receivedData.setInstanceId(receivedDataPb.getInstanceId());
    receivedData.setLocalZone(receivedDataPb.getLocalZone());
    receivedData.setScope(receivedDataPb.getScope());
    receivedData.setSegment(receivedDataPb.getSegment());
    receivedData.setSubscriberRegistIds(
        ListStringConvertor.convert2Java(receivedDataPb.getSubscriberRegistIdsList()));
    receivedData.setVersion(receivedDataPb.getVersion());

    return receivedData;
  }

  public static ReceivedDataPb convert2MultiPb(
          ReceivedData receivedDataJava, CompressorGetter compressorGetter) {
    if (receivedDataJava == null) {
      return null;
    }

    String segment = receivedDataJava.getSegment();
    String dataId = receivedDataJava.getDataId();
    String instanceId = receivedDataJava.getInstanceId();
    String group = receivedDataJava.getGroup();
    try {
      ReceivedDataPb.Builder builder = ReceivedDataPb.newBuilder();
      builder
          .setDataId(dataId)
          .setGroup(group)
          .setInstanceId(instanceId)
          .setLocalZone(receivedDataJava.getLocalZone())
          .setScope(receivedDataJava.getScope())
          .setSegment(segment)
          .addAllSubscriberRegistIds(receivedDataJava.getSubscriberRegistIds());

        int originSize = 0;
        for (Entry<String, Map<String, List<DataBox>>> dataCenterEntry :
            receivedDataJava.getUnzipMultiDatas().entrySet()) {
          String dataCenter = dataCenterEntry.getKey();
          long version = receivedDataJava.getMultiVersion().get(dataCenter);
          Map<String, List<DataBox>> data = dataCenterEntry.getValue();
          CompressedItem compressedItem =
              dataCenterCompressed(segment, dataId, instanceId, group, version, data, compressor);
          // todo xiaojian.xj set receivedDataPb multiDatas multiVersion
          originSize += compressedItem.getOriginSize();
          builder.setOriginBodySize(originSize);
        }

      return builder.build();
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  public static CompressedItem dataCenterCompressed(
      String segment,
      String dataId,
      String instanceId,
      String group,
      long version,
      Map<String, List<DataBox>> zoneData,
      Compressor compressor)
      throws Exception {
    CompressedItem compressedItem =
        pushCompressExecutor.execute(
            CompressPushKey.of(
                segment, dataId, instanceId, group, version, zoneData, compressor.getEncoding()),
            () -> {
              Map<String, DataBoxesPb> dataBoxesPbMap = DataBoxConvertor.convert2PbMaps(zoneData);
              ReceivedDataBodyPb bodyPb =
                  ReceivedDataBodyPb.newBuilder().putAllData(dataBoxesPbMap).build();
              byte[] bodyData = bodyPb.toByteArray();
              byte[] compressed = compressor.compress(bodyData);
              return new CompressedItem(compressed, bodyData.length, compressor.getEncoding());
            });
    return compressedItem;
  }

  public static ReceivedDataPb convert2Pb(ReceivedData receivedDataJava, CompressorGetter compressorGetter) {
    if (receivedDataJava == null) {
      return null;
    }
    try {
      Compressor compressor = compressorGetter.get(receivedDataJava.getData());
      String segment = receivedDataJava.getSegment();
      String dataId = receivedDataJava.getDataId();
      String instanceId = receivedDataJava.getInstanceId();
      String group = receivedDataJava.getGroup();

      ReceivedDataPb.Builder builder = ReceivedDataPb.newBuilder();
      builder.setDataId(receivedDataJava.getDataId())
              .setGroup(receivedDataJava.getGroup())
              .setInstanceId(receivedDataJava.getInstanceId())
              .setLocalZone(receivedDataJava.getLocalZone())
              .setScope(receivedDataJava.getScope())
              .setSegment(receivedDataJava.getSegment())
              .setVersion(receivedDataJava.getVersion())
              .addAllSubscriberRegistIds(receivedDataJava.getSubscriberRegistIds());

      if (compressor == null) {
        Map<String, DataBoxesPb> dataBoxesPbMap =
                DataBoxConvertor.convert2PbMaps(receivedDataJava.getData());
        builder.putAllData(dataBoxesPbMap);
      } else {
        CompressedItem compressedItem =
                dataCenterCompressed(
                        segment,
                        dataId,
                        instanceId,
                        group,
                        receivedDataJava.getVersion(),
                        receivedDataJava.getData(),
                        compressor);
        // compressed data is immutable, use unsafe wrap to avoid copy
        builder.setBody(UnsafeByteOperations.unsafeWrap(compressedItem.getCompressedData()))
                .setOriginBodySize(compressedItem.getOriginSize());
      }


      return builder.build();
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  public static ReceivedConfigDataPb convert2Pb(ReceivedConfigData receivedConfigData) {

    if (receivedConfigData == null) {
      return null;
    }

    ReceivedConfigDataPb.Builder builder = ReceivedConfigDataPb.newBuilder();

    builder
        .setDataId(receivedConfigData.getDataId())
        .setGroup(receivedConfigData.getGroup())
        .setInstanceId(receivedConfigData.getInstanceId())
        .setVersion(receivedConfigData.getVersion())
        .addAllConfiguratorRegistIds(receivedConfigData.getConfiguratorRegistIds())
        .setDataBox(DataBoxConvertor.convert2Pb(receivedConfigData.getDataBox()));

    return builder.build();
  }

  public interface CompressorGetter {
    Compressor get(Map<String, List<DataBox>> data);
  }
}
