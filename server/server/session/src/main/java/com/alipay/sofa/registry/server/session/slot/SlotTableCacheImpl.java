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
package com.alipay.sofa.registry.server.session.slot;

import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.shared.slot.SlotTableRecorder;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuzhi.lyz
 * @version v 0.1 2020-11-11 10:07 yuzhi.lyz Exp $
 */
public final class SlotTableCacheImpl implements SlotTableCache {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlotTableCacheImpl.class);

  private final SlotFunction slotFunction = SlotFunctionRegistry.getFunc();

  private volatile SlotTable slotTable = SlotTable.INIT;

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  @Autowired(required = false)
  private List<SlotTableRecorder> recorders;

  @Override
  public int slotOf(String dataInfoId) {
    return slotFunction.slotOf(dataInfoId);
  }

  @Override
  public Slot getSlot(String dataInfoId) {
    int slotId = slotOf(dataInfoId);
    lock.readLock().lock();
    try {
      return slotTable.getSlot(slotId);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Slot getSlot(int slotId) {
    lock.readLock().lock();
    try {
      return slotTable.getSlot(slotId);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public String getLeader(int slotId) {
    lock.readLock().lock();
    try {
      final Slot slot = slotTable.getSlot(slotId);
      return slot == null ? null : slot.getLeader();
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public long getEpoch() {
    lock.readLock().lock();
    try {
      return slotTable.getEpoch();
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean updateSlotTable(SlotTable slotTable) {
    lock.writeLock().lock();
    final long curEpoch;
    try {
      curEpoch = this.slotTable.getEpoch();
      if (curEpoch >= slotTable.getEpoch()) {
        LOGGER.info("skip update, current={}, update={}", curEpoch, slotTable.getEpoch());
        return false;
      }
      recordSlotTable(slotTable);
      this.slotTable = slotTable;
    } finally {
      lock.writeLock().unlock();
    }
    checkForSlotTable(curEpoch);
    return true;
  }

  private void recordSlotTable(SlotTable slotTable) {
    if (recorders == null) {
      return;
    }
    for (SlotTableRecorder recorder : recorders) {
      if (recorder != null) {
        recorder.record(slotTable);
      }
    }
  }

  protected void checkForSlotTable(long curEpoch) {
    for (Slot slot : this.slotTable.getSlots()) {
      if (StringUtils.isBlank(slot.getLeader())) {
        LOGGER.error("[NoLeader] {}", slot);
      }
    }
    LOGGER.info(
        "updating slot table, expect={}, current={}, {}",
        slotTable.getEpoch(),
        curEpoch,
        this.slotTable);
  }

  @Override
  public SlotTable getCurrentSlotTable() {
    lock.readLock().lock();
    try {
      return new SlotTable(slotTable.getEpoch(), slotTable.getSlots());
    } finally {
      lock.readLock().unlock();
    }
  }

  @VisibleForTesting
  protected SlotTableCacheImpl setRecorders(List<SlotTableRecorder> recorders) {
    this.recorders = recorders;
    return this;
  }
}
