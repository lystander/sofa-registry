/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.console;

import com.alipay.sofa.registry.common.model.ServiceGroupType;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : MultiSegmentSyncSwitch.java, v 0.1 2022年07月20日 11:07 xiaojian.xj Exp $
 */
public class MultiSegmentSyncSwitch {

    /** multi sync switch */
    private boolean multiSync;

    /**
     * allow sync multi group
     *
     **/
    private Set<String> synPublisherGroups = Collections.EMPTY_SET;

    /**
     * allow sync multi dataInfoId
     **/
    private Set<String> syncDataInfoIds = Collections.EMPTY_SET;

    /**
     * dataInfoId will not multi sync,
     * this priority is higher than syncGroups and syncDataInfoIds
     **/
    private Set<String> ignoreDataInfoIds = Collections.EMPTY_SET;

    public boolean isMultiSync() {
        return this.multiSync;
    }

    /**
     * Setter method for property <tt>multiSync</tt>.
     *
     * @param multiSync value to be assigned to property multiSync
     */
    public void setMultiSync(Boolean multiSync) {
        this.multiSync = multiSync;
    }

    /**
     * Getter method for property <tt>synPublisherGroups</tt>.
     *
     * @return property value of synPublisherGroups
     */
    public Set<String> getSynPublisherGroups() {
        return synPublisherGroups;
    }

    /**
     * Setter method for property <tt>synPublisherGroups</tt>.
     *
     * @param synPublisherGroups value to be assigned to property synPublisherGroups
     */
    public void setSynPublisherGroups(Set<String> synPublisherGroups) {
        this.synPublisherGroups = synPublisherGroups;
    }

    /**
     * Getter method for property <tt>syncDataInfoIds</tt>.
     *
     * @return property value of syncDataInfoIds
     */
    public Set<String> getSyncDataInfoIds() {
        return syncDataInfoIds;
    }

    /**
     * Setter method for property <tt>syncDataInfoIds</tt>.
     *
     * @param syncDataInfoIds value to be assigned to property syncDataInfoIds
     */
    public void setSyncDataInfoIds(Set<String> syncDataInfoIds) {
        this.syncDataInfoIds = syncDataInfoIds;
    }

    /**
     * Getter method for property <tt>ignoreDataInfoIds</tt>.
     *
     * @return property value of ignoreDataInfoIds
     */
    public Set<String> getIgnoreDataInfoIds() {
        return ignoreDataInfoIds;
    }

    /**
     * Setter method for property <tt>ignoreDataInfoIds</tt>.
     *
     * @param ignoreDataInfoIds value to be assigned to property ignoreDataInfoIds
     */
    public void setIgnoreDataInfoIds(Set<String> ignoreDataInfoIds) {
        this.ignoreDataInfoIds = ignoreDataInfoIds;
    }
}
