/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.exchanger;

import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.server.data.bootstrap.MultiClusterDataServerConfig;
import com.alipay.sofa.registry.server.shared.remoting.ClientSideExchanger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteDataNodeExchanger.java, v 0.1 2022年05月10日 20:30 xiaojian.xj Exp $
 */
public class RemoteDataNodeExchanger extends ClientSideExchanger {

    @Autowired
    private MultiClusterDataServerConfig multiClusterDataServerConfig;

    public RemoteDataNodeExchanger() {
        super(Exchange.DATA_SERVER_TYPE);
    }

    @Override
    public int getRpcTimeoutMillis() {
        return multiClusterDataServerConfig.getSyncRemoteSlotLeaderTimeoutMillis();
    }

    @Override
    public int getServerPort() {
        return multiClusterDataServerConfig.getSyncRemoteSlotLeaderPort();
    }

    @Override
    public int getConnNum() {
        return multiClusterDataServerConfig.getSyncRemoteSlotLeaderConnNum();
    }

    // todo xiaojian.xj handler data change notify
    @Override
    protected Collection<ChannelHandler> getClientHandlers() {
        return null;
    }
}
