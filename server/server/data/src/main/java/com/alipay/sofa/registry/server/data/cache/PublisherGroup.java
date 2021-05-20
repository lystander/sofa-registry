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
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.ProcessIdCache;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.common.model.store.WordCache;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.util.DatumVersionUtil;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.collections.MapUtils;

/**
 * @author yuzhi.lyz
 * @version v 0.1 2020-12-02 20:26 yuzhi.lyz Exp $
 */
public final class PublisherGroup {
  private static final Logger LOGGER = LoggerFactory.getLogger(PublisherGroup.class);

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  final String dataInfoId;

  final String dataCenter;

  final String dataId;

  final String instanceId;

  final String group;

  // if the delete publisher from session, mark unpub
  final Map<String /*registerId*/, PublisherEnvelope> pubMap = Maps.newConcurrentMap();

  private volatile long version;

  PublisherGroup(String dataInfoId, String dataCenter) {
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    this.dataInfoId = WordCache.getWordCache(dataInfoId);
    this.dataCenter = WordCache.getWordCache(dataCenter);
    this.dataId = WordCache.getWordCache(dataInfo.getDataId());
    this.instanceId = WordCache.getWordCache(dataInfo.getInstanceId());
    this.group = WordCache.getWordCache(dataInfo.getGroup());
    if (DatumVersionUtil.useConfregVersionGen()) {
      this.version = DatumVersionUtil.confregNextId(0);
    } else {
      this.version = DatumVersionUtil.nextId();
    }
  }

  DatumVersion getVersion() {
    return new DatumVersion(version);
  }

  Datum toDatum() {
    Datum datum = new Datum();
    datum.setDataCenter(dataCenter);
    datum.setDataId(dataId);
    datum.setDataInfoId(dataInfoId);
    datum.setGroup(group);
    datum.setInstanceId(instanceId);
    long ver;
    List<Publisher> list = new ArrayList<>(pubMap.size());
    lock.readLock().lock();
    try {
      ver = this.version;
      for (PublisherEnvelope envelope : pubMap.values()) {
        if (envelope.isPub()) {
          list.add(envelope.publisher);
        }
      }
    } finally {
      lock.readLock().unlock();
    }
    datum.setVersion(ver);
    list.forEach(p -> datum.addPublisher(p));
    return datum;
  }

  List<Publisher> getPublishers() {
    List<Publisher> list = new ArrayList<>(pubMap.size());
    for (PublisherEnvelope envelope : pubMap.values()) {
      if (envelope.isPub()) {
        list.add(envelope.publisher);
      }
    }
    return list;
  }

  Map<String, Publisher> getByConnectId(ConnectId connectId) {
    Map<String, Publisher> map = Maps.newHashMap();
    for (PublisherEnvelope p : pubMap.values()) {
      if (p.isConnectId(connectId)) {
        map.put(p.publisher.getRegisterId(), p.publisher);
      }
    }
    return map;
  }

  DatumVersion updateVersion() {
    final boolean useConfreg = DatumVersionUtil.useConfregVersionGen();
    lock.writeLock().lock();
    try {
      if (useConfreg) {
        this.version = DatumVersionUtil.confregNextId(this.version);
      } else {
        this.version = DatumVersionUtil.nextId();
      }
      return new DatumVersion(version);
    } finally {
      lock.writeLock().unlock();
    }
  }

