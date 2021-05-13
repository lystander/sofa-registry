/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.store.api.meta;

import com.alipay.sofa.registry.common.model.metaserver.ClientManagerPods;

import java.util.List;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerPodsRepository.java, v 0.1 2021年05月11日 16:47 xiaojian.xj Exp $
 */
public interface ClientManagerPodsRepository {

    /**
     * client open
     * @param ipSet
     * @return
     */
    boolean clientOpen(Set<String> ipSet);


    /**
     * client off
     * @param ipSet
     * @return
     */
    boolean clientOff(Set<String> ipSet);

    /**
     * query all client manager pods
     * @return
     */
    List<ClientManagerPods> queryAll();

    /**
     * query records
     * @param maxId
     * @return
     */
    List<ClientManagerPods> queryAfterThan(long maxId);

}