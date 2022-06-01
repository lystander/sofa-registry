/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.jdbc.notify;

import com.alipay.sofa.registry.store.api.meta.DbEntry;
import com.alipay.sofa.registry.store.api.meta.EntryNotify;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : DBEntryNotify.java, v 0.1 2022年05月25日 20:55 xiaojian.xj Exp $
 */
public class DBEntryNotify {

    private final Set<EntryNotify> notifies = Sets.newConcurrentHashSet();

    public void register(EntryNotify notify) {
        notifies.add(notify);
    }

    public void notify(DbEntry entry) {
        for(EntryNotify notify: notifies) {
            notify.notify(entry);
        }
    }
}
