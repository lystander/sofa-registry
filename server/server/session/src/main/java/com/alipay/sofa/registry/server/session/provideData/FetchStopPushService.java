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
 * @version $Id: FetchStopPushService.java, v 0.1 2021年05月16日 17:48 xiaojian.xj Exp $
 */
public class FetchStopPushService extends AbstractFetchSystemPropertyService {


    public FetchStopPushService() {
        super(ValueConstants.STOP_PUSH_DATA_SWITCH_DATA_ID);
    }

    @Override
    public boolean processorData(FetchSystemPropertyResult data) {
        return true;
    }
}