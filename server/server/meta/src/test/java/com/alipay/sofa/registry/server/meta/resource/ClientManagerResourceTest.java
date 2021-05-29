/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.resource;

import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.server.meta.AbstractMetaServerTestBase;
import com.alipay.sofa.registry.server.meta.provide.data.ClientManagerService;
import com.alipay.sofa.registry.store.api.DBResponse;
import com.alipay.sofa.registry.store.api.OperationStatus;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.spy;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerResourceTest.java, v 0.1 2021年05月29日 17:13 xiaojian.xj Exp $
 */
public class ClientManagerResourceTest extends AbstractMetaServerTestBase {

    private ClientManagerResource clientManagerResource;

    private ClientManagerService clientManagerService = spy(new InMemoryClientManagerServiceRepo());

    private static final String CLIENT_OFF_STR = "1.1.1.1;2.2.2.2";
    private static final String CLIENT_OPEN_STR = "2.2.2.2;3.3.3.3";

    @Before
    public void beforeClientManagerResourceTest() {
        clientManagerResource = new ClientManagerResource().setClientManagerService(clientManagerService);
    }

    class InMemoryClientManagerServiceRepo implements ClientManagerService {

        private final AtomicLong version = new AtomicLong(0L);

        private final AtomicReference<ConcurrentHashMap.KeySetView> cache = new AtomicReference<>(new ConcurrentHashMap<>().newKeySet());

        @Override
        public boolean clientOpen(Set<String> ipSet) {
            version.incrementAndGet();
            return cache.get().removeAll(ipSet);
        }

        @Override
        public boolean clientOff(Set<String> ipSet) {
            version.incrementAndGet();
            return cache.get().addAll(ipSet);
        }

        @Override
        public DBResponse<ProvideData> queryClientOffSet() {

            ProvideData provideData = new ProvideData(new ServerDataBox(cache.get()),
                    ValueConstants.CLIENT_OFF_PODS_DATA_ID, version.get());
            return DBResponse.ok(provideData).build();
        }

        @Override
        public void becomeLeader() {

        }

        @Override
        public void loseLeader() {

        }
    }

    @Test
    public void testClientManager() {
        clientManagerResource.clientOff(CLIENT_OFF_STR);

        clientManagerResource.clientOpen(CLIENT_OPEN_STR);

        Map<String, Object> query = clientManagerResource.query();
        Set<String> ips = (Set<String>) query.get("ips");

        Assert.assertEquals(query.get("status"), OperationStatus.SUCCESS);
        Assert.assertEquals(query.get("version"), 2L);
        Assert.assertEquals(ips.size(), 1);
    }
}