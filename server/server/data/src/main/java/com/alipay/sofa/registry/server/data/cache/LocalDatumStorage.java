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
import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * @author yuzhi.lyz
 * @version v 0.1 2020-12-02 19:40 yuzhi.lyz Exp $
 */
public final class LocalDatumStorage implements DatumStorage {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatumStorage.class);

  @Autowired
  private DataServerConfig dataServerConfig;

  private final BaseDatumStorage storage = new BaseDatumStorage(dataServerConfig.getLocalDataCenter(), LOGGER);

  @Override
  public Datum get(String dataCenter, String dataInfoId) {
    return storage.get(dataCenter, dataInfoId);
  }

  @Override
  public DatumVersion getVersion(String dataCenter, String dataInfoId) {
    return storage.getVersion(dataCenter, dataInfoId);
  }

  @Override
  public Map<String, DatumVersion> getVersions(String dataCenter, int slotId, Collection<String> targetDataInfoIds) {
    return storage.getVersions(dataCenter, slotId, targetDataInfoIds);
  }

  @Override
  public Map<String, Datum> getAll(String dataCenter) {
    return storage.getAll(dataCenter);
  }

  @Override
  public Map<String, List<Publisher>> getAllPublisher(String dataCenter) {
    return storage.getAllPublisher(dataCenter);
  }

  @Override
  public Map<String, Integer> getPubCount(String dataCenter) {
    return storage.getPubCount(dataCenter);
  }

  @Override
  public Map<String, Publisher> getByConnectId(ConnectId connectId) {
    return storage.getByConnectId(connectId);
  }

  @Override
  public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slotId) {
    return storage.getPublishers(dataCenter, slotId);
  }

  @Override
  public DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId) {
    return storage.createEmptyDatumIfAbsent(dataCenter, dataInfoId);
  }

  @Override
  public Map<String, DatumVersion> clean(
          String dataCenter, int slotId, ProcessId sessionProcessId, CleanContinues cleanContinues) {
    return storage.clean(dataCenter, slotId, sessionProcessId, cleanContinues);
  }

  // only for http testapi
  @Override
  public DatumVersion remove(String dataCenter, String dataInfoId, ProcessId sessionProcessId) {
    return storage.remove(dataCenter, dataInfoId, sessionProcessId);
  }

  @Override
  public DatumVersion put(String dataCenter, String dataInfoId, List<Publisher> publishers) {
    return storage.put(dataCenter, dataInfoId, publishers);
  }

  @Override
  public DatumVersion put(String dataCenter, Publisher publisher) {
    return put(dataCenter, publisher.getDataInfoId(), Collections.singletonList(publisher));
  }

  @Override
  public DatumVersion remove(
      String dataCenter,
      String dataInfoId,
      ProcessId sessionProcessId,
      Map<String, RegisterVersion> removedPublishers) {
    return storage.remove(dataCenter, dataInfoId, sessionProcessId, removedPublishers);
  }

  @Override
  public Map<String, Map<String, DatumSummary>> getDatumSummary(String dataCenter, int slotId, Set<String> sessions) {
    return storage.getDatumSummary(dataCenter, slotId, sessions);
  }

  @Override
  public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId) {
    return storage.getDatumSummary(dataCenter, slotId);
  }

  @Override
  public SlotChangeListener getSlotChangeListener() {
    return new SlotListener();
  }

  @Override
  public Set<ProcessId> getSessionProcessIds(String dataCenter) {
    Set<ProcessId> ids = Sets.newHashSet();
    publisherGroupsMap.values().forEach(g -> ids.addAll(g.getSessionProcessIds()));
    return ids;
  }

  @Override
  public Map<String, Integer> compact(String dataCenter, long tombstoneTimestamp) {
    Map<String, Integer> compacts = Maps.newHashMap();
    publisherGroupsMap.values().forEach(g -> compacts.putAll(g.compact(tombstoneTimestamp)));
    return compacts;
  }

  @Override
  public int tombstoneNum(String dataCenter) {
    int count = 0;
    for (PublisherGroups groups : publisherGroupsMap.values()) {
      count += groups.tombstoneNum();
    }
    return count;
  }

  @Override
  public Map<String, DatumVersion> updateVersion(String dataCenter, int slotId) {
    PublisherGroups groups = publisherGroupsMap.get(slotId);
    if (groups == null) {
      return Collections.emptyMap();
    }
    return groups.updateVersion();
  }

  @Override
  public DatumVersion updateVersion(String dataCenter, String dataInfoId) {
    PublisherGroups groups = getPublisherGroups(dataInfoId);
    return groups == null ? null : groups.updateVersion(dataInfoId);
  }

  private final class SlotListener implements SlotChangeListener {

    @Override
    public void onSlotAdd(String dataCenter, int slotId, Slot.Role role) {
      publisherGroupsMap.computeIfAbsent(
          slotId,
          k -> {
            PublisherGroups groups = new PublisherGroups(dataCenter);
            LOGGER.info(
                "{} add publisherGroup {}, role={}, slotNum={}",
                dataCenter,
                slotId,
                role,
                publisherGroupsMap.size());
            return groups;
          });
    }

    @Override
    public void onSlotRemove(String dataCenter, int slotId, Slot.Role role) {
      boolean removed = publisherGroupsMap.remove(slotId) != null;
      LOGGER.info(
          "{}, remove publisherGroup {}, removed={}, role={}, slotNum={}",
          dataCenter,
          slotId,
          removed,
          role,
          publisherGroupsMap.size());
    }
  }
}
