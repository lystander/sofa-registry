/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.wrapper;

import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.StoreData;
import com.alipay.sofa.registry.common.model.store.StoreData.DataType;
import com.alipay.sofa.registry.common.model.store.Subscriber;
import com.alipay.sofa.registry.server.session.filter.ProcessFilter;
import com.alipay.sofa.registry.server.session.push.FirePushService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientOffWrapperInterceptor.java, v 0.1 2021年05月28日 21:18 xiaojian.xj Exp $
 */
public class ClientOffWrapperInterceptor implements WrapperInterceptor<StoreData, Boolean> {

    @Autowired
    private FirePushService firePushService;

    @Resource
    private ProcessFilter<BaseInfo> clientOffMatchProcessFilter;

    @Override
    public Boolean invokeCodeWrapper(WrapperInvocation<StoreData, Boolean> invocation) throws Exception {
        BaseInfo storeData = (BaseInfo) invocation.getParameterSupplier().get();


        if (clientOffMatchProcessFilter.match(storeData)) {
            if (DataType.PUBLISHER == storeData.getDataType()) {
                // match blacklist stop pub.
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