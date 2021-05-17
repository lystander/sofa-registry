/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.provideData;

import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyResult;
import com.alipay.sofa.registry.server.shared.providedata.AbstractFetchSystemPropertyService;

/**
 *
 * @author xiaojian.xj
 * @version $Id: FetchClientOffPodsService.java, v 0.1 2021年05月16日 18:01 xiaojian.xj Exp $
 */
public class FetchClientOffPodsService extends AbstractFetchSystemPropertyService {


    public FetchClientOffPodsService() {
        super(ValueConstants.CLIENT_OFF_PODS_DATA_ID);
    }

    @Override
    public boolean processorData(FetchSystemPropertyResult data) {
        return false;
    }
}