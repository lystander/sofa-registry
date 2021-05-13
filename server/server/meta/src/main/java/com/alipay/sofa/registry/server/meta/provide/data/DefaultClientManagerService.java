/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.provide.data;

import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.store.api.DBResponse;
import com.alipay.sofa.registry.store.api.meta.ClientManagerPodsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version $Id: DefaultClientManagerService.java, v 0.1 2021年05月12日 15:16 xiaojian.xj Exp $
 */
public class DefaultClientManagerService implements ClientManagerService {

    @Autowired
    private ClientManagerPodsRepository clientManagerPodsRepository;

    /**
     * client open
     * @param ipSet
     * @return
     */
    @Override
    public DBResponse<ProvideData> clientOpen(Set<String> ipSet) {
        return null;
    }


    /**
     * client off
     * @param ipSet
     * @return
     */
    @Override
    public DBResponse<ProvideData> clientOff(Set<String> ipSet) {
        return null;
    }

    /**
     * query client off ips
     * @return
     */
    @Override
    public DBResponse<ProvideData> queryClientOffSet() {
        return null;
    }

    @Override
    public void becomeLeader() {

    }

    @Override
    public void loseLeader() {

    }
}