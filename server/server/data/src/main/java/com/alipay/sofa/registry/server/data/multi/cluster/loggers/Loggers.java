/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.loggers;

import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;

/**
 *
 * @author xiaojian.xj
 * @version : Loggers.java, v 0.1 2022年05月06日 20:40 xiaojian.xj Exp $
 */
public class Loggers {

    private Loggers(){}

    public static final Logger MULTI_CLUSTER_SRV_LOGGER = LoggerFactory.getLogger("MULTI-CLUSTER-SRV");

    public static final Logger MULTI_CLUSTER_CLIENT_LOGGER = LoggerFactory.getLogger("MULTI-CLUSTER-CLIENT");

    public static final Logger MULTI_CLUSTER_SYNC_DIGEST_LOGGER = LoggerFactory.getLogger("MULTI_CLUSTER_SYNC_DIGEST_LOGGER");

    public static final Logger MULTI_CLUSTER_SLOT_TABLE = LoggerFactory.getLogger("MULTI-CLUSTER-SLOT-TABLE");
}
