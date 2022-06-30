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

public final class ReceivedDataPbOuterClass {
  private ReceivedDataPbOuterClass() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor internal_static_ReceivedDataPb_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_DataEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_DataEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_ZipMultiDataEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_ZipMultiDataEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_UnzipMultiDataEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_UnzipMultiDataEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_MultiEncodingEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_MultiEncodingEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_MultiVersionEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_MultiVersionEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_ReceivedDataPb_SegMetadataEntry_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_SegMetadataEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n\024ReceivedDataPb.proto\032\021DataBoxesPb.prot"
          + "o\032\030ReceivedDataBodyPb.proto\032\027SegmentMeta"
          + "dataPb.proto\"\225\007\n\016ReceivedDataPb\022\016\n\006dataI"
          + "d\030\001 \001(\t\022\r\n\005group\030\002 \001(\t\022\022\n\ninstanceId\030\003 \001"
          + "(\t\022\017\n\007segment\030\004 \001(\t\022\r\n\005scope\030\005 \001(\t\022\033\n\023su"
          + "bscriberRegistIds\030\006 \003(\t\022\'\n\004data\030\007 \003(\0132\031."
          + "ReceivedDataPb.DataEntry\022\017\n\007version\030\010 \001("
          + "\003\022\021\n\tlocalZone\030\t \001(\t\022\020\n\010encoding\030\n \001(\t\022\014"
          + "\n\004body\030\013 \001(\014\022\026\n\016originBodySize\030\014 \001(\005\0227\n\014"
          + "zipMultiData\030\r \003(\0132!.ReceivedDataPb.ZipM"
          + "ultiDataEntry\022;\n\016unzipMultiData\030\016 \003(\0132#."
          + "ReceivedDataPb.UnzipMultiDataEntry\0229\n\rmu"
          + "ltiEncoding\030\017 \003(\0132\".ReceivedDataPb.Multi"
          + "EncodingEntry\0227\n\014multiVersion\030\020 \003(\0132!.Re"
          + "ceivedDataPb.MultiVersionEntry\0225\n\013segMet"
          + "adata\030\021 \003(\0132 .ReceivedDataPb.SegMetadata"
          + "Entry\0329\n\tDataEntry\022\013\n\003key\030\001 \001(\t\022\033\n\005value"
          + "\030\002 \001(\0132\014.DataBoxesPb:\0028\001\0323\n\021ZipMultiData"
          + "Entry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\014:\0028\001\032J\n"
          + "\023UnzipMultiDataEntry\022\013\n\003key\030\001 \001(\t\022\"\n\005val"
          + "ue\030\002 \001(\0132\023.ReceivedDataBodyPb:\0028\001\0324\n\022Mul"
          + "tiEncodingEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 "
          + "\001(\t:\0028\001\0323\n\021MultiVersionEntry\022\013\n\003key\030\001 \001("
          + "\t\022\r\n\005value\030\002 \001(\003:\0028\001\032F\n\020SegMetadataEntry"
          + "\022\013\n\003key\030\001 \001(\t\022!\n\005value\030\002 \001(\0132\022.SegmentMe"
          + "tadataPb:\0028\001B:\n/com.alipay.sofa.registry"
          + ".common.model.client.pbP\001Z\005protob\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.alipay.sofa.registry.common.model.client.pb.DataBoxesPbOuterClass.getDescriptor(),
              com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPbOuterClass
                  .getDescriptor(),
              com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPbOuterClass
                  .getDescriptor(),
            });
    internal_static_ReceivedDataPb_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_ReceivedDataPb_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_descriptor,
            new java.lang.String[] {
              "DataId",
              "Group",
              "InstanceId",
              "Segment",
              "Scope",
              "SubscriberRegistIds",
              "Data",
              "Version",
              "LocalZone",
              "Encoding",
              "Body",
              "OriginBodySize",
              "ZipMultiData",
              "UnzipMultiData",
              "MultiEncoding",
              "MultiVersion",
              "SegMetadata",
            });
    internal_static_ReceivedDataPb_DataEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(0);
    internal_static_ReceivedDataPb_DataEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_DataEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_ReceivedDataPb_ZipMultiDataEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(1);
    internal_static_ReceivedDataPb_ZipMultiDataEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_ZipMultiDataEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_ReceivedDataPb_UnzipMultiDataEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(2);
    internal_static_ReceivedDataPb_UnzipMultiDataEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_UnzipMultiDataEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_ReceivedDataPb_MultiEncodingEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(3);
    internal_static_ReceivedDataPb_MultiEncodingEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_MultiEncodingEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_ReceivedDataPb_MultiVersionEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(4);
    internal_static_ReceivedDataPb_MultiVersionEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_MultiVersionEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    internal_static_ReceivedDataPb_SegMetadataEntry_descriptor =
        internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(5);
    internal_static_ReceivedDataPb_SegMetadataEntry_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_ReceivedDataPb_SegMetadataEntry_descriptor,
            new java.lang.String[] {
              "Key", "Value",
            });
    com.alipay.sofa.registry.common.model.client.pb.DataBoxesPbOuterClass.getDescriptor();
    com.alipay.sofa.registry.common.model.client.pb.ReceivedDataBodyPbOuterClass.getDescriptor();
    com.alipay.sofa.registry.common.model.client.pb.SegmentMetadataPbOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
