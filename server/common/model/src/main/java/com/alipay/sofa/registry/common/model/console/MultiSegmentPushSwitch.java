/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.console;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : MultiSegmentPushSwitch.java, v 0.1 2022年07月20日 11:07 xiaojian.xj Exp $
 */
public class MultiSegmentPushSwitch {

    /** multi push switch */
    private boolean multiPush;

    /**
     * allow push multi dataInfoId,
     * if contains All means all dataInfoId push multi
     **/
    private Set<String> pushDataInfoIds = Collections.EMPTY_SET;

    public boolean isMultiPush() {
        return this.multiPush;
    }

    /**
     * Setter method for property <tt>multiPush</tt>.
     *
     * @param multiPush value to be assigned to property multiPush
     */
    public void setMultiPush(Boolean multiPush) {
        this.multiPush = multiPush;
    }

    /**
     * Getter method for property <tt>pushDataInfoIds</tt>.
     *
     * @return property value of pushDataInfoIds
     */
    public Set<String> getPushDataInfoIds() {
        return pushDataInfoIds;
    }

    /**
     * Setter method for property <tt>pushDataInfoIds</tt>.
     *
     * @param pushDataInfoIds value to be assigned to property pushDataInfoIds
     */
    public void setPushDataInfoIds(Set<String> pushDataInfoIds) {
        this.pushDataInfoIds = pushDataInfoIds;
    }
}
