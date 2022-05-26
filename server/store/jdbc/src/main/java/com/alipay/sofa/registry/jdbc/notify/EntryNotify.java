/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.jdbc.notify;

import com.alipay.sofa.registry.jdbc.informer.DbEntry;

/**
 *
 * @author xiaojian.xj
 * @version : EntryNotify.java, v 0.1 2022年05月25日 20:55 xiaojian.xj Exp $
 */
public interface EntryNotify<T extends DbEntry> {
    public void notify(T entry);
}
