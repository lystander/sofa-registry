/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.multi.cluster.remote;

import com.alipay.sofa.registry.common.model.elector.LeaderInfo;
import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.Server;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.remoting.exchange.RequestException;
import com.alipay.sofa.registry.remoting.exchange.message.Request;
import com.alipay.sofa.registry.remoting.exchange.message.Response;
import com.alipay.sofa.registry.remoting.jersey.JerseyClient;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.server.shared.constant.ExchangerModeEnum;
import com.alipay.sofa.registry.server.shared.meta.AbstractMetaLeaderExchanger;
import com.alipay.sofa.registry.server.shared.remoting.ClientSideExchanger;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteClusterMetaExchanger.java, v 0.1 2022年04月16日 14:57 xiaojian.xj Exp $
 */
public class RemoteClusterMetaExchanger extends AbstractMetaLeaderExchanger {

    private static final Logger LOGGER = LoggerFactory.getLogger("MULTI-CLUSTER", "[Exchanger]");

    @Autowired
    private MetaServerConfig metaServerConfig;

    @Resource(name = "remoteMetaClientHandlers")
    private Collection<ChannelHandler> remoteMetaClientHandlers;


    /** <dataCenter, syncInfo> */
    private Map<String, MultiClusterSyncInfo> syncConfigMap = Maps.newConcurrentMap();

    protected RemoteClusterMetaExchanger() {
        super(Exchange.REMOTE_CLUSTER_META, ExchangerModeEnum.REMOTE_DATA_CENTER, LOGGER);
    }

    @Override
    public int getRpcTimeoutMillis() {
        return metaServerConfig.getRemoteClusterRpcTimeoutMillis();
    }

    @Override
    public int getServerPort() {
        return metaServerConfig.getRemoteMetaServerPort();
    }

    @Override
    protected Collection<ChannelHandler> getClientHandlers() {
        return remoteMetaClientHandlers;
    }

    @Override
    protected Collection<String> getMetaServerDomains(String dataCenter) {
        MultiClusterSyncInfo info = syncConfigMap.get(dataCenter);
        if (info == null) {
            throw new RuntimeException();
        }
        return Collections.singleton(info.getRemoteMetaAddress());
    }
}
