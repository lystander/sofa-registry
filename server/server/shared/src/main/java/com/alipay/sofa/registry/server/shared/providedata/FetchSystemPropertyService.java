/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.shared.providedata;

import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyRequest;
import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyResult;

/**
 *
 * @author xiaojian.xj
 * @version $Id: FetchSystemPropertyService.java, v 0.1 2021年05月16日 13:06 xiaojian.xj Exp $
 */
public interface FetchSystemPropertyService {

    /**
     * start load data
     */
    void load();

    boolean processorData(FetchSystemPropertyResult data);
}