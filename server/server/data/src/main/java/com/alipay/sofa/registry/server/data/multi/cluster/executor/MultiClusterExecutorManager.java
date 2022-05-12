/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.multi.cluster.executor;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.MultiClusterDataServerConfig;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaojian.xj
 * @version : MultiClusterExecutorManager.java, v 0.1 2022年05月10日 20:47 xiaojian.xj Exp $
 */
public class MultiClusterExecutorManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultiClusterExecutorManager.class);

  private final KeyedThreadPoolExecutor remoteSyncLeaderExecutor;

  private static final String REMOTE_SYNC_LEADER_EXECUTOR = "REMOTE_SYNC_LEADER_EXECUTOR";

  private Map<String, KeyedThreadPoolExecutor> reportExecutors = new HashMap<>();

  public MultiClusterExecutorManager(MultiClusterDataServerConfig multiClusterDataServerConfig) {
    remoteSyncLeaderExecutor =
        reportExecutors.computeIfAbsent(
            REMOTE_SYNC_LEADER_EXECUTOR,
            k ->
                new KeyedThreadPoolExecutor(
                    REMOTE_SYNC_LEADER_EXECUTOR,
                    multiClusterDataServerConfig.getRemoteSyncSlotLeaderExecutorThreadSize(),
                    multiClusterDataServerConfig.getRemoteSyncSlotLeaderExecutorQueueSize()));
  }

  /**
   * Getter method for property <tt>remoteSyncLeaderExecutor</tt>.
   *
   * @return property value of remoteSyncLeaderExecutor
   */
  public KeyedThreadPoolExecutor getRemoteSyncLeaderExecutor() {
    return remoteSyncLeaderExecutor;
  }
}
