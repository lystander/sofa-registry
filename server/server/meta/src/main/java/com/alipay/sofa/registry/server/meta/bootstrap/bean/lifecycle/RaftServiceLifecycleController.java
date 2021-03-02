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
package com.alipay.sofa.registry.server.meta.bootstrap.bean.lifecycle;

import com.alipay.sofa.registry.jraft.annotation.RaftReferenceContainer;
import com.alipay.sofa.registry.jraft.annotation.RaftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author chen.zhu
 * <p>
 * Dec 14, 2020
 */
public class RaftServiceLifecycleController {

    @Autowired
    private RaftAnnotationBeanPostProcessor processor;

    @Autowired
    private ApplicationContext              applicationContext;

    @PostConstruct
    public void postConstruct() {
        registerRaftServices();
        replaceRaftReferences();
    }

    private void replaceRaftReferences() {
        Map<String, Object> targetBeans = applicationContext.getBeansWithAnnotation(RaftReferenceContainer.class);
        targetBeans.forEach((beanName, bean)-> {
            processor.processRaftReference(bean);
        });
    }

    private void registerRaftServices() {
        Map<String, Object> targetBeans = applicationContext.getBeansWithAnnotation(RaftService.class);
        targetBeans.forEach((beanName, bean)->{
            processor.registerBeanAsRaftService(bean);
        });
    }
}
