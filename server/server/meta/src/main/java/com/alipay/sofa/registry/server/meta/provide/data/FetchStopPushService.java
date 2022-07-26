/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.meta.provide.data;

import com.alipay.sofa.registry.common.model.console.PersistenceData;
import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.server.shared.util.PersistenceDataParser;
import com.alipay.sofa.registry.store.api.DBResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author xiaojian.xj
 * @version : FetchStopPushService.java, v 0.1 2022年07月21日 15:00 xiaojian.xj Exp $
 */
public class FetchStopPushService {

    @Autowired
    private ProvideDataService provideDataService;

    public boolean isStopPush() {
        DBResponse<PersistenceData> stopPushResp =
                provideDataService.queryProvideData(ValueConstants.STOP_PUSH_DATA_SWITCH_DATA_ID);
        return PersistenceDataParser.parse2BoolIgnoreCase(stopPushResp, false);
    }

}
