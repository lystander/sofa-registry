/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MultiClusterMetaServerConfig;
import com.alipay.sofa.registry.server.shared.constant.ExchangerModeEnum;
import com.alipay.sofa.registry.server.shared.meta.AbstractMetaLeaderExchanger;
import com.alipay.sofa.registry.store.api.meta.MultiClusterSyncRepository;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : RemoteClusterMetaExchanger.java, v 0.1 2022年04月16日 14:57 xiaojian.xj Exp $
 */
public class RemoteClusterMetaExchanger extends AbstractMetaLeaderExchanger {

  private static final Logger LOGGER = LoggerFactory.getLogger("MULTI-CLUSTER", "[Exchanger]");

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
      // todo xiaojian.xj
      throw new RuntimeException();
    }
    return Collections.singleton(info.getRemoteMetaAddress());
  }

  /**
   * update and remove cluster infos according to db
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
   * @return
   */
  public synchronized Set<String> getAllRemoteClusters() {
    return Sets.newHashSet(syncConfigMap.keySet());
  }
}
