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
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MultiClusterMetaServerConfig;
import com.alipay.sofa.registry.server.shared.constant.ExchangerModeEnum;
import com.alipay.sofa.registry.server.shared.meta.AbstractMetaLeaderExchanger;
import com.alipay.sofa.registry.store.api.meta.MultiClusterSyncRepository;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojian.xj
 * @version : RemoteClusterMetaExchanger.java, v 0.1 2022年04月16日 14:57 xiaojian.xj Exp $
 */
public class RemoteClusterMetaExchanger extends AbstractMetaLeaderExchanger {

  private static final Logger LOGGER =
      LoggerFactory.getLogger("MULTI-CLUSTER-CLIENT", "[Exchanger]");

  @Autowired private MultiClusterMetaServerConfig multiClusterMetaServerConfig;

  @Autowired private MultiClusterSyncRepository multiClusterSyncRepository;

  /** <dataCenter, syncInfo> */
  private volatile Map<String, MultiClusterSyncInfo> syncConfigMap = Maps.newConcurrentMap();

  public RemoteClusterMetaExchanger() {
    super(Exchange.REMOTE_CLUSTER_META, ExchangerModeEnum.REMOTE_DATA_CENTER, LOGGER);
  }

  @Override
  public int getRpcTimeoutMillis() {
    return multiClusterMetaServerConfig.getRemoteClusterRpcTimeoutMillis();
  }

  @Override
  public int getServerPort() {
    return multiClusterMetaServerConfig.getRemoteMetaServerPort();
  }

  @Override
  protected Collection<ChannelHandler> getClientHandlers() {
    return Collections.emptyList();
  }

  @Override
  protected Collection<String> getMetaServerDomains(String dataCenter) {
    MultiClusterSyncInfo info = syncConfigMap.get(dataCenter);
    if (info == null) {
      throw new RuntimeException(
          String.format("dataCenter: {} meta domain config not exist.", dataCenter));
    }
    return Collections.singleton(info.getRemoteMetaAddress());
  }

  /**
   * update and remove cluster infos according to db
   *
   * @return
   */
  public void refreshClusterInfos() {
    Set<MultiClusterSyncInfo> updates = multiClusterSyncRepository.queryAll();
    Set<String> removes = Sets.difference(syncConfigMap.keySet(), updates);

    synchronized (this) {
      for (MultiClusterSyncInfo update : updates) {
        syncConfigMap.put(update.getDataCenter(), update);
      }

      for (String remove : removes) {
        syncConfigMap.remove(remove);
        removeLeader(remove);
      }
    }
  }

  /**
   * get all remote clusters
   *
   * @return
   */
  public synchronized Set<String> getAllRemoteClusters() {
    return Sets.newHashSet(syncConfigMap.keySet());
  }
}
