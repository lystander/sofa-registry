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
import com.alipay.sofa.registry.common.model.slot.filter.RemoteSyncDataAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.multi.cluster.storage.MultiClusterDatumStorage;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.*;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

/**
 * cache of datum, providing query function to the upper module
 *
 * @author kezhu.wukz
 * @author qian.lqlq
 * @version $Id: DatumCache.java, v 0.1 2017-12-06 20:50 qian.lqlq Exp $
 */
public class DatumStorageDelegate implements DatumStorage {

  private final String localDataCenter;
  private final DatumStorage localDatumStorage;

  private final DatumStorage multiClusterDatumStorage;

  public DatumStorageDelegate(DataServerConfig dataServerConfig) {
    this.localDataCenter = dataServerConfig.getLocalDataCenter();
    this.localDatumStorage = new LocalDatumStorage(localDataCenter);
    this.multiClusterDatumStorage = new MultiClusterDatumStorage();
  }

  @Override
  public Set<String> allDataCenters() {
    Set<String> allDataCenters = Sets.newLinkedHashSetWithExpectedSize(6);
    allDataCenters.add(localDataCenter);
    allDataCenters.addAll(multiClusterDatumStorage.allDataCenters());
    return allDataCenters;
  }

  /**
   * get datum by specific dataCenter and dataInfoId
   *
   * @param dataCenter
   * @param dataInfoId
   * @return
   */
  @Override
  public Datum get(String dataCenter, String dataInfoId) {
    return storageOf(dataCenter).get(dataCenter, dataInfoId);
  }

  public void cleanLocal(String dataCenter, String dataInfoId) {
    localDatumStorage.removePublishers(dataCenter, dataInfoId, null);
  }

  @Override
  public DatumVersion getVersion(String dataCenter, String dataInfoId) {
    DatumVersion version = storageOf(dataCenter).getVersion(dataCenter, dataInfoId);
    return version;
  }

  @Override
  public Map<String, DatumVersion> getVersions(
      String dataCenter, int slotId, Collection<String> targetDataInfoIds) {
    return storageOf(dataCenter).getVersions(dataCenter, slotId, targetDataInfoIds);
  }

  @Override
  public DatumVersion updateVersion(String dataCenter, String dataInfoId) {
    return storageOf(dataCenter).updateVersion(dataCenter, dataInfoId);
  }

  /**
   * get all datum
   *
   * @return
   */
  public Map<String, Map<String, Datum>> getLocalAll() {
    Map<String, Map<String, Datum>> datumMap = new HashMap<>();
    datumMap.put(localDataCenter, localDatumStorage.getAll(localDataCenter));
    return datumMap;
  }

  // get without datum.version, it's more efficient than getDatum
  public Map<String, Map<String, List<Publisher>>> getLocalAllPublisher() {
    Map<String, Map<String, List<Publisher>>> datumMap = new HashMap<>();
    datumMap.put(localDataCenter, localDatumStorage.getAllPublisher(localDataCenter));
    return datumMap;
  }

  public Map<String, Map<String, Integer>> getLocalPubCount() {
    Map<String, Map<String, Integer>> map = Maps.newHashMap();
    map.put(localDataCenter, localDatumStorage.getPubCount(localDataCenter));
    return map;
  }

  @Override
  public Map<String, Publisher> getByConnectId(ConnectId connectId) {
    return localDatumStorage.getByConnectId(connectId);
  }

  @Override
  public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slot) {
    return storageOf(dataCenter).getPublishers(dataCenter, slot);
  }

  @Override
  public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slot, SyncSlotAcceptorManager acceptorManager) {
    return storageOf(dataCenter).getPublishers(dataCenter, slot, acceptorManager);
  }

  /**
   * get all datum
   *
   * @return
   * @param dataCenter
   */
  @Override
  public Map<String, Datum> getAll(String dataCenter) {
    return storageOf(dataCenter).getAll(dataCenter);
  }

  @Override
  public Map<String, List<Publisher>> getAllPublisher(String dataCenter) {
    return storageOf(dataCenter).getAllPublisher(dataCenter);
  }

  @Override
  public Map<String, Integer> getPubCount(String dataCenter) {
    return storageOf(dataCenter).getPubCount(dataCenter);
  }

  @Override
  public void putPublisherGroups(String dataCenter, int slotId) {
    storageOf(dataCenter).putPublisherGroups(dataCenter, slotId);
  }

  @Override
  public DatumVersion putPublisher(String dataCenter, Publisher publisher) {
    return storageOf(dataCenter).putPublisher(dataCenter, publisher);
  }

  @Override
  public DatumVersion putPublisher(String dataCenter, String dataInfoId, List<Publisher> updatedPublishers) {
    return storageOf(dataCenter).putPublisher(dataCenter, dataInfoId, updatedPublishers);
  }

  @Override
  public DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId) {
    return storageOf(dataCenter).createEmptyDatumIfAbsent(dataCenter, dataInfoId);
  }

  @Override
  public Map<String, DatumVersion> cleanBySessionId(String dataCenter, int slotId, ProcessId sessionProcessId,
                                                    CleanContinues cleanContinues) {
    return storageOf(dataCenter).cleanBySessionId(dataCenter, slotId, sessionProcessId, cleanContinues);
  }

  @Override
  public boolean removePublisherGroups(String dataCenter, int slotId) {
    return storageOf(dataCenter).removePublisherGroups(dataCenter, slotId);
  }

  @Override
  public DatumVersion removePublishers(String dataCenter, String dataInfoId, ProcessId sessionProcessId) {
    return storageOf(dataCenter).removePublishers(dataCenter, dataInfoId, sessionProcessId);
  }

  @Override
  public DatumVersion removePublishers(String dataCenter, String dataInfoId, ProcessId sessionProcessId,
                                       Map<String, RegisterVersion> removedPublishers) {
    return storageOf(dataCenter).removePublishers(dataCenter, dataInfoId, sessionProcessId, removedPublishers);
  }

  @Override
  public Map<String, Map<String, DatumSummary>> getDatumSummary(String dataCenter, int slotId, Set<String> sessions) {
    return storageOf(dataCenter).getDatumSummary(dataCenter, slotId, sessions);
  }

  @Override
  public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId) {
    return storageOf(dataCenter).getDatumSummary(dataCenter, slotId);
  }

  @Override
  public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId, SyncSlotAcceptorManager acceptorManager) {
    return storageOf(dataCenter).getDatumSummary(dataCenter, slotId, acceptorManager);
  }

  @Override
  public SlotChangeListener getSlotChangeListener(boolean localDataCenter) {
    return localDataCenter
        ? localDatumStorage.getSlotChangeListener(true)
        : multiClusterDatumStorage.getSlotChangeListener(false);
  }

  @Override
  public Set<ProcessId> getSessionProcessIds(String dataCenter) {
    return storageOf(dataCenter).getSessionProcessIds(dataCenter);
  }

  @Override
  public Map<String, Integer> compact(String dataCenter, long tombstoneTimestamp) {
    return storageOf(dataCenter).compact(dataCenter, tombstoneTimestamp);
  }

  @Override
  public int tombstoneNum(String dataCenter) {
    return storageOf(dataCenter).tombstoneNum(dataCenter);
  }

  @Override
  public Map<String, DatumVersion> updateVersion(String dataCenter, int slotId) {
    return storageOf(dataCenter).updateVersion(dataCenter, slotId);
  }


  private DatumStorage storageOf(String dataCenter) {
    return StringUtils.equalsIgnoreCase(localDataCenter, dataCenter) ? localDatumStorage : multiClusterDatumStorage;
  }
}
