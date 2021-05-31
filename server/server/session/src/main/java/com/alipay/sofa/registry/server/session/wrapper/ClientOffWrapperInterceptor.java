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
package com.alipay.sofa.registry.server.session.wrapper;

import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.StoreData;
import com.alipay.sofa.registry.common.model.store.StoreData.DataType;
import com.alipay.sofa.registry.common.model.store.Subscriber;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.provideData.FetchClientOffPodsService;
import com.alipay.sofa.registry.server.session.push.FirePushService;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojian.xj
 * @version $Id: ClientOffWrapperInterceptor.java, v 0.1 2021年05月28日 21:18 xiaojian.xj Exp $
 */
public class ClientOffWrapperInterceptor implements WrapperInterceptor<StoreData, Boolean> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientOffWrapperInterceptor.class);

  @Autowired private FirePushService firePushService;

  @Resource private FetchClientOffPodsService fetchClientOffPodsService;

  @Override
  public Boolean invokeCodeWrapper(WrapperInvocation<StoreData, Boolean> invocation)
      throws Exception {
    BaseInfo storeData = (BaseInfo) invocation.getParameterSupplier().get();

    URL url = storeData.getSourceAddress();

    if (fetchClientOffPodsService.getClientOffPods().contains(url.getIpAddress())) {
      LOGGER.info(
          "dataInfoId:{} ,url:{} match clientOff ips.",
          storeData.getDataInfoId(),
          url.getIpAddress());
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
