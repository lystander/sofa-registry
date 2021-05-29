/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.filter.clientoff;

import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.server.session.filter.ProcessFilter;
import com.alipay.sofa.registry.server.session.filter.blacklist.BlacklistConstants;
import com.alipay.sofa.registry.server.session.provideData.FetchClientOffPodsService;

import javax.annotation.Resource;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientOffMatchProcessFilter.java, v 0.1 2021年05月28日 21:02 xiaojian.xj Exp $
 */
public class ClientOffMatchProcessFilter implements ProcessFilter<BaseInfo> {

    @Resource
    private FetchClientOffPodsService fetchClientOffPodsService;

    @Override
    public boolean match(BaseInfo storeData) {
        URL url = storeData.getSourceAddress();

        if (url != null) {
            return fetchClientOffPodsService.getClientOffPods().equals(url.getIpAddress());
        }
        return false;
    }
}