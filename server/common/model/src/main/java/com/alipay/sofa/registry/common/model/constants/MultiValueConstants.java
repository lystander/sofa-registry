/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.constants;

import com.alipay.sofa.registry.common.model.PublishSource;
import com.alipay.sofa.registry.common.model.ServiceGroupType;
import com.alipay.sofa.registry.common.model.slot.filter.SyncPublishSourceAcceptor;
import com.alipay.sofa.registry.common.model.slot.filter.SyncServiceGroupAcceptor;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptor;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.google.common.collect.Sets;

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

    public static final String PUSH_ACCEPT_ALL = "ACCEPT_ALL";

    public static final SyncSlotAcceptor SERVICE_MAPPING_GROUP_ACCEPTOR =
            new SyncServiceGroupAcceptor(
                    Sets.newHashSet(ServiceGroupType.REGISTRY_MAPPING));

    public static final SyncSlotAcceptor APP_REVISION_GROUP_ACCEPTOR =
            new SyncServiceGroupAcceptor(
                    Sets.newHashSet(ServiceGroupType.REGISTRY_MAPPING));

    public static final SyncSlotAcceptor REGISTRY_SERVICE_GROUP_ACCEPTOR =
            new SyncServiceGroupAcceptor(
                    Sets.newHashSet(ServiceGroupType.REGISTRY_SERVICE));

    public static final SyncSlotAcceptor DATUM_SYNCER_SOURCE_FILTER =
            new SyncPublishSourceAcceptor(
                    Sets.newHashSet(PublishSource.DATUM_SYNCER));


    public static final String SYNC_SLOT_DATAINFOID_ACCEPTOR = "SyncSlotDataInfoIdAcceptor";

    public static final String SYNC_PUBLISH_SOURCE_ACCEPTOR = "SyncPublishSourceAcceptor";

    public static final String SYNC_PUBLISHER_GROUP_ACCEPTOR = "SyncPublisherGroupAcceptor";

    public static final String SYNC_SERVICE_GROUP_ACCEPTOR = "SyncServiceGroupAcceptor";

}
