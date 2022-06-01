/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.bootstrap;

import com.alipay.sofa.registry.common.model.slot.filter.RemoteSyncDataAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.AppRevisionPublish;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.ServiceAppsPublish;
import com.alipay.sofa.registry.server.data.multi.cluster.dataserver.handler.MultiClusterSlotDiffDigestRequestHandler;
import com.alipay.sofa.registry.server.data.multi.cluster.dataserver.handler.MultiClusterSlotDiffPublisherRequestHandler;
import com.alipay.sofa.registry.server.data.multi.cluster.exchanger.RemoteDataNodeExchanger;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.slot.MultiClusterSlotManager;
import com.alipay.sofa.registry.server.data.multi.cluster.slot.MultiClusterSlotManagerImpl;
import com.alipay.sofa.registry.server.data.multi.cluster.storage.MultiClusterDatumStorage;
import com.alipay.sofa.registry.server.data.remoting.dataserver.handler.SlotFollowerDiffPublisherRequestHandler;
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
 * @version : MultiClusterDataConfiguration.java, v 0.1 2022年05月06日 15:46 xiaojian.xj Exp $
 */
@Configuration
@EnableConfigurationProperties
public class MultiClusterDataConfiguration {

    @Configuration
    public static class RemoteDataConfigConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public MultiClusterDataServerConfigBean multiClusterDataServerConfig() {
            return new MultiClusterDataServerConfigBean();
        }

        @Bean
        public MultiClusterExecutorManager multiClusterExecutorManager(MultiClusterDataServerConfig multiClusterDataServerConfig) {
            return new MultiClusterExecutorManager(multiClusterDataServerConfig);
        }
    }

    @Configuration
    public static class RemoteClusterExchangerConfiguration {

        @Bean
        public RemoteDataNodeExchanger remoteDataNodeExchanger() {
            return new RemoteDataNodeExchanger();
        }

        @Bean(name = "remoteDataServerHandlers")
        public Collection<AbstractServerHandler> remoteDataServerHandlers() {
            Collection<AbstractServerHandler> list = new ArrayList<>();
            list.add(multiClusterSlotDiffDigestRequestHandler());
            list.add(multiClusterSlotDiffPublisherRequestHandler());
            return list;
        }

        @Bean
        public AbstractServerHandler multiClusterSlotDiffDigestRequestHandler() {
            return new MultiClusterSlotDiffDigestRequestHandler();
        }

        @Bean
        public AbstractServerHandler multiClusterSlotDiffPublisherRequestHandler() {
            return new MultiClusterSlotDiffPublisherRequestHandler();
        }
    }

    @Configuration
    public static class RemoteDataStorageConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MultiClusterSlotManager multiClusterSlotManager() {
            return new MultiClusterSlotManagerImpl();
        }

        @Bean
        public SyncSlotAcceptorManager remoteSyncDataAcceptorManager() {
            return new RemoteSyncDataAcceptorManager();
        }

        @Bean
        public AppRevisionPublish appRevisionPublish() {
            return new AppRevisionPublish();
        }

        @Bean
        public ServiceAppsPublish serviceAppsPublish() {
            return new ServiceAppsPublish();
        }
    }
}
