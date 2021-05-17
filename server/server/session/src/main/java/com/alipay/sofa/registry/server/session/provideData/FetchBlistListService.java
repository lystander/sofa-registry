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
 * @version $Id: FetchBlistListService.java, v 0.1 2021年05月16日 17:59 xiaojian.xj Exp $
 */
public class FetchBlistListService extends AbstractFetchSystemPropertyService {


    public FetchBlistListService() {
        super(ValueConstants.BLACK_LIST_DATA_ID);
    }

    @Override
    public boolean processorData(FetchSystemPropertyResult data) {
        return false;
    }
}