/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterDataServerConfigBean.java, v 0.1 2022年05月09日 17:37 xiaojian.xj Exp $
 */
@ConfigurationProperties(prefix = MultiClusterDataServerConfigBean.PREFIX)
public class MultiClusterDataServerConfigBean implements MultiClusterDataServerConfig {

    public static final String PREFIX = "data.remote.server";

    private volatile int syncRemoteSlotLeaderIntervalSecs = 6;

    @Override
    public int getSyncRemoteSlotLeaderIntervalSecs() {
        return syncRemoteSlotLeaderIntervalSecs;
    }

    /**
     * Setter method for property <tt>syncRemoteSlotLeaderIntervalSecs</tt>.
     *
     * @param syncRemoteSlotLeaderIntervalSecs value to be assigned to property syncRemoteSlotLeaderIntervalSecs
     */
    public void setSyncRemoteSlotLeaderIntervalSecs(int syncRemoteSlotLeaderIntervalSecs) {
        this.syncRemoteSlotLeaderIntervalSecs = syncRemoteSlotLeaderIntervalSecs;
    }
}
