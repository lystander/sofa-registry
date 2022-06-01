/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.app.discovery;

import com.alipay.sofa.registry.common.model.slot.Slot.Role;
import com.alipay.sofa.registry.jdbc.domain.AppRevisionDomain;
import com.alipay.sofa.registry.server.data.multi.cluster.app.discovery.MetadataSlotChangeListener.MetadataVersion;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;
import com.alipay.sofa.registry.store.api.meta.EntryNotify;
import com.alipay.sofa.registry.store.api.repository.AppRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : AppRevisionPublish.java, v 0.1 2022年05月25日 20:49 xiaojian.xj Exp $
 */
public class AppRevisionPublish extends MetadataSlotChangeListener<MetadataVersion> implements EntryNotify<AppRevisionDomain> {



    @Autowired
    private AppRevisionRepository appRevisionRepository;

    @PostConstruct
    public void init() {
        appRevisionRepository.registerNotify(this);
    }

    @Override
    public void notify(AppRevisionDomain entry) {

    }
}
