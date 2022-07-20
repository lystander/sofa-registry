/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.constants;

import com.alipay.sofa.registry.common.model.store.DataInfo;

import static com.alipay.sofa.registry.common.model.constants.ValueConstants.SESSION_PROVIDE_DATA_GROUP;
import static com.alipay.sofa.registry.common.model.constants.ValueConstants.SESSION_PROVIDE_DATA_INSTANCE_ID;

/**
 *
 * @author xiaojian.xj
 * @version : MultiValueConstants.java, v 0.1 2022年07月20日 14:41 xiaojian.xj Exp $
 */
public final class MultiValueConstants {

    private MultiValueConstants(){}

    public static final String SESSION_SERVER_MULTI_PUSH_DATA_ID =
            DataInfo.toDataInfoId(
                    "session.server.multi.push",
                    SESSION_PROVIDE_DATA_INSTANCE_ID,
                    SESSION_PROVIDE_DATA_GROUP);

    public static final String DATA_SERVER_MULTI_SYNC_DATA_ID =
            DataInfo.toDataInfoId(
                    "data.server.multi.sync",
                    SESSION_PROVIDE_DATA_INSTANCE_ID,
                    SESSION_PROVIDE_DATA_GROUP);

    public static final String SYNC_ACCEPT_ALL = "ACCEPT_ALL";

}