  private boolean tryAddPublisher(Publisher publisher) {
    PublisherEnvelope exist = pubMap.get(publisher.getRegisterId());

    final RegisterVersion registerVersion = publisher.registerVersion();
    if (exist != null) {
      if (exist.registerVersion.equals(registerVersion)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "[AddSameVer] {}, {}, exist={}, add={}",
              publisher.getDataInfoId(),
              publisher.getRegisterId(),
              exist.registerVersion,
              publisher.registerVersion());
        }
        return false;
      }
      if (!exist.registerVersion.orderThan(registerVersion)) {
        LOGGER.warn(
            "[AddOlderVer] {}, {}, exist={}, add={}",
            publisher.getDataInfoId(),
            publisher.getRegisterId(),
            exist.registerVersion,
            publisher.registerVersion());
        return false;
      }
    }
    pubMap.put(publisher.getRegisterId(), PublisherEnvelope.of(publisher));
    return true;
  }

  DatumVersion addPublisher(Publisher publisher) {
    publisher.setSessionProcessId(ProcessIdCache.cache(publisher.getSessionProcessId()));
    lock.writeLock().lock();
    try {
      if (tryAddPublisher(publisher)) {
        return updateVersion();
      }
      return null;
    } finally {
      lock.writeLock().unlock();
    }
  }

  DatumVersion clean(ProcessId sessionProcessId, CleanContinues cleanContinues) {
    sessionProcessId = ProcessIdCache.cache(sessionProcessId);
    if (sessionProcessId == null) {
      // not check continues
      lock.writeLock().lock();
      try {
        final int size = pubMap.size();
        if (size == 0) {
          return null;
        }
        cleanContinues.onClean(size);
        pubMap.clear();
        return updateVersion();
      } finally {
        lock.writeLock().unlock();
      }
    }
    // collect the pub of the processId without lock
    Map<String, PublisherEnvelope> cleans = Maps.newHashMapWithExpectedSize(64);
    for (Map.Entry<String, PublisherEnvelope> pub : pubMap.entrySet()) {
      PublisherEnvelope envelope = pub.getValue();
      if (envelope.isPub() && envelope.sessionProcessId.equals(sessionProcessId)) {
        cleans.put(pub.getKey(), envelope);
      }
    }
    // clean modify the version, need to lock
    lock.writeLock().lock();
    try {
      boolean modified = false;
      for (Map.Entry<String, PublisherEnvelope> clean : cleans.entrySet()) {
        if (!cleanContinues.continues()) {
          break;
        }
        if (pubMap.remove(clean.getKey(), clean.getValue())) {
          cleanContinues.onClean(1);
          modified = true;
        }
      }
      return modified ? updateVersion() : null;
    } finally {
      lock.writeLock().unlock();
    }
  }

  DatumVersion remove(ProcessId sessionProcessId, Map<String, RegisterVersion> removedPublishers) {
    sessionProcessId = ProcessIdCache.cache(sessionProcessId);
    if (MapUtils.isEmpty(removedPublishers)) {
      return null;
    }
    lock.writeLock().lock();
    try {
      boolean modified = false;
      for (Map.Entry<String, RegisterVersion> e : removedPublishers.entrySet()) {
        final String registerId = e.getKey();
        final RegisterVersion removedVer = e.getValue();

        final PublisherEnvelope existing = pubMap.get(registerId);
        if (existing == null || !existing.isPub()) {
          // the removedPublishers is from pubMap, but now notExist/unpub/pubByOtherSession
          continue;
        }
        if (existing.registerVersion.equals(removedVer)) {
          // sync from leader
          if (sessionProcessId == null) {
            pubMap.remove(registerId);
            modified = true;
            continue;
          }
          if (sessionProcessId.equals(existing.sessionProcessId)) {
            // syn from session, mark unpub with higher registerTimestamp
            pubMap.put(
                registerId,
                PublisherEnvelope.unpubOf(removedVer.incrRegisterTimestamp(), sessionProcessId));
            modified = true;
          } else {
            LOGGER.warn(
                "[RemovePidModified] {}, {}, exist={}/{}, expect={}/{}",
                dataInfoId,
                registerId,
                existing.registerVersion,
                existing.sessionProcessId,
                removedVer,
                sessionProcessId);
          }
        } else {
          // the item has modified after diff, ignored
          LOGGER.warn(
              "[RemoveVerModified] {}, {}, exist={}, expect={}",
              dataInfoId,
              registerId,
              existing.registerVersion,
              removedVer);
        }
      }
      return modified ? updateVersion() : null;
    } finally {
      lock.writeLock().unlock();
    }
  }

  DatumVersion put(List<Publisher> puts) {
    for (Publisher p : puts) {
      ParaCheckUtil.checkNotNull(p.getSessionProcessId(), "publisher.sessionProcessId");
      ParaCheckUtil.checkEquals(p.getDataInfoId(), dataInfoId, "publisher.dataInfoId");
      p.setSessionProcessId(ProcessIdCache.cache(p.getSessionProcessId()));
    }
    lock.writeLock().lock();
    try {
      boolean modified = false;
      for (Publisher publisher : puts) {
        if (tryAddPublisher(publisher)) {
          modified = true;
        }
      }
      if (modified) {
        return updateVersion();
      }
      return null;
    } finally {
      lock.writeLock().unlock();
    }
  }

  DatumSummary getSummary(String sessionIpAddress) {
    Map<String /*registerId*/, RegisterVersion> publisherVersions =
        Maps.newHashMapWithExpectedSize(64);
    for (Map.Entry<String, PublisherEnvelope> e : pubMap.entrySet()) {
      PublisherEnvelope envelope = e.getValue();
      RegisterVersion v = envelope.getVersionIfPub();
      if (v == null) {
        continue;
      }
      if (sessionIpAddress == null
          || sessionIpAddress.equals(envelope.sessionProcessId.getHostAddress())) {
        publisherVersions.put(e.getKey(), v);
      }
    }
    return new DatumSummary(dataInfoId, publisherVersions);
  }

  Collection<ProcessId> getSessionProcessIds() {
    Set<ProcessId> set = Sets.newHashSet();
    for (PublisherEnvelope e : pubMap.values()) {
      if (e.isPub()) {
        set.add(e.sessionProcessId);
      }
    }
    return set;
  }

  int compact(long tombstoneTimestamp) {
    // compact not modify the version, no need to lock
    int count = 0;
    Map<String, PublisherEnvelope> compacts = Maps.newHashMap();
    for (Map.Entry<String, PublisherEnvelope> e : pubMap.entrySet()) {
      final PublisherEnvelope envelope = e.getValue();
      if (!envelope.isPub() && envelope.tombstoneTimestamp <= tombstoneTimestamp) {
        compacts.put(e.getKey(), envelope);
      }
    }

    for (Map.Entry<String, PublisherEnvelope> compact : compacts.entrySet()) {
      if (pubMap.remove(compact.getKey(), compact.getValue())) {
        count++;
      }
    }
    return count;
  }

  int tombstoneNum() {
    int count = 0;
    for (PublisherEnvelope envelope : pubMap.values()) {
      if (!envelope.isPub()) {
        count++;
      }
    }
    return count;
  }

  @Override
  public String toString() {
    return StringFormatter.format(
        "PubGroup{{},size={},ver={}}", dataInfoId, pubMap.size(), version);
  }
}
