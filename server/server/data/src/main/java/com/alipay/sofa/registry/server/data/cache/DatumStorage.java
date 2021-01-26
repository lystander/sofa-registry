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
package com.alipay.sofa.registry.server.data.cache;

import com.alipay.sofa.registry.common.model.ConnectId;
import com.alipay.sofa.registry.common.model.ProcessId;
import com.alipay.sofa.registry.common.model.PublisherVersion;
import com.alipay.sofa.registry.common.model.dataserver.Datum;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author yuzhi.lyz
 * @version v 0.1 2020-12-02 19:52 yuzhi.lyz Exp $
 */
public interface DatumStorage {
    /**
     * get datum by specific dataInfoId
     *
     * @param dataInfoId
     * @return
     */
    Datum get(String dataInfoId);

    DatumVersion getVersion(String dataInfoId);

    Map<String, DatumVersion> getVersions(int slotId);

    Map<String, Publisher> getByConnectId(ConnectId connectId);

    Map<String, Map<String, Publisher>> getPublishers(int slot);

    /**
     * get all datum
     *
     * @return
     */
    Map<String, Datum> getAll();

    DatumVersion putPublisher(Publisher publisher);

    DatumVersion createEmptyDatumIfAbsent(String dataInfoId, String dataCenter);

    Map<String, DatumVersion> clean(ProcessId sessionProcessId);

    DatumVersion remove(String dataInfoId, ProcessId sessionProcessId);

    DatumVersion remove(String dataInfoId, ProcessId sessionProcessId,
                        Map<String, PublisherVersion> removedPublishers);

    DatumVersion update(String dataInfoId, List<Publisher> updatedPublishers);

    Map<String, DatumSummary> getDatumSummary(int slotId, String sessionIpAddress);

    SlotChangeListener getSlotChangeListener();

    Set<ProcessId> getSessionProcessIds();

    Map<String, Integer> compact(long tombstoneTimestamp);

    void updateVersion(int slotId);

}
