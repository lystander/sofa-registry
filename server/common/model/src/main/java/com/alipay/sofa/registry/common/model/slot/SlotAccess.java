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
package com.alipay.sofa.registry.common.model.slot;

import com.alipay.sofa.registry.util.ParaCheckUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuzhi.lyz
 * @version v 0.1 2020-10-30 10:34 yuzhi.lyz Exp $
 */
public final class SlotAccess implements Serializable {

  public enum Status {
    Accept,
    Migrating,
    Moved,
    MisMatch,
    UnSupport,
  }

  private final int slotId;
  private final Status status;
  private final long slotTableEpoch;
  private final long slotLeaderEpoch;

  public SlotAccess(int slotId, long slotTableEpoch, Status status, long slotLeaderEpoch) {
    this.slotTableEpoch = slotTableEpoch;
    this.slotId = slotId;
    this.status = status;
    this.slotLeaderEpoch = slotLeaderEpoch;
  }

  public boolean isMoved() {
    return status == Status.Moved;
  }

  public boolean isMigrating() {
    return status == Status.Migrating;
  }

  public boolean isAccept() {
    return status == Status.Accept;
  }

  public boolean isMisMatch() {
    return status == Status.MisMatch;
  }

  public Status getStatus() {
    return status;
  }

  public int getSlotId() {
    return slotId;
  }

  public long getSlotTableEpoch() {
    return slotTableEpoch;
  }

  public long getSlotLeaderEpoch() {
    return slotLeaderEpoch;
  }

  public static SlotAccess mergeAccess(List<SlotAccess> slotAccessList) {
    ParaCheckUtil.checkNotEmpty(slotAccessList, "slotAccessList");

    int count = 0;
    for (SlotAccess slotAccess : slotAccessList) {
      if (!slotAccess.isAccept()) {
        return slotAccess;
      }
      count++;
      if (count == slotAccessList.size()) {
        return slotAccess;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return "SlotAccess{"
        + "slotId="
        + slotId
        + ", status="
        + status
        + ", tableEpoch="
        + slotTableEpoch
        + ", leaderEpoch="
        + slotLeaderEpoch
        + '}';
  }
}
