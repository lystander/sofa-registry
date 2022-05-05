/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.bootstrap;

import com.alipay.sofa.registry.server.meta.bootstrap.config.MultiClusterMetaServerConfig;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MultiClusterMetaServerConfigBean;
import com.alipay.sofa.registry.server.meta.multi.cluster.DefaultMultiClusterSlotTableSyncer;
import com.alipay.sofa.registry.server.meta.multi.cluster.remote.RemoteClusterMetaExchanger;
import com.alipay.sofa.registry.server.meta.multi.cluster.remote.RemoteClusterSlotSyncHandler;
import com.alipay.sofa.registry.server.meta.resource.MultiClusterSyncResource;
import com.alipay.sofa.registry.server.shared.remoting.AbstractServerHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterMetaServerConfiguration.java, v 0.1 2022年04月29日 20:56 xiaojian.xj Exp $
 */
@Configuration
@EnableConfigurationProperties
public class MultiClusterMetaServerConfiguration {

    @Configuration
    public static class MultiClusterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MultiClusterMetaServerConfig multiClusterMetaServerConfig() {
            return new MultiClusterMetaServerConfigBean();
        }

        @Bean
        public DefaultMultiClusterSlotTableSyncer multiClusterSlotTableSyncer() {
            return new DefaultMultiClusterSlotTableSyncer();
        }

        @Bean
        public RemoteClusterMetaExchanger remoteClusterMetaExchanger() {
            return new RemoteClusterMetaExchanger();
        }

        @Bean
        public MultiClusterSyncResource multiClusterSyncResource() {
            return new MultiClusterSyncResource();
        }
    }

    @Configuration
    public static class MultiClusterRemotingConfiguration {

        @Bean(name = "remoteMetaServerHandlers")
        public Collection<AbstractServerHandler> metaServerHandlers() {
            Collection<AbstractServerHandler> list = new ArrayList<>();
            list.add(remoteClusterSlotSyncHandler());
            return list;
        }

        @Bean
        public RemoteClusterSlotSyncHandler remoteClusterSlotSyncHandler() {
            return new RemoteClusterSlotSyncHandler();
        }
    }

}
