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
package com.alipay.sofa.registry.jdbc.domain;

import com.alipay.sofa.registry.jdbc.informer.DbEntry;
import com.alipay.sofa.registry.jdbc.version.config.ConfigEntry;
import java.util.Date;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSyncInfo.java, v 0.1 2022年04月13日 14:41 xiaojian.xj Exp $
 */
public class MultiClusterSyncDomain implements DbEntry, ConfigEntry {
  /** primary key */
  private long id;

  /** local data center */
  private String dataCenter;

  /** sync remote data center */
  private String remoteDataCenter;

  /** remote meta address, use to get meta leader */
  private String remoteMetaAddress;

  /** data version */
  private long dataVersion;

  /** create time */
  private Date gmtCreate;

  /** last update time */
  private Date gmtModify;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public Date getGmtCreate() {
    return gmtCreate;
  }

  public MultiClusterSyncDomain() {}

  public MultiClusterSyncDomain(
      String dataCenter, String remoteDataCenter, String remoteMetaAddress, long dataVersion) {
    this.dataCenter = dataCenter;
    this.remoteDataCenter = remoteDataCenter;
    this.remoteMetaAddress = remoteMetaAddress;
    this.dataVersion = dataVersion;
  }

  /**
   * Setter method for property <tt>id</tt>.
   *
   * @param id value to be assigned to property id
   */
  public void setId(long id) {
    this.id = id;
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
   * Setter method for property <tt>gmtCreate</tt>.
   *
   * @param gmtCreate value to be assigned to property gmtCreate
   */
  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  /**
   * Getter method for property <tt>gmtModify</tt>.
   *
   * @return property value of gmtModify
   */
  public Date getGmtModify() {
    return gmtModify;
  }

  /**
   * Setter method for property <tt>gmtModify</tt>.
   *
   * @param gmtModify value to be assigned to property gmtModify
   */
  public void setGmtModify(Date gmtModify) {
    this.gmtModify = gmtModify;
  }

  /**
   * Getter method for property <tt>dataVersion</tt>.
   *
   * @return property value of dataVersion
   */
  @Override
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
}
