/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.slot;

import com.alipay.sofa.registry.server.data.cache.DatumStorageDelegate;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.AppRevisionPublish;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.ServiceAppsPublish;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiaojian.xj
 * @version : SlotChangeListenerManager.java, v 0.1 2022年05月31日 10:20 xiaojian.xj Exp $
 */
public class SlotChangeListenerManager {

    private final List<SlotChangeListener> localSlotChangeListeners = new ArrayList<>();

    private final List<SlotChangeListener> remoteSlotChangeListeners = new ArrayList<>();

    @Resource
    private DatumStorageDelegate datumStorageDelegate;

    @Autowired
    private AppRevisionPublish appRevisionPublish;

    @Autowired
    private ServiceAppsPublish serviceAppsPublish;

    @PostConstruct
    public void init() {
        localSlotChangeListeners.add(datumStorageDelegate.getSlotChangeListener(true));
        localSlotChangeListeners.add(appRevisionPublish);
        localSlotChangeListeners.add(serviceAppsPublish);

        remoteSlotChangeListeners.add(datumStorageDelegate.getSlotChangeListener(false));
    }

    public List<SlotChangeListener> localListeners() {
        return Lists.newArrayList(localSlotChangeListeners);
    }

    public List<SlotChangeListener> remoteListeners() {
        return Lists.newArrayList(remoteSlotChangeListeners);
    }
}
