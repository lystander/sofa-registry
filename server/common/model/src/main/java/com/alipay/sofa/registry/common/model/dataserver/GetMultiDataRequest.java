/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model.dataserver;

import com.alipay.sofa.registry.common.model.ProcessId;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author xiaojian.xj
 * @version : GetMultiDataRequest.java, v 0.1 2022年06月20日 15:52 xiaojian.xj Exp $
 */
public class GetMultiDataRequest implements Serializable {
    private static final long serialVersionUID = -4353547075497551438L;

    private final ProcessId sessionProcessId;

    private final int slotId;

    private final String dataInfoId;

    private final String[] acceptEncodes;

    private final Map<String, Long> slotTableEpochs;

    private final Map<String, Long> slotLeaderEpochs;

    public GetMultiDataRequest(ProcessId sessionProcessId, int slotId, String dataInfoId, String[] acceptEncodes,
                               Map<String, Long> slotTableEpochs, Map<String, Long> slotLeaderEpochs) {
        this.sessionProcessId = sessionProcessId;
        this.slotId = slotId;
        this.dataInfoId = dataInfoId;
        this.acceptEncodes = acceptEncodes;
        this.slotTableEpochs = slotTableEpochs;
        this.slotLeaderEpochs = slotLeaderEpochs;
    }

    /**
     * Getter method for property <tt>sessionProcessId</tt>.
     *
     * @return property value of sessionProcessId
     */
    public ProcessId getSessionProcessId() {
        return sessionProcessId;
    }

    /**
     * Getter method for property <tt>slotId</tt>.
     *
     * @return property value of slotId
     */
    public int getSlotId() {
        return slotId;
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
     * Getter method for property <tt>acceptEncodes</tt>.
     *
     * @return property value of acceptEncodes
     */
    public String[] getAcceptEncodes() {
        return acceptEncodes;
    }

    /**
     * Getter method for property <tt>slotTableEpochs</tt>.
     *
     * @return property value of slotTableEpochs
     */
    public Map<String, Long> getSlotTableEpochs() {
        return slotTableEpochs;
    }

    /**
     * Getter method for property <tt>slotLeaderEpochs</tt>.
     *
     * @return property value of slotLeaderEpochs
     */
    public Map<String, Long> getSlotLeaderEpochs() {
        return slotLeaderEpochs;
    }
}
