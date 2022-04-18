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
package com.alipay.sofa.registry.common.model.metaserver;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSyncInfo.java, v 0.1 2022年04月13日 17:09 xiaojian.xj Exp $
 */
public class MultiClusterSyncInfo {

  /** local data center */
  private String dataCenter;

  /** sync remote data center */
  private String remoteDataCenter;

  /** remote meta address, use to get meta leader */
  private String remoteMetaAddress;

  /** data version */
  private long dataVersion;

  public MultiClusterSyncInfo() {}

  public MultiClusterSyncInfo(String remoteDataCenter, String remoteMetaAddress, long dataVersion) {
    this.remoteDataCenter = remoteDataCenter;
    this.remoteMetaAddress = remoteMetaAddress;
    this.dataVersion = dataVersion;
  }

  public MultiClusterSyncInfo(
      String dataCenter, String remoteDataCenter, String remoteMetaAddress, long dataVersion) {
    this.dataCenter = dataCenter;
    this.remoteDataCenter = remoteDataCenter;
    this.remoteMetaAddress = remoteMetaAddress;
    this.dataVersion = dataVersion;
  }

  /**
   * Getter method for property <tt>dataCenter</tt>.
   *
   * @return property value of dataCenter
   */
  public String getDataCenter() {
    return dataCenter;
  }

  /**
   * Setter method for property <tt>dataCenter</tt>.
   *
   * @param dataCenter value to be assigned to property dataCenter
   */
  public void setDataCenter(String dataCenter) {
    this.dataCenter = dataCenter;
  }

  /**
   * Getter method for property <tt>remoteDataCenter</tt>.
   *
   * @return property value of remoteDataCenter
   */
  public String getRemoteDataCenter() {
    return remoteDataCenter;
  }

  /**
   * Setter method for property <tt>remoteDataCenter</tt>.
   *
   * @param remoteDataCenter value to be assigned to property remoteDataCenter
   */
  public void setRemoteDataCenter(String remoteDataCenter) {
    this.remoteDataCenter = remoteDataCenter;
  }

  /**
   * Getter method for property <tt>remoteMetaAddress</tt>.
   *
   * @return property value of remoteMetaAddress
   */
  public String getRemoteMetaAddress() {
    return remoteMetaAddress;
  }

  /**
   * Setter method for property <tt>remoteMetaAddress</tt>.
   *
   * @param remoteMetaAddress value to be assigned to property remoteMetaAddress
   */
  public void setRemoteMetaAddress(String remoteMetaAddress) {
    this.remoteMetaAddress = remoteMetaAddress;
  }

  /**
   * Getter method for property <tt>dataVersion</tt>.
   *
   * @return property value of dataVersion
   */
  public long getDataVersion() {
    return dataVersion;
  }

  /**
   * Setter method for property <tt>dataVersion</tt>.
   *
   * @param dataVersion value to be assigned to property dataVersion
   */
  public void setDataVersion(long dataVersion) {
    this.dataVersion = dataVersion;
  }

  @Override
  public String toString() {
    return "MultiClusterSyncInfo{"
        + "dataCenter='"
        + dataCenter
        + '\''
        + ", remoteDataCenter='"
        + remoteDataCenter
        + '\''
        + ", remoteMetaAddress='"
        + remoteMetaAddress
        + '\''
        + ", dataVersion="
        + dataVersion
        + '}';
  }
}
