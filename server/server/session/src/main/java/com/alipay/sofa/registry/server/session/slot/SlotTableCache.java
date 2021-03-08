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

/**
 * 
 * @author yuzhi.lyz
 * @version v 0.1 2020-11-06 16:21 yuzhi.lyz Exp $
 */
public interface SlotTableCache {
	int slotOf(String dataInfoId);

	Slot getSlot(String dataInfoId);

	Slot getSlot(int slotId);

	String getLeader(String dataInfoId);

	String getLeader(int slotId);

	long getEpoch();

	boolean updateSlotTable(SlotTable slotTable);

	int slotNum();

	SlotTable currentSlotTable();
}
