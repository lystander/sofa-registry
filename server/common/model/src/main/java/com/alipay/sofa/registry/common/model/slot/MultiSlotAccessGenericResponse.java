/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.slot;

import com.alipay.sofa.registry.common.model.GenericResponse;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : MultiSlotAccessGenericResponse.java, v 0.1 2022年06月20日 16:28 xiaojian.xj Exp $
 */
public class MultiSlotAccessGenericResponse<T> extends GenericResponse<T> {

    private final Map<String, SlotAccess> slotAccessMap;

    public MultiSlotAccessGenericResponse(boolean success, String message, T data, Map<String, SlotAccess> slotAccessMap) {
        this.slotAccessMap = slotAccessMap;
        this.setData(data);
        this.setSuccess(success);
        this.setMessage(message);
    }

    /**
     * Getter method for property <tt>slotAccessMap</tt>.
     *
     * @return property value of slotAccessMap
     */
    public Map<String, SlotAccess> getSlotAccessMap() {
        return slotAccessMap;
    }
}
