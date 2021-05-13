/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.jdbc.repository.impl;

import com.alipay.sofa.registry.common.model.metaserver.ClientManagerPods;
import com.alipay.sofa.registry.jdbc.config.DefaultCommonConfig;
import com.alipay.sofa.registry.jdbc.mapper.ClientManagerPodsMapper;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.store.api.meta.ClientManagerPodsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerPodsJdbcRepository.java, v 0.1 2021年05月12日 19:27 xiaojian.xj Exp $
 */
public class ClientManagerPodsJdbcRepository implements ClientManagerPodsRepository {

    private static final Logger LOG = LoggerFactory.getLogger("META-PROVIDEDATA", "[ClientManager]");


    @Autowired
    private DefaultCommonConfig defaultCommonConfig;

    @Autowired
    private ClientManagerPodsMapper clientManagerPodsMapper;


    @Override
    public boolean clientOpen(Set<String> ipSet) {
        return false;
    }

    @Override
    public boolean clientOff(Set<String> ipSet) {
        return false;
    }

    @Override
    public List<ClientManagerPods> queryAll() {
        return null;
    }

    @Override
    public List<ClientManagerPods> queryAfterThan(long maxId) {
        return null;
    }
}