/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.bootstrap;

import com.alipay.sofa.registry.server.data.multi.cluster.exchanger.RemoteDataNodeExchanger;
import com.alipay.sofa.registry.server.data.multi.cluster.executor.MultiClusterExecutorManager;
import com.alipay.sofa.registry.server.data.multi.cluster.slot.MultiClusterSlotManager;
import com.alipay.sofa.registry.server.data.multi.cluster.slot.MultiClusterSlotManagerImpl;
import com.alipay.sofa.registry.server.data.multi.cluster.storage.MultiClusterDatumStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    }

    @Configuration
    public static class RemoteDataStorageConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MultiClusterSlotManager multiClusterSlotManager() {
            return new MultiClusterSlotManagerImpl();
        }

        @Bean
        public MultiClusterDatumStorage multiClusterDatumStorage() {
            return new MultiClusterDatumStorage();
        }
    }
}
