/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.slot.SlotTable;

import java.io.Serializable;

/**
 * @author xiaojian.xj
 * @version : RemoteClusterSlotSyncResponse.java, v 0.1 2022年04月15日 21:29 xiaojian.xj Exp $
 */
public class RemoteClusterSlotSyncResponse implements Serializable {
  private static final long serialVersionUID = -2923303142018284216L;

  /** meta leader */
  private final String metaLeader;

  /** leader epoch */
  private final long metaLeaderEpoch;

  /** sync on meta leader */
  private final boolean syncOnLeader;

  /** if slot table upgrade */
  private final boolean slotTableUpgrade;

  /** slot table will be null if syncOnLeader=false or slotTableUpgrade=false */
  private final SlotTable slotTable;

  public RemoteClusterSlotSyncResponse(
      String metaLeader,
      long metaLeaderEpoch,
      boolean syncOnLeader,
      boolean slotTableUpgrade,
      SlotTable slotTable) {
    this.metaLeader = metaLeader;
    this.metaLeaderEpoch = metaLeaderEpoch;
    this.syncOnLeader = syncOnLeader;
    this.slotTableUpgrade = slotTableUpgrade;
    this.slotTable = slotTable;
  }

  public static RemoteClusterSlotSyncResponse wrongLeader(
      String metaLeader, long metaLeaderEpoch) {
    return new RemoteClusterSlotSyncResponse(metaLeader, metaLeaderEpoch, false, false, null);
  }

  public static RemoteClusterSlotSyncResponse notUpgrade(
      String metaLeader, long metaLeaderEpoch) {
    return new RemoteClusterSlotSyncResponse(metaLeader, metaLeaderEpoch, true, true, null);
  }

  public static RemoteClusterSlotSyncResponse upgrade(
      String metaLeader, long metaLeaderEpoch, SlotTable slotTable) {
    return new RemoteClusterSlotSyncResponse(metaLeader, metaLeaderEpoch, true, true, slotTable);
  }

  public boolean isSyncOnLeader() {
    return syncOnLeader;
  }

  public boolean isSlotTableUpgrade() {
    return slotTableUpgrade;
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
    return "RemoteClusterSlotSyncResponse{" +
            "metaLeader='" + metaLeader + '\'' +
            ", metaLeaderEpoch=" + metaLeaderEpoch +
            ", syncOnLeader=" + syncOnLeader +
            ", slotTableUpgrade=" + slotTableUpgrade +
            ", slotTable=" + slotTable +
            '}';
  }
}
