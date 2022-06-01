/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.slot.Slot.Role;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptor;
import com.alipay.sofa.registry.jdbc.domain.InterfaceAppsIndexDomain;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.ServiceAppsPublish.ServiceMappingVersion;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.store.api.meta.EntryNotify;
import com.alipay.sofa.registry.store.api.repository.InterfaceAppsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : ServiceAppsPublish.java, v 0.1 2022年05月25日 20:48 xiaojian.xj Exp $
 */
public class ServiceAppsPublish extends MetadataSlotChangeListener<ServiceMappingVersion> implements EntryNotify<InterfaceAppsIndexDomain> {

    @Autowired
    private InterfaceAppsRepository interfaceAppsRepository;


    @PostConstruct
    public void init() {
        interfaceAppsRepository.registerNotify(this);
    }

    @Override
    public void notify(InterfaceAppsIndexDomain entry) {

    }

    @Override
    protected int waitingMillis() {
        return dataServerConfig.getSlotSyncAppRevisionIntervalSecs() * 1000;
    }

    @Override
    protected List<SyncSlotAcceptor> getSyncSlotAcceptor() {
        return null;
    }

    final static class ServiceMappingVersion extends MetadataVersion {
        final Set<String> apps;

        public ServiceMappingVersion(String dataInfoId, long version, Set<String> apps) {
            super(dataInfoId, version);
            this.apps = apps;
        }
    }

}
