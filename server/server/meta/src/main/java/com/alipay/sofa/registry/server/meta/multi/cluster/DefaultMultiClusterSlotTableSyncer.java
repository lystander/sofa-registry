/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.multi.cluster;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.meta.MetaLeaderService;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.WakeUpLoopRunnable;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSlotTableSyncer.java, v 0.1 2022年04月15日 16:46 xiaojian.xj Exp $
 */
public class DefaultMultiClusterSlotTableSyncer implements MultiClusterSlotTableSyncer {

    private static final Logger LOGGER = LoggerFactory.getLogger("MULTI-CLUSTER", "[SlotTableSyncer]");

    private Map<String, SlotTable> slotTableMap = Maps.newConcurrentMap();

    private SlotTableWatcher watcher = new SlotTableWatcher();

    @Autowired
    private MetaLeaderService metaLeaderService;

    @Autowired
    private MetaServerConfig metaServerConfig;

    @PostConstruct
    public void init() {
        ConcurrentUtils.createDaemonThread("multi_cluster_slot_table", watcher).start();
        metaLeaderService.registerListener(this);
    }

    @Override
    public void becomeLeader() {

    }

    @Override
    public void loseLeader() {

    }

    private final class SlotTableWatcher extends WakeUpLoopRunnable {

        @Override
        public void runUnthrowable() {

        }

        @Override
        public int getWaitingMillis() {
            return metaServerConfig.getMultiClusterWatcherMillis();
        }
    }
}
