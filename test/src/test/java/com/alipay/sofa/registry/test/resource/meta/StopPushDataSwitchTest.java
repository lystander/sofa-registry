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
package com.alipay.sofa.registry.test.resource.meta;

import static com.alipay.sofa.registry.client.constants.ValueConstants.DEFAULT_GROUP;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;

import com.alipay.sofa.registry.client.api.model.RegistryType;
import com.alipay.sofa.registry.client.api.model.UserData;
import com.alipay.sofa.registry.client.api.registration.PublisherRegistration;
import com.alipay.sofa.registry.client.api.registration.SubscriberRegistration;
import com.alipay.sofa.registry.core.model.Result;
import com.alipay.sofa.registry.core.model.ScopeEnum;
import com.alipay.sofa.registry.server.session.provideData.FetchStopPushService;
import com.alipay.sofa.registry.test.BaseIntegrationTest;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xuanbei
 * @since 2019/1/14
 */
@RunWith(SpringRunner.class)
public class StopPushDataSwitchTest extends BaseIntegrationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(StopPushDataSwitchTest.class);

  @Test
  public void testStopPushDataSwitch() throws Exception {
    // open stop push switch
    ParaCheckUtil.checkNotNull(metaChannel, "metaChannel");
    assertTrue(
        getMetaChannel()
            .getWebTarget()
            .path("stopPushDataSwitch/open")
            .request(APPLICATION_JSON)
            .get(Result.class)
            .isSuccess());
    Thread.sleep(2000L);

    AtomicReference<String> dataIdResult = new AtomicReference<>();
    AtomicReference<UserData> userDataResult = new AtomicReference<>();

    // register Publisher & Subscriber, Subscriber get no data
    String dataId = "test-dataId-" + System.currentTimeMillis();
    String value = "test stop publish data switch";

    LOGGER.info("dataidIn:" + dataId);

    FetchStopPushService fetchStopPushService =
        sessionApplicationContext.getBean("fetchStopPushService", FetchStopPushService.class);

    LOGGER.info("fetchStopPushService.isStopPushSwitch:" + fetchStopPushService.isStopPushSwitch());
    waitConditionUntilTimeOut(fetchStopPushService::isStopPushSwitch, 6000);

    PublisherRegistration registration = new PublisherRegistration(dataId);
    registryClient1.register(registration, value);
    Thread.sleep(2000L);

    SubscriberRegistration subReg =
        new SubscriberRegistration(
            dataId,
            (dataIdOb, data) -> {
              LOGGER.info("sub:" + data);
              dataIdResult.set(dataIdOb);
              userDataResult.set(data);
            });
    subReg.setScopeEnum(ScopeEnum.dataCenter);
    registryClient1.register(subReg);
    Thread.sleep(3000L);
    assertNull(dataIdResult.get());

    // close stop push switch
    assertTrue(
        getMetaChannel()
            .getWebTarget()
            .path("stopPushDataSwitch/close")
            .request(APPLICATION_JSON)
            .get(Result.class)
            .isSuccess());

    waitConditionUntilTimeOut(() -> dataIdResult.get() != null, 6000);

    // Subscriber get data, test data
    assertEquals(dataId, dataIdResult.get());
    assertEquals(LOCAL_REGION, userDataResult.get().getLocalZone());
    assertEquals(1, userDataResult.get().getZoneData().size());
    assertEquals(1, userDataResult.get().getZoneData().values().size());
    assertEquals(true, userDataResult.get().getZoneData().containsKey(LOCAL_REGION));
    assertEquals(1, userDataResult.get().getZoneData().get(LOCAL_REGION).size());
    assertEquals(value, userDataResult.get().getZoneData().get(LOCAL_REGION).get(0));

    // unregister Publisher & Subscriber
    registryClient1.unregister(dataId, DEFAULT_GROUP, RegistryType.SUBSCRIBER);
    registryClient1.unregister(dataId, DEFAULT_GROUP, RegistryType.PUBLISHER);
  }
}
