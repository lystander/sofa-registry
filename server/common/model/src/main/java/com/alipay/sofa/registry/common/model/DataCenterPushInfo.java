/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.compress.CompressUtils;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : DataCenterPushInfo.java, v 0.1 2022年07月19日 15:05 xiaojian.xj Exp $
 */
public class DataCenterPushInfo {

  private long pushVersion;

  private Map<String, SegmentPushInfo> segmentPushInfos = Maps.newHashMap();

  public DataCenterPushInfo() {
  }

  public DataCenterPushInfo(long pushVersion) {
    this.pushVersion = pushVersion;
  }

  public DataCenterPushInfo(long pushVersion,
                            Map<String, SegmentPushInfo> segmentPushInfos) {
    this.pushVersion = pushVersion;
    this.segmentPushInfos = segmentPushInfos;
  }

  /**
   * Getter method for property <tt>pushVersion</tt>.
   *
   * @return property value of pushVersion
   */
  public long getPushVersion() {
    return pushVersion;
  }

  /**
   * Setter method for property <tt>pushVersion</tt>.
   *
   * @param pushVersion value to be assigned to property pushVersion
   */
  public void setPushVersion(long pushVersion) {
    this.pushVersion = pushVersion;
  }

  /**
   * Getter method for property <tt>segmentPushInfos</tt>.
   *
   * @return property value of segmentPushInfos
   */
  public Map<String, SegmentPushInfo> getSegmentPushInfos() {
    return segmentPushInfos;
  }

  public Set<String> getSegments() {
    if (segmentPushInfos == null) {
      return Collections.EMPTY_SET;
    }
    return segmentPushInfos.keySet();
  }

  public Map<String, Integer> getPushNum(){
    if (segmentPushInfos == null) {
      return Collections.EMPTY_MAP;
    }
    Map<String, Integer> ret = Maps.newHashMap();
    for (SegmentPushInfo value : segmentPushInfos.values()) {
      ret.put(value.getSegment(), value.getDataCount());
    }
    return ret;
  }

  public Map<String, String> getEncode(){
    if (segmentPushInfos == null) {
      return Collections.EMPTY_MAP;
    }
    Map<String, String> ret = Maps.newHashMap();
    for (SegmentPushInfo value : segmentPushInfos.values()) {
      ret.put(value.getSegment(), CompressUtils.normalizeEncode(value.getEncode()));
    }
    return ret;
  }

  public Map<String, Integer> getEncodeSize(){
    if (segmentPushInfos == null) {
      return Collections.EMPTY_MAP;
    }
    Map<String, Integer> ret = Maps.newHashMap();
    for (SegmentPushInfo value : segmentPushInfos.values()) {
      ret.put(value.getSegment(), value.getEncodeSize());
    }
    return ret;
  }

  /**
   * Setter method for property <tt>segmentPushInfos</tt>.
   *
   * @param segmentPushInfos value to be assigned to property segmentPushInfos
   */
  public void setSegmentPushInfos(Map<String, SegmentPushInfo> segmentPushInfos) {
    this.segmentPushInfos = segmentPushInfos;
  }

  public void addSegmentInfo(String segment, String encoding, int encodeSize) {
    SegmentPushInfo segmentPushInfo = segmentPushInfos.computeIfAbsent(segment, k -> new SegmentPushInfo(segment));
    segmentPushInfo.setEncode(encoding);
    segmentPushInfo.setEncodeSize(encodeSize);
  }
}
