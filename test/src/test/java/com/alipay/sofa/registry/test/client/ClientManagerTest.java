/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.test.client;

import com.alipay.sofa.registry.client.api.registration.PublisherRegistration;
import com.alipay.sofa.registry.common.model.CommonResponse;
import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.server.meta.resource.ClientManagerResource;
import com.alipay.sofa.registry.server.session.provideData.FetchClientOffPodsService;
import com.alipay.sofa.registry.server.session.store.DataStore;
import com.alipay.sofa.registry.store.api.OperationStatus;
import com.alipay.sofa.registry.test.BaseIntegrationTest;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerTest.java, v 0.1 2021年05月31日 11:40 xiaojian.xj Exp $
 */
@RunWith(SpringRunner.class)
public class ClientManagerTest extends BaseIntegrationTest {

    private ClientManagerResource     clientManagerResource;
    private FetchClientOffPodsService fetchClientOffPodsService;
    private DataStore                 sessionDataStore;

    private       String localAddress    = sessionChannel.getLocalAddress().getHostString();
    private final String CLIENT_OFF_STR  = "1.1.1.1;2.2.2.2;" + localAddress;
    private final String CLIENT_OPEN_STR = "2.2.2.2;3.3.3.3;" + localAddress;

    private final Set<String> CLIENT_OFF_SET  = Sets.newHashSet(CLIENT_OFF_STR.split(";"));
    private final Set<String> CLIENT_OPEN_SET = Sets.newHashSet(CLIENT_OPEN_STR.split(";"));

    @Before
    public void beforeClientManagerTest() {
        clientManagerResource =
                metaApplicationContext.getBean("clientManagerResource", ClientManagerResource.class);
        sessionDataStore = sessionApplicationContext.getBean("sessionDataStore", DataStore.class);
        fetchClientOffPodsService = sessionApplicationContext.getBean("fetchClientOffPodsService", FetchClientOffPodsService.class);

    }

    @Test
    public void testClientOff() throws InterruptedException, TimeoutException {
        String dataId = "test-meta-client-off-dataId-" + System.currentTimeMillis();
        String value = "test meta client off";

        DataInfo dataInfo = new DataInfo(ValueConstants.DEFAULT_INSTANCE_ID, dataId, ValueConstants.DEFAULT_GROUP);

        /**
         * register
         */
        PublisherRegistration registration = new PublisherRegistration(dataId);
        com.alipay.sofa.registry.client.api.Publisher register = registryClient1.register(registration, value);
        Thread.sleep(2000L);

        Collection<Publisher> datas = sessionDataStore.getDatas(dataInfo.getDataInfoId());
        boolean exist = false;
        for (Publisher publisher : datas) {
            if (StringUtils.equals(publisher.getSourceAddress().getIpAddress(), localAddress)) {
                exist = true;
            }
        }
        Assert.assertTrue(exist);

        String countResult =
                dataChannel
                        .getWebTarget()
                        .path("digest/datum/count")
                        .request(APPLICATION_JSON)
                        .get(String.class);
        assertTrue(countResult.contains("[Publisher] size of publisher in DefaultDataCenter is 1"));


        /**
         * client off
         */
        CommonResponse response = clientManagerResource.clientOff(CLIENT_OFF_STR);
        Assert.assertTrue(response.isSuccess());

        // check session client off list
        waitConditionUntilTimeOut(() -> fetchClientOffPodsService.getClientOffPods().equals(CLIENT_OFF_SET), 5000);

        register.republish(value);
        Thread.sleep(2000L);

        // check session local cache
        Collection<Publisher> datasAfterClientOff = sessionDataStore.getDatas(dataInfo.getDataInfoId());
        boolean existAfterClientOff = false;
        for (Publisher publisher : datasAfterClientOff) {
            if (StringUtils.equals(publisher.getSourceAddress().getIpAddress(), localAddress)) {
                existAfterClientOff = true;
            }
        }
        Assert.assertTrue(!existAfterClientOff);

        // check data publisher
        countResult =
                dataChannel
                        .getWebTarget()
                        .path("digest/datum/count")
                        .request(APPLICATION_JSON)
                        .get(String.class);
        assertTrue(countResult.contains("[Publisher] size of publisher in DefaultDataCenter is 0"));

        /**
         * client open
         */
        response = clientManagerResource.clientOpen(CLIENT_OPEN_STR);
        Assert.assertTrue(response.isSuccess());

        SetView<String> difference = Sets.difference(CLIENT_OFF_SET, CLIENT_OPEN_SET);
        waitConditionUntilTimeOut(() -> fetchClientOffPodsService.getClientOffPods().equals(difference), 5000);

        Map<String, Object> query = clientManagerResource.query();
        Assert.assertEquals(query.get("status"), OperationStatus.SUCCESS);
        Assert.assertEquals(query.get("ips"), fetchClientOffPodsService.getClientOffPods());

        Thread.sleep(3000);

        // check session local cache
        Collection<Publisher> datasAfterClientOpen = sessionDataStore.getDatas(dataInfo.getDataInfoId());
        boolean existAfterClientOpen = false;
        for (Publisher publisher : datasAfterClientOpen) {
            if (StringUtils.equals(publisher.getSourceAddress().getIpAddress(), localAddress)) {
                existAfterClientOpen = true;
            }
        }
        Assert.assertTrue(existAfterClientOpen);

        // check data publisher
        countResult =
                dataChannel
                        .getWebTarget()
                        .path("digest/datum/count")
                        .request(APPLICATION_JSON)
                        .get(String.class);
        assertTrue(countResult.contains("[Publisher] size of publisher in DefaultDataCenter is 1"));


    }
}