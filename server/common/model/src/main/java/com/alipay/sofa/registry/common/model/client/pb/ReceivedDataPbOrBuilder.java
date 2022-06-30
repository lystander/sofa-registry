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
package com.alipay.sofa.registry.common.model.client.pb;

public interface ReceivedDataPbOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:ReceivedDataPb)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string dataId = 1;</code>
   *
   * @return The dataId.
   */
  java.lang.String getDataId();
  /**
   * <code>string dataId = 1;</code>
   *
   * @return The bytes for dataId.
   */
  com.google.protobuf.ByteString getDataIdBytes();

  /**
   * <code>string group = 2;</code>
   *
   * @return The group.
   */
  java.lang.String getGroup();
  /**
   * <code>string group = 2;</code>
   *
   * @return The bytes for group.
   */
  com.google.protobuf.ByteString getGroupBytes();

  /**
   * <code>string instanceId = 3;</code>
   *
   * @return The instanceId.
   */
  java.lang.String getInstanceId();
  /**
   * <code>string instanceId = 3;</code>
   *
   * @return The bytes for instanceId.
   */
  com.google.protobuf.ByteString getInstanceIdBytes();

  /**
   * <code>string segment = 4;</code>
   *
   * @return The segment.
   */
  java.lang.String getSegment();
  /**
   * <code>string segment = 4;</code>
   *
   * @return The bytes for segment.
   */
  com.google.protobuf.ByteString getSegmentBytes();

  /**
   * <code>string scope = 5;</code>
   *
   * @return The scope.
   */
  java.lang.String getScope();
  /**
   * <code>string scope = 5;</code>
   *
   * @return The bytes for scope.
   */
  com.google.protobuf.ByteString getScopeBytes();

  /**
   * <code>repeated string subscriberRegistIds = 6;</code>
   *
   * @return A list containing the subscriberRegistIds.
   */
  java.util.List<java.lang.String> getSubscriberRegistIdsList();
  /**
   * <code>repeated string subscriberRegistIds = 6;</code>
   *
   * @return The count of subscriberRegistIds.
   */
  int getSubscriberRegistIdsCount();
  /**
   * <code>repeated string subscriberRegistIds = 6;</code>
   *
   * @param index The index of the element to return.
   * @return The subscriberRegistIds at the given index.
   */
  java.lang.String getSubscriberRegistIds(int index);
  /**
   * <code>repeated string subscriberRegistIds = 6;</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the subscriberRegistIds at the given index.
   */
  com.google.protobuf.ByteString getSubscriberRegistIdsBytes(int index);

  /** <code>map&lt;string, .DataBoxesPb&gt; data = 7;</code> */
  int getDataCount();
  /** <code>map&lt;string, .DataBoxesPb&gt; data = 7;</code> */
  boolean containsData(java.lang.String key);
  /** Use {@link #getDataMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, com.alipay.sofa.registry.common.model.client.pb.DataBoxesPb>
      getData();
  /** <code>map&lt;string, .DataBoxesPb&gt; data = 7;</code> */
  java.util.Map<java.lang.String, com.alipay.sofa.registry.common.model.client.pb.DataBoxesPb>
      getDataMap();
  /** <code>map&lt;string, .DataBoxesPb&gt; data = 7;</code> */
  com.alipay.sofa.registry.common.model.client.pb.DataBoxesPb getDataOrDefault(
      java.lang.String key,
      com.alipay.sofa.registry.common.model.client.pb.DataBoxesPb defaultValue);
  /** <code>map&lt;string, .DataBoxesPb&gt; data = 7;</code> */
  com.alipay.sofa.registry.common.model.client.pb.DataBoxesPb getDataOrThrow(java.lang.String key);

  /**
   * <code>int64 version = 8;</code>
   *
   * @return The version.
   */
  long getVersion();

  /**
   * <code>string localZone = 9;</code>
   *
   * @return The localZone.
   */
  java.lang.String getLocalZone();
  /**
   * <code>string localZone = 9;</code>
   *
   * @return The bytes for localZone.
   */
  com.google.protobuf.ByteString getLocalZoneBytes();

  /**
   * <code>string encoding = 10;</code>
   *
   * @return The encoding.
   */
  java.lang.String getEncoding();
  /**
   * <code>string encoding = 10;</code>
   *
   * @return The bytes for encoding.
   */
  com.google.protobuf.ByteString getEncodingBytes();

  /**
   * <code>bytes body = 11;</code>
   *
   * @return The body.
   */
  com.google.protobuf.ByteString getBody();

  /**
   * <code>int32 originBodySize = 12;</code>
   *
   * @return The originBodySize.
   */
  int getOriginBodySize();

  /** <code>map&lt;string, bytes&gt; zipMultiData = 13;</code> */
  int getZipMultiDataCount();
  /** <code>map&lt;string, bytes&gt; zipMultiData = 13;</code> */
  boolean containsZipMultiData(java.lang.String key);
  /** Use {@link #getZipMultiDataMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, com.google.protobuf.ByteString> getZipMultiData();
  /** <code>map&lt;string, bytes&gt; zipMultiData = 13;</code> */
  java.util.Map<java.lang.String, com.google.protobuf.ByteString> getZipMultiDataMap();
  /** <code>map&lt;string, bytes&gt; zipMultiData = 13;</code> */
  com.google.protobuf.ByteString getZipMultiDataOrDefault(
      java.lang.String key, com.google.protobuf.ByteString defaultValue);
  /** <code>map&lt;string, bytes&gt; zipMultiData = 13;</code> */
  com.google.protobuf.ByteString getZipMultiDataOrThrow(java.lang.String key);

  /** <code>map&lt;string, .ReceivedDataBodyPb&gt; unzipMultiData = 14;</code> */
  int getUnzipMultiDataCount();
  /** <code>map&lt;string, .ReceivedDataBodyPb&gt; unzipMultiData = 14;</code> */
  boolean containsUnzipMultiData(java.lang.String key);
  /** Use {@link #getUnzipMultiDataMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<
          java.lang.String, com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPb>
      getUnzipMultiData();
  /** <code>map&lt;string, .ReceivedDataBodyPb&gt; unzipMultiData = 14;</code> */
  java.util.Map<
          java.lang.String, com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPb>
      getUnzipMultiDataMap();
  /** <code>map&lt;string, .ReceivedDataBodyPb&gt; unzipMultiData = 14;</code> */
  com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPb getUnzipMultiDataOrDefault(
      java.lang.String key,
      com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPb defaultValue);
  /** <code>map&lt;string, .ReceivedDataBodyPb&gt; unzipMultiData = 14;</code> */
  com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPb getUnzipMultiDataOrThrow(
      java.lang.String key);

  /** <code>map&lt;string, string&gt; multiEncoding = 15;</code> */
  int getMultiEncodingCount();
  /** <code>map&lt;string, string&gt; multiEncoding = 15;</code> */
  boolean containsMultiEncoding(java.lang.String key);
  /** Use {@link #getMultiEncodingMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getMultiEncoding();
  /** <code>map&lt;string, string&gt; multiEncoding = 15;</code> */
  java.util.Map<java.lang.String, java.lang.String> getMultiEncodingMap();
  /** <code>map&lt;string, string&gt; multiEncoding = 15;</code> */
  java.lang.String getMultiEncodingOrDefault(java.lang.String key, java.lang.String defaultValue);
  /** <code>map&lt;string, string&gt; multiEncoding = 15;</code> */
  java.lang.String getMultiEncodingOrThrow(java.lang.String key);

  /** <code>map&lt;string, int64&gt; multiVersion = 16;</code> */
  int getMultiVersionCount();
  /** <code>map&lt;string, int64&gt; multiVersion = 16;</code> */
  boolean containsMultiVersion(java.lang.String key);
  /** Use {@link #getMultiVersionMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.Long> getMultiVersion();
  /** <code>map&lt;string, int64&gt; multiVersion = 16;</code> */
  java.util.Map<java.lang.String, java.lang.Long> getMultiVersionMap();
  /** <code>map&lt;string, int64&gt; multiVersion = 16;</code> */
  long getMultiVersionOrDefault(java.lang.String key, long defaultValue);
  /** <code>map&lt;string, int64&gt; multiVersion = 16;</code> */
  long getMultiVersionOrThrow(java.lang.String key);

  /** <code>map&lt;string, .SegmentMetadataPb&gt; segMetadata = 17;</code> */
  int getSegMetadataCount();
  /** <code>map&lt;string, .SegmentMetadataPb&gt; segMetadata = 17;</code> */
  boolean containsSegMetadata(java.lang.String key);
  /** Use {@link #getSegMetadataMap()} instead. */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPb>
      getSegMetadata();
  /** <code>map&lt;string, .SegmentMetadataPb&gt; segMetadata = 17;</code> */
  java.util.Map<java.lang.String, com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPb>
      getSegMetadataMap();
  /** <code>map&lt;string, .SegmentMetadataPb&gt; segMetadata = 17;</code> */
  com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPb getSegMetadataOrDefault(
      java.lang.String key,
      com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPb defaultValue);
  /** <code>map&lt;string, .SegmentMetadataPb&gt; segMetadata = 17;</code> */
  com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPb getSegMetadataOrThrow(
      java.lang.String key);
}
