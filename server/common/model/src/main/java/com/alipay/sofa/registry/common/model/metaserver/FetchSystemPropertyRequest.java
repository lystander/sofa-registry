/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.metaserver;

import java.io.Serializable;

/**
 *
 * @author xiaojian.xj
 * @version $Id: FetchSystemPropertyRequest.java, v 0.1 2021年05月12日 21:04 xiaojian.xj Exp $
 */
public class FetchSystemPropertyRequest implements Serializable {

    private final String dataInfoId;

    private final long version;

    public FetchSystemPropertyRequest(String dataInfoId, long version) {
        this.dataInfoId = dataInfoId;
        this.version = version;
    }

    /**
     * Getter method for property <tt>dataInfoId</tt>.
     *
     * @return property value of dataInfoId
     */
    public String getDataInfoId() {
        return dataInfoId;
    }

    /**
     * Getter method for property <tt>version</tt>.
     *
     * @return property value of version
     */
    public long getVersion() {
        return version;
    }



}