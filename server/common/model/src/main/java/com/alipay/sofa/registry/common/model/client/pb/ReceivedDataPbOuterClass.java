// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ReceivedDataPb.proto

package com.alipay.sofa.registry.common.model.client.pb;

public final class ReceivedDataPbOuterClass {
  private ReceivedDataPbOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ReceivedDataPb_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ReceivedDataPb_DataEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_DataEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ReceivedDataPb_MultiDataEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_MultiDataEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ReceivedDataPb_MultiVersionEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ReceivedDataPb_MultiVersionEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024ReceivedDataPb.proto\032\021DataBoxesPb.prot" +
      "o\"\256\004\n\016ReceivedDataPb\022\016\n\006dataId\030\001 \001(\t\022\r\n\005" +
      "group\030\002 \001(\t\022\022\n\ninstanceId\030\003 \001(\t\022\017\n\007segme" +
      "nt\030\004 \001(\t\022\r\n\005scope\030\005 \001(\t\022\033\n\023subscriberReg" +
      "istIds\030\006 \003(\t\022\'\n\004data\030\007 \003(\0132\031.ReceivedDat" +
      "aPb.DataEntry\022\017\n\007version\030\010 \001(\003\022\021\n\tlocalZ" +
      "one\030\t \001(\t\022\020\n\010encoding\030\n \001(\t\022\014\n\004body\030\013 \001(" +
      "\014\022\026\n\016originBodySize\030\014 \001(\005\0221\n\tmultiData\030\r" +
      " \003(\0132\036.ReceivedDataPb.MultiDataEntry\0227\n\014" +
      "multiVersion\030\016 \003(\0132!.ReceivedDataPb.Mult" +
      "iVersionEntry\022\031\n\021localSegmentZones\030\017 \003(\t" +
      "\0329\n\tDataEntry\022\013\n\003key\030\001 \001(\t\022\033\n\005value\030\002 \001(" +
      "\0132\014.DataBoxesPb:\0028\001\0320\n\016MultiDataEntry\022\013\n" +
      "\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\014:\0028\001\0323\n\021MultiVe" +
      "rsionEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\003:\002" +
      "8\001B:\n/com.alipay.sofa.registry.common.mo" +
      "del.client.pbP\001Z\005protob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.alipay.sofa.registry.common.model.client.pb.DataBoxesPbOuterClass.getDescriptor(),
        });
    internal_static_ReceivedDataPb_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ReceivedDataPb_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ReceivedDataPb_descriptor,
        new java.lang.String[] { "DataId", "Group", "InstanceId", "Segment", "Scope", "SubscriberRegistIds", "Data", "Version", "LocalZone", "Encoding", "Body", "OriginBodySize", "MultiData", "MultiVersion", "LocalSegmentZones", });
    internal_static_ReceivedDataPb_DataEntry_descriptor =
      internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(0);
    internal_static_ReceivedDataPb_DataEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ReceivedDataPb_DataEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_ReceivedDataPb_MultiDataEntry_descriptor =
      internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(1);
    internal_static_ReceivedDataPb_MultiDataEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ReceivedDataPb_MultiDataEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_ReceivedDataPb_MultiVersionEntry_descriptor =
      internal_static_ReceivedDataPb_descriptor.getNestedTypes().get(2);
    internal_static_ReceivedDataPb_MultiVersionEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ReceivedDataPb_MultiVersionEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    com.alipay.sofa.registry.common.model.client.pb.DataBoxesPbOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
