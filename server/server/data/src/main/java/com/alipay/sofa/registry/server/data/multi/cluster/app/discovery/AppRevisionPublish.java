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
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;


import com.alipay.sofa.registry.common.model.DataInfoIdGenerator;
import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.filter.BaseSyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.common.model.store.AppRevision;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.jdbc.convertor.AppRevisionDomainConvertor;
import com.alipay.sofa.registry.jdbc.domain.AppRevisionDomain;
import com.alipay.sofa.registry.store.api.meta.EntryNotify;
import com.alipay.sofa.registry.store.api.repository.AppRevisionRepository;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * @author xiaojian.xj
 * @version : AppRevisionPublish.java, v 0.1 2022年05月25日 20:49 xiaojian.xj Exp $
 */
public class AppRevisionPublish extends MetadataSlotChangeListener<MetadataVersion>
    implements EntryNotify<AppRevisionDomain> {

  @Autowired private AppRevisionRepository appRevisionRepository;

  @PostConstruct
  public void init() {
    appRevisionRepository.registerNotify(this);
  }

  @Override
  protected int waitingMillis() {
    return dataServerConfig.getSlotSyncAppRevisionIntervalSecs() * 1000;
  }

  @Override
  protected SyncSlotAcceptorManager getSlotAcceptorManager() {
    return new BaseSyncSlotAcceptorManager(Sets.newHashSet(MultiValueConstants.APP_REVISION_GROUP_ACCEPTOR));
  }

  @Override
  protected KeyedThreadPoolExecutor getExecutor() {
    return multiClusterExecutorManager.getSyncAppRevisionExecutor();
  }

  @Override
  public void notify(AppRevisionDomain entry) {
    String pubDataInfoId = buildDataInfoId(entry.getRevision());
    int slotId = slotFunction.slotOf(pubDataInfoId);
    MetadataLoadState state = slotLeaderStatus.get(slotId);

    AppRevision revision = AppRevisionDomainConvertor.convert2Revision(entry);
    Publisher publisher =
        buildPub(
            pubDataInfoId, buildPubVersion(revision.getRevision()), buildServerDataBox(revision));

    pub2Datum(
        state.getSlotTableEpoch(),
        slotId,
        state.getSlotLeaderEpoch(),
        Collections.singletonList(publisher));
  }

  @Override
  protected void dealWithPub(
      Set<MetadataVersion> toBeAdd, long slotTableEpoch, int slotId, long slotLeaderEpoch) {
    if (CollectionUtils.isEmpty(toBeAdd)) {
      return;
    }

    try {
      List<Object> pubs = Lists.newArrayListWithExpectedSize(toBeAdd.size());
      for (MetadataVersion metadata : toBeAdd) {
        DataInfo dataInfo = DataInfo.valueOf(metadata.dataInfoId);
        AppRevision revision = appRevisionRepository.queryRevision(dataInfo.getDataId());
        pubs.add(
            buildPub(
                metadata.dataInfoId,
                buildPubVersion(dataInfo.getDataId()),
                buildServerDataBox(revision)));
      }
      pub2Datum(slotTableEpoch, slotId, slotLeaderEpoch, pubs);

    } catch (Throwable throwable) {
      LOGGER.error(
          "[dealWithPub]pub pubs error, pubs:{}, slotTableEpoch:{}, slotId:{}, slotLeaderEpoch:{}",
          toBeAdd,
          slotTableEpoch,
          slotId,
          slotLeaderEpoch,
          throwable);
    }
  }

  /**
   * @param summary
   * @param value
   * @return
   */
  @Override
  protected boolean needRePub(DatumSummary summary, MetadataVersion value) {
    Map<String, RegisterVersion> publisherVersions = summary.getPublisherVersions();

    RegisterVersion registerVersion = publisherVersions.get(getRegisterId(value.dataInfoId));
    return registerVersion == null;
  }

  @Override
  protected String buildDataInfoId(String revision) {
    return DataInfoIdGenerator.revisionId(revision);
  }

  @Override
  protected Map<String, MetadataVersion> queryMetadata(int slotId) {
    Set<String> revisions = appRevisionRepository.getAllRevisions();

    Map<String, MetadataVersion> query = Maps.newHashMap();
    for (String revision : revisions) {
      String dataInfoId = buildDataInfoId(revision);
      if (slotFunction.slotOf(dataInfoId) == slotId) {
        query.put(dataInfoId, new MetadataVersion(dataInfoId));
      }
    }
    return query;
  }

  private long buildPubVersion(String revision) {
    return md5HashFunction.hash(revision);
  }

  private List<ServerDataBox> buildServerDataBox(AppRevision revision) {
    ServerDataBox serverDataBox = new ServerDataBox(ServerDataBox.getBytes(revision));
    return Collections.singletonList(serverDataBox);
  }
}
