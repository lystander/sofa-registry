/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.slot;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSlotMetrics.java, v 0.1 2022年05月09日 16:30 xiaojian.xj Exp $
 */
public class MultiClusterSlotMetrics {
    private MultiClusterSlotMetrics() {}

    private static final Gauge REMOTE_LEADER_ASSIGN_GAUGE =
            Gauge.build()
                    .namespace("data")
                    .subsystem("slot")
                    .name("remote_leader_assign_total")
                    .help("leader assign")
                    .labelNames("dataCenter")
                    .register();

    private static final Histogram REMOTE_LEADER_SYNCING_HISTOGRAM =
            Histogram.build()
                    .namespace("data")
                    .subsystem("slot")
                    .name("remote_leader_syncing_secs")
                    .help("syncing in seconds.")
                    .labelNames("dataCenter", "slot")
                    .buckets(3, 5, 10, 20, 30, 60, 120, 180, 240)
                    .register();

    private static final Gauge REMOTE_LEADER_SYNCING_GAUGE =
            Gauge.build()
                    .namespace("data")
                    .subsystem("slot")
                    .name("remote_leader_syncing_total")
                    .help("count remote leader is syncing")
                    .labelNames("dataCenter", "slot")
                    .register();

    static void observeRemoteLeaderAssignGauge(String dataCenter, int num) {
        REMOTE_LEADER_ASSIGN_GAUGE.labels(dataCenter).set(num);
    }

    static void observeRemoteLeaderSyncingStart(String dataCenter, int slotId) {
        REMOTE_LEADER_SYNCING_GAUGE.labels(dataCenter, String.valueOf(slotId)).set(1);
    }

    static void observeRemoteLeaderSyncingFinish(String dataCenter, int slotId) {
        REMOTE_LEADER_SYNCING_GAUGE.labels(dataCenter, String.valueOf(slotId)).set(0);
    }

    static void observeRemoteLeaderSyncingHistogram(String dataCenter, int slotId, long millis) {
        REMOTE_LEADER_SYNCING_HISTOGRAM.labels(dataCenter, String.valueOf(slotId)).observe(millis / 1000d);
    }

}
