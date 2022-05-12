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
import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.dataserver.Datum;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuzhi.lyz
 * @version v 0.1 2020-12-02 19:52 yuzhi.lyz Exp $
 */
public interface DatumStorage {

  /**
   * get datum by specific dataInfoId
   *
   * @param dataCenter
   * @param dataInfoId
   * @return
   */
  Datum get(String dataCenter, String dataInfoId);

  DatumVersion getVersion(String dataCenter, String dataInfoId);

  Map<String, DatumVersion> getVersions(
      String dataCenter, int slotId, Collection<String> targetDatInfoIds);

  Map<String, Publisher> getByConnectId(ConnectId connectId);

  Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slot);

  /**
   * get all datum
   *
   * @return
   */
  Map<String, Datum> getAll(String dataCenter);

  Map<String, List<Publisher>> getAllPublisher(String dataCenter);

  Map<String, Integer> getPubCount(String dataCenter);

  DatumVersion put(String dataCenter, Publisher publisher);

  DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId);

  Map<String, DatumVersion> clean(
      String dataCenter, int slotId, ProcessId sessionProcessId, CleanContinues cleanContinues);

  DatumVersion remove(String dataCenter, String dataInfoId, ProcessId sessionProcessId);

  DatumVersion remove(
      String dataCenter,
      String dataInfoId,
      ProcessId sessionProcessId,
      Map<String, RegisterVersion> removedPublishers);

  DatumVersion put(String dataCenter, String dataInfoId, List<Publisher> updatedPublishers);

  Map<String, Map<String, DatumSummary>> getDatumSummary(String dataCenter, int slotId, Set<String> sessions);

  Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId);

  default SlotChangeListener getSlotChangeListener(){
    return null;
  }

  default Set<ProcessId> getSessionProcessIds(String dataCenter){
    return Collections.emptySet();
  }

  default Map<String, Integer> compact(String dataCenter, long tombstoneTimestamp){
    return Collections.emptyMap();
  }

  default int tombstoneNum(String dataCenter){
    return 0;
  }

  Map<String, DatumVersion> updateVersion(String dataCenter, int slotId);

  DatumVersion updateVersion(String dataCenter, String dataInfoId);
}
