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
package com.alipay.sofa.registry.common.model.multi.cluster;

import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import java.io.Serializable;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteSlotTableStatus.java, v 0.1 2022年05月06日 15:31 xiaojian.xj Exp $
 */
public class RemoteSlotTableStatus implements Serializable {
  private static final long serialVersionUID = 1023079253275268831L;

  /** slotTableEpoch */
  private final long slotTableEpoch;

  /** when data.slotTableEpoch > meta.slotTableEpoch, slotTableEpochConflict=true */
  private final boolean slotTableEpochConflict;

  /** if slot table upgrade */
  private final boolean slotTableUpgrade;

  /** slot table will be null if syncOnLeader=false or slotTableUpgrade=false */
  private final SlotTable slotTable;

  /** segmentZones */
  private Set<String> segmentZones;

  public RemoteSlotTableStatus(
      long slotTableEpoch,
      boolean slotTableUpgrade,
      boolean slotTableEpochConflict,
      SlotTable slotTable,
      Set<String> segmentZones) {
    this.slotTableEpoch = slotTableEpoch;
    this.slotTableUpgrade = slotTableUpgrade;
    this.slotTableEpochConflict = slotTableEpochConflict;
    this.slotTable = slotTable;
    this.segmentZones = segmentZones;
  }

  public static RemoteSlotTableStatus conflict(SlotTable slotTable, Set<String> segmentZones) {
    return new RemoteSlotTableStatus(slotTable.getEpoch(), false, true, slotTable, segmentZones);
  }

  public static RemoteSlotTableStatus notUpgrade(long slotTableEpoch, Set<String> segmentZones) {
    return new RemoteSlotTableStatus(slotTableEpoch, false, false, null, segmentZones);
  }

  public static RemoteSlotTableStatus upgrade(SlotTable slotTable, Set<String> segmentZones) {
    ParaCheckUtil.checkNotNull(slotTable, "slotTable");
    return new RemoteSlotTableStatus(slotTable.getEpoch(), true, false, slotTable, segmentZones);
  }

  /**
   * Getter method for property <tt>slotTableEpoch</tt>.
   *
   * @return property value of slotTableEpoch
   */
  public long getSlotTableEpoch() {
    return slotTableEpoch;
  }

  public boolean isSlotTableUpgrade() {
    return slotTableUpgrade;
  }

  public boolean isSlotTableEpochConflict() {
    return slotTableEpochConflict;
  }

  /**
   * Getter method for property <tt>slotTable</tt>.
   *
   * @return property value of slotTable
   */
  public SlotTable getSlotTable() {
    return slotTable;
  }

  /**
   * Getter method for property <tt>segmentZones</tt>.
   *
   * @return property value of segmentZones
   */
  public Set<String> getSegmentZones() {
    return segmentZones;
  }

  @Override
  public String toString() {
    return "RemoteSlotTableStatus{"
        + "slotTableEpoch="
        + slotTableEpoch
        + ", slotTableEpochConflict="
        + slotTableEpochConflict
        + ", slotTableUpgrade="
        + slotTableUpgrade
        + ", slotTable="
        + slotTable
        + '}';
  }
}
