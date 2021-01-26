/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.common.model.store.WordCache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version $Id: AppRegisterServerDataBox.java, v 0.1 2020年11月12日 11:14 xiaojian.xj Exp $
 */
public class AppRegisterServerDataBox implements Serializable {
    private static final long                                                    serialVersionUID = -3615677271684611262L;

    /** revision */
    private String                                                               revision;

    /** ip:port */
    private String                                                               url;

    /** baseParams */
    private Map<String/*key*/, List<String>/*values*/>                         baseParams;

    /** */
    private Map<String/*service*/, Map<String/*key*/, List<String>/*value*/>> interfaceParams;

    /**
     * Getter method for property <tt>revision</tt>.
     *
     * @return property value of revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Setter method for property <tt>revision</tt>.
     *
     * @param revision value to be assigned to property revision
     */
    public void setRevision(String revision) {
        this.revision = WordCache.getWordCache(revision);
    }

    /**
     * Getter method for property <tt>url</tt>.
     *
     * @return property value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for property <tt>url</tt>.
     *
     * @param url value to be assigned to property url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for property <tt>baseParams</tt>.
     *
     * @return property value of baseParams
     */
    public Map<String, List<String>> getBaseParams() {
        return baseParams;
    }

    /**
     * Setter method for property <tt>baseParams</tt>.
     *
     * @param baseParams value to be assigned to property baseParams
     */
    public void setBaseParams(Map<String, List<String>> baseParams) {
        this.baseParams = baseParams;
    }

    /**
     * Getter method for property <tt>interfaceParams</tt>.
     *
     * @return property value of interfaceParams
     */
    public Map<String, Map<String, List<String>>> getInterfaceParams() {
        return interfaceParams;
    }

    /**
     * Setter method for property <tt>interfaceParams</tt>.
     *
     * @param interfaceParams value to be assigned to property interfaceParams
     */
    public void setInterfaceParams(Map<String, Map<String, List<String>>> interfaceParams) {
        this.interfaceParams = interfaceParams;
    }
}