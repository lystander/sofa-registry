/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.provide.data;

import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.server.meta.AbstractH2DbTestBase;
import com.alipay.sofa.registry.store.api.DBResponse;
import com.alipay.sofa.registry.store.api.OperationStatus;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerServiceTest.java, v 0.1 2021年05月31日 10:23 xiaojian.xj Exp $
 */
public class ClientManagerServiceTest extends AbstractH2DbTestBase {

    @Autowired
    private ClientManagerService clientManagerService;

    private final Set<String> clientOffSet  = Sets.newHashSet("1.1.1.1", "2.2.2.2");
    private final Set<String> clientOpenSet = Sets.newHashSet("2.2.2.2", "3.3.3.3");

    @Test
    public void testClientManager() throws InterruptedException {
        clientManagerService.becomeLeader();

        clientManagerService.clientOff(clientOffSet);

        Thread.sleep(2000);
        DBResponse<ProvideData> clientOffResponse = clientManagerService.queryClientOffSet();
        Assert.assertEquals(clientOffResponse.getOperationStatus(), OperationStatus.SUCCESS);
        ProvideData clientOffData = clientOffResponse.getEntity();
        Long v1 = clientOffData.getVersion();
        Set<String> set1 = (Set<String>) clientOffData.getProvideData().getObject();
        Assert.assertTrue(v1 > -1L);
        Assert.assertEquals(clientOffSet, set1);

        clientManagerService.clientOpen(clientOpenSet);
        Thread.sleep(2000);
        DBResponse<ProvideData> clientOpenResponse = clientManagerService.queryClientOffSet();
        Assert.assertEquals(clientOpenResponse.getOperationStatus(), OperationStatus.SUCCESS);
        ProvideData clientOpenData = clientOpenResponse.getEntity();
        Long v2 = clientOpenData.getVersion();
        Set<String> set2 = (Set<String>) clientOpenData.getProvideData().getObject();
        Assert.assertTrue(v2 > v1);
        Assert.assertEquals(Sets.difference(clientOffSet, clientOpenSet), set2);

    }
}