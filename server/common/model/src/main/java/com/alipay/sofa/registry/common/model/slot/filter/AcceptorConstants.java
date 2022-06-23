/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot.filter;

import com.alipay.sofa.registry.common.model.PublisherGroupType;
import com.google.common.collect.Sets;

import java.util.Collections;

/**
 *
 * @author xiaojian.xj
 * @version : AcceptorConstants.java, v 0.1 2022年06月08日 20:37 xiaojian.xj Exp $
 */
public class AcceptorConstants {

    public static final SyncSlotAcceptor SERVICE_MAPPING_ACCEPTOR =
            new SyncSlotGroupAcceptor(
                    Sets.newHashSet(PublisherGroupType.REGISTRY_MAPPING.getCode()), Collections.emptySet());

    public static final SyncSlotAcceptor APP_REVISION_ACCEPTOR =
            new SyncSlotGroupAcceptor(
                    Sets.newHashSet(PublisherGroupType.REGISTRY_MAPPING.getCode()), Collections.emptySet());
}
