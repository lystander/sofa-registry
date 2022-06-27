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
package com.alipay.sofa.registry.server.data.multi.cluster.executor;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.MultiClusterDataServerConfig;
import com.alipay.sofa.registry.task.KeyedThreadPoolExecutor;
import com.alipay.sofa.registry.task.MetricsableThreadPoolExecutor;
import com.alipay.sofa.registry.util.NamedThreadFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojian.xj
 * @version : MultiClusterExecutorManager.java, v 0.1 2022年05月10日 20:47 xiaojian.xj Exp $
 */
public class MultiClusterExecutorManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultiClusterExecutorManager.class);

  private final KeyedThreadPoolExecutor remoteSyncLeaderExecutor;

  private final KeyedThreadPoolExecutor syncAppRevisionExecutor;

  private final KeyedThreadPoolExecutor syncServiceMappingExecutor;

  private final MetricsableThreadPoolExecutor remoteSlotSyncProcessorExecutor;

  private static final String REMOTE_SYNC_LEADER_EXECUTOR = "REMOTE_SYNC_LEADER_EXECUTOR";

  private static final String SYNC_APP_REVISION_EXECUTOR = "SYNC_APP_REVISION_EXECUTOR";

  private static final String SYNC_SERVICE_MAPPING_EXECUTOR = "SYNC_SERVICE_MAPPING_EXECUTOR";

  private static final String REMOTE_SLOT_SYNC_PROCESSOR_EXECUTOR =
      "REMOTE_SLOT_SYNC_PROCESSOR_EXECUTOR";

  private Map<String, KeyedThreadPoolExecutor> reportExecutors = new HashMap<>();

  private Map<String, MetricsableThreadPoolExecutor> metricsableExecutors = new HashMap<>();

  public MultiClusterExecutorManager(MultiClusterDataServerConfig multiClusterDataServerConfig) {
    remoteSyncLeaderExecutor =
        reportExecutors.computeIfAbsent(
            REMOTE_SYNC_LEADER_EXECUTOR,
            k ->
                new KeyedThreadPoolExecutor(
                    REMOTE_SYNC_LEADER_EXECUTOR,
                    multiClusterDataServerConfig.getRemoteSyncSlotLeaderExecutorThreadSize(),
                    multiClusterDataServerConfig.getRemoteSyncSlotLeaderExecutorQueueSize()));

    syncAppRevisionExecutor =
        reportExecutors.computeIfAbsent(
            SYNC_APP_REVISION_EXECUTOR,
            k ->
                new KeyedThreadPoolExecutor(
                    SYNC_APP_REVISION_EXECUTOR,
                    multiClusterDataServerConfig.getSyncAppRevisionExecutorThreadSize(),
                    multiClusterDataServerConfig.getSyncAppRevisionExecutorQueueSize()));

    syncServiceMappingExecutor =
        reportExecutors.computeIfAbsent(
            SYNC_SERVICE_MAPPING_EXECUTOR,
            k ->
                new KeyedThreadPoolExecutor(
                    SYNC_SERVICE_MAPPING_EXECUTOR,
                    multiClusterDataServerConfig.getSyncServiceMappingExecutorThreadSize(),
                    multiClusterDataServerConfig.getSyncServiceMappingExecutorQueueSize()));

    remoteSlotSyncProcessorExecutor =
        metricsableExecutors.computeIfAbsent(
            REMOTE_SLOT_SYNC_PROCESSOR_EXECUTOR,
            k ->
                new MetricsableThreadPoolExecutor(
                    "RemoteSlotSyncProcessorExecutor",
                    multiClusterDataServerConfig.getRemoteSlotSyncRequestExecutorMinPoolSize(),
                    multiClusterDataServerConfig.getRemoteSlotSyncRequestExecutorMaxPoolSize(),
                    300,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(
                        multiClusterDataServerConfig.getRemoteSlotSyncRequestExecutorQueueSize()),
                    new NamedThreadFactory("RemoteSlotSyncProcessorExecutor", true)));
  }

  /**
   * Getter method for property <tt>remoteSyncLeaderExecutor</tt>.
   *
   * @return property value of remoteSyncLeaderExecutor
   */
  public KeyedThreadPoolExecutor getRemoteSyncLeaderExecutor() {
    return remoteSyncLeaderExecutor;
  }

  /**
   * Getter method for property <tt>syncAppRevisionExecutor</tt>.
   *
   * @return property value of syncAppRevisionExecutor
   */
  public KeyedThreadPoolExecutor getSyncAppRevisionExecutor() {
    return syncAppRevisionExecutor;
  }

  /**
   * Getter method for property <tt>syncServiceMappingExecutor</tt>.
   *
   * @return property value of syncServiceMappingExecutor
   */
  public KeyedThreadPoolExecutor getSyncServiceMappingExecutor() {
    return syncServiceMappingExecutor;
  }

  /**
   * Getter method for property <tt>remoteSlotSyncProcessorExecutor</tt>.
   *
   * @return property value of remoteSlotSyncProcessorExecutor
   */
  public MetricsableThreadPoolExecutor getRemoteSlotSyncProcessorExecutor() {
    return remoteSlotSyncProcessorExecutor;
  }
}
