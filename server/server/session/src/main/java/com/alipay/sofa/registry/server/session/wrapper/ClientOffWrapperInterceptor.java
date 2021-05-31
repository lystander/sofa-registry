/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.wrapper;

import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.StoreData;
import com.alipay.sofa.registry.common.model.store.StoreData.DataType;
import com.alipay.sofa.registry.common.model.store.Subscriber;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.filter.ProcessFilter;
import com.alipay.sofa.registry.server.session.provideData.FetchClientOffPodsService;
import com.alipay.sofa.registry.server.session.push.FirePushService;
import com.alipay.sofa.registry.server.session.registry.SessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientOffWrapperInterceptor.java, v 0.1 2021年05月28日 21:18 xiaojian.xj Exp $
 */
public class ClientOffWrapperInterceptor implements WrapperInterceptor<StoreData, Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOffWrapperInterceptor.class);

    @Autowired
    private FirePushService firePushService;

    @Resource
    private FetchClientOffPodsService fetchClientOffPodsService;

    @Override
    public Boolean invokeCodeWrapper(WrapperInvocation<StoreData, Boolean> invocation) throws Exception {
        BaseInfo storeData = (BaseInfo) invocation.getParameterSupplier().get();

        URL url = storeData.getSourceAddress();

        if (fetchClientOffPodsService.getClientOffPods().contains(url.getIpAddress())) {
            LOGGER.info("dataInfoId:{} ,url:{} match clientOff ips.", storeData.getDataInfoId(), url.getIpAddress());
            if (DataType.PUBLISHER == storeData.getDataType()) {
                // match client off pub.
                return true;
            }

            if (DataType.SUBSCRIBER == storeData.getDataType()) {
                fireSubscriberPushEmptyTask((Subscriber) storeData);
                return true;
            }
        }
        return invocation.proceed();
    }

    @Override
    public int getOrder() {
        return 300;
    }

    private void fireSubscriberPushEmptyTask(Subscriber subscriber) {
        // trigger empty data push
        firePushService.fireOnPushEmpty(subscriber);
    }
}