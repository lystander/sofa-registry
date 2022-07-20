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
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.slot.SlotTable;
import java.io.Serializable;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteClusterSlotSyncResponse.java, v 0.1 2022年04月15日 21:29 xiaojian.xj Exp $
 */
public class RemoteClusterSlotSyncResponse implements Serializable {
  private static final long serialVersionUID = -2923303142018284216L;

  /** nodes.clusterId */
  private final String clusterId;

  /** nodes.localSegmentRegions */
  private final Set<String> localSegZones;

  /** meta leader */
  private final String metaLeader;

  /** leader epoch */
  private final long metaLeaderEpoch;

  /** sync on meta leader */
  private final boolean syncOnLeader;

  /** leader warmuped */
  private final boolean leaderWarmuped;

  /** if slot table upgrade */
  private final boolean slotTableUpgrade;

  /** slot table will be null if syncOnLeader=false or slotTableUpgrade=false */
  private final SlotTable slotTable;

  public RemoteClusterSlotSyncResponse(
      String clusterId,
      Set<String> localSegZones,
      String metaLeader,
      long metaLeaderEpoch,
      boolean syncOnLeader,
      boolean leaderWarmuped,
      boolean slotTableUpgrade,
      SlotTable slotTable) {
    this.clusterId = clusterId;
    this.localSegZones = localSegZones;
    this.metaLeader = metaLeader;
    this.metaLeaderEpoch = metaLeaderEpoch;
    this.syncOnLeader = syncOnLeader;
    this.leaderWarmuped = leaderWarmuped;
    this.slotTableUpgrade = slotTableUpgrade;
    this.slotTable = slotTable;
  }

  public static RemoteClusterSlotSyncResponse wrongLeader(String clusterId, Set<String> localSegZones, String metaLeader, long metaLeaderEpoch) {
    return new RemoteClusterSlotSyncResponse(clusterId, localSegZones,
        metaLeader, metaLeaderEpoch, false, false, false, null);
  }

  public static RemoteClusterSlotSyncResponse leaderNotWarmuped(
          String clusterId, Set<String> localSegZones, String metaLeader, long metaLeaderEpoch) {
    return new RemoteClusterSlotSyncResponse(clusterId, localSegZones, metaLeader, metaLeaderEpoch, true, false, false, null);
  }

  public static RemoteClusterSlotSyncResponse notUpgrade(String clusterId, Set<String> localSegZones, String metaLeader, long metaLeaderEpoch) {
    return new RemoteClusterSlotSyncResponse(clusterId, localSegZones, metaLeader, metaLeaderEpoch, true, true, false, null);
  }

  public static RemoteClusterSlotSyncResponse upgrade(
          String clusterId,Set<String> localSegZones, String metaLeader, long metaLeaderEpoch, SlotTable slotTable) {
    return new RemoteClusterSlotSyncResponse(clusterId, localSegZones,
        metaLeader, metaLeaderEpoch, true, true, true, slotTable);
  }

  /**
   * Getter method for property <tt>clusterId</tt>.
   *
   * @return property value of clusterId
   */
  public String getClusterId() {
    return clusterId;
  }

  /**
   * Getter method for property <tt>localSegZones</tt>.
   *
   * @return property value of localSegZones
   */
  public Set<String> getLocalSegZones() {
    return localSegZones;
  }

  public boolean isSyncOnLeader() {
    return syncOnLeader;
  }

  public boolean isSlotTableUpgrade() {
    return slotTableUpgrade;
  }

  public boolean isLeaderWarmuped() {
    return leaderWarmuped;
  }

  /**
   * Getter method for property <tt>metaLeader</tt>.
   *
   * @return property value of metaLeader
   */
  public String getMetaLeader() {
    return metaLeader;
  }

  /**
   * Getter method for property <tt>metaLeaderEpoch</tt>.
   *
   * @return property value of metaLeaderEpoch
   */
  public long getMetaLeaderEpoch() {
    return metaLeaderEpoch;
  }

  /**
   * Getter method for property <tt>slotTable</tt>.
   *
   * @return property value of slotTable
   */
  public SlotTable getSlotTable() {
    return slotTable;
  }

  @Override
  public String toString() {
    return "RemoteClusterSlotSyncResponse{"
        + "metaLeader='"
        + metaLeader
        + '\''
        + ", metaLeaderEpoch="
        + metaLeaderEpoch
        + ", syncOnLeader="
        + syncOnLeader
        + ", slotTableUpgrade="
        + slotTableUpgrade
        + ", slotTable="
        + slotTable
        + '}';
  }
}
