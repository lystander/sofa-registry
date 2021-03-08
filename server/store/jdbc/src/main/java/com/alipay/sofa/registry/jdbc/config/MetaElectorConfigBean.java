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
package com.alipay.sofa.registry.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author xiaojian.xj
 * @version $Id: MetaElectorConfigBean.java, v 0.1 2021年03月17日 16:49 xiaojian.xj Exp $
 */
@ConfigurationProperties(prefix = MetaElectorConfigBean.PRE_FIX)
public class MetaElectorConfigBean implements MetaElectorConfig {

    public static final String PRE_FIX = "meta.server.elector";

    private String dataCenter;

    private long lockExpireDuration = 20 * 1000;

    @Override
    public String getDataCenter() {
        return dataCenter;
    }

    @Override
    public long getLockExpireDuration() {
        return lockExpireDuration;
    }

    /**
     * Setter method for property <tt>dataCenter</tt>.
     *
     * @param dataCenter value to be assigned to property dataCenter
     */
    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    /**
     * Setter method for property <tt>lockExpireDuration</tt>.
     *
     * @param lockExpireDuration value to be assigned to property lockExpireDuration
     */
    public void setLockExpireDuration(long lockExpireDuration) {
        this.lockExpireDuration = lockExpireDuration;
    }
}