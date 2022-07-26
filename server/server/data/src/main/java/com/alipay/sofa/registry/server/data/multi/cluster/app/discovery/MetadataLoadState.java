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

import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.MetadataSlotChangeListener.SyncMetadataTask;
import com.alipay.sofa.registry.task.KeyedTask;

/**
 * @author xiaojian.xj
 * @version : MetadataLoadState.java, v 0.1 2022年05月31日 22:28 xiaojian.xj Exp $
 */
public final class MetadataLoadState {

  private long slotTableEpoch;

  private final int slotId;

  private long slotLeaderEpoch;

  private volatile boolean synced;

  private long lastSuccessSyncTime = -1L;

  private KeyedTask<SyncMetadataTask> syncMetadataTask;

  public MetadataLoadState(long slotTableEpoch, int slotId, long slotLeaderEpoch) {
    this.slotId = slotId;
    this.slotTableEpoch = slotTableEpoch;
    this.slotLeaderEpoch = slotLeaderEpoch;
  }

  public static MetadataLoadState initState(long slotTableEpoch, int slotId, long slotLeaderEpoch) {
    return new MetadataLoadState(slotTableEpoch, slotId, slotLeaderEpoch);
  }


  /**
   * Getter method for property <tt>slotId</tt>.
   *
   * @return property value of slotId
   */
  public int getSlotId() {
    return slotId;
  }

  public boolean isSynced() {
    return synced;
  }

  /**
   * Getter method for property <tt>lastSuccessSyncTime</tt>.
   *
   * @return property value of lastSuccessSyncTime
   */
  public long getLastSuccessSyncTime() {
    return lastSuccessSyncTime;
  }

  /**
   * Setter method for property <tt>lastSuccessSyncTime</tt>.
   *
   * @param lastSuccessSyncTime value to be assigned to property lastSuccessSyncTime
   */
  public void setLastSuccessSyncTime(long lastSuccessSyncTime) {
    this.lastSuccessSyncTime = lastSuccessSyncTime;
  }

  /**
   * Getter method for property <tt>slotTableEpoch</tt>.
   *
   * @return property value of slotTableEpoch
   */
  public synchronized long getSlotTableEpoch() {
    return slotTableEpoch;
  }

  /**
   * Getter method for property <tt>slotLeaderEpoch</tt>.
   *
   * @return property value of slotLeaderEpoch
   */
  public synchronized long getSlotLeaderEpoch() {
    return slotLeaderEpoch;
  }

  /**
   * Getter method for property <tt>syncMetadataTask</tt>.
   *
   * @return property value of syncMetadataTask
   */
  public KeyedTask<SyncMetadataTask> getSyncMetadataTask() {
    return syncMetadataTask;
  }

  /**
   * Setter method for property <tt>syncMetadataTask</tt>.
   *
   * @param syncMetadataTask value to be assigned to property syncMetadataTask
   */
  public void setSyncMetadataTask(KeyedTask<SyncMetadataTask> syncMetadataTask) {
    this.syncMetadataTask = syncMetadataTask;
  }

  public synchronized void completeSyncMetadataTask() {
    if (syncMetadataTask != null && syncMetadataTask.isSuccess()) {
      this.lastSuccessSyncTime = syncMetadataTask.getEndTime();
      this.synced = true;
    }
  }

  public synchronized void update(long slotTableEpoch, long slotLeaderEpoch) {
    this.slotTableEpoch = slotTableEpoch;
    this.slotLeaderEpoch = slotLeaderEpoch;
  }
}
