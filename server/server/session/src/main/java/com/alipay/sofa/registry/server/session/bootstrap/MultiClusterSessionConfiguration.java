/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.bootstrap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author xiaojian.xj
 * @version : MultiClusterSessionConfiguration.java, v 0.1 2022年05月06日 16:16 xiaojian.xj Exp $
 */
@Configuration
@EnableConfigurationProperties
public class MultiClusterSessionConfiguration {

    @Configuration
    public static class RemoteSessionStorageConfiguration {

        @Bean
        public MultiClusterSlotTableCache multiClusterSlotTableCache() {
            return new MultiClusterSlotTableCacheImpl();
        }
    }

}
