/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.metaserver;

import java.io.Serializable;

/**
 *
 * @author xiaojian.xj
 * @version $Id: FetchSystemPropertyResult.java, v 0.1 2021年05月12日 21:41 xiaojian.xj Exp $
 */
public class FetchSystemPropertyResult implements Serializable {

    private final boolean versionUpgrade;

    private final ProvideData provideData;

    public FetchSystemPropertyResult(boolean versionUpgrade, ProvideData provideData) {
        this.versionUpgrade = versionUpgrade;
        this.provideData = provideData;
    }


    public boolean isVersionUpgrade() {
        return versionUpgrade;
    }

    /**
     * Getter method for property <tt>provideData</tt>.
     *
     * @return property value of provideData
     */
    public ProvideData getProvideData() {
        return provideData;
    }
}