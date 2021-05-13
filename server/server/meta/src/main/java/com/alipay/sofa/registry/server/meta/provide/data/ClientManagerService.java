/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.provide.data;

import com.alipay.sofa.registry.common.model.console.PersistenceData;
import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.server.meta.MetaLeaderService.MetaLeaderElectorListener;
import com.alipay.sofa.registry.store.api.DBResponse;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerService.java, v 0.1 2021年05月12日 15:19 xiaojian.xj Exp $
 */
public interface ClientManagerService extends MetaLeaderElectorListener {

    /**
     * client open
     * @param ipSet
     * @return
     */
    DBResponse<ProvideData> clientOpen(Set<String> ipSet);


    /**
     * client off
     * @param ipSet
     * @return
     */
    DBResponse<ProvideData> clientOff(Set<String> ipSet);

    /**
     * query client off ips
     * @return
     */
    DBResponse<ProvideData> queryClientOffSet();
}