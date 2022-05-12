/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.multi.cluster.storage;

import com.alipay.sofa.registry.common.model.ConnectId;
import com.alipay.sofa.registry.common.model.ProcessId;
import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.dataserver.Datum;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunction;
import com.alipay.sofa.registry.common.model.slot.func.SlotFunctionRegistry;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.cache.CleanContinues;
import com.alipay.sofa.registry.server.data.cache.DatumStorage;
import com.alipay.sofa.registry.server.data.slot.SlotChangeListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : RemoteDatumStorage.java, v 0.1 2022年05月05日 21:21 xiaojian.xj Exp $
 */
public class MultiClusterDatumStorage implements DatumStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiClusterDatumStorage.class);

    private final SlotFunction slotFunction = SlotFunctionRegistry.getFunc();


    /**
     * get datum by specific dataInfoId
     *
     * @param dataCenter
     * @param dataInfoId
     * @return
     */
    @Override
    public Datum get(String dataCenter, String dataInfoId) {
        return null;
    }

    @Override
    public DatumVersion getVersion(String dataCenter, String dataInfoId) {
        return null;
    }

    @Override
    public Map<String, DatumVersion> getVersions(String dataCenter, int slotId, Collection<String> targetDatInfoIds) {
        return null;
    }

    @Override
    public Map<String, Publisher> getByConnectId(ConnectId connectId) {
        return null;
    }

    @Override
    public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slot) {
        return null;
    }

    /**
     * get all datum
     *
     * @return
     * @param dataCenter
     */
    @Override
    public Map<String, Datum> getAll(String dataCenter) {
        return null;
    }

    @Override
    public Map<String, List<Publisher>> getAllPublisher(String dataCenter) {
        return null;
    }

    @Override
    public Map<String, Integer> getPubCount(String dataCenter) {
        return null;
    }

    @Override
    public DatumVersion put(String dataCenter, Publisher publisher) {
        return null;
    }

    @Override
    public DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId) {
        return null;
    }

    @Override
    public Map<String, DatumVersion> clean(String dataCenter, int slotId, ProcessId sessionProcessId, CleanContinues cleanContinues) {
        return null;
    }

    @Override
    public DatumVersion remove(String dataCenter, String dataInfoId, ProcessId sessionProcessId) {
        return null;
    }

    @Override
    public DatumVersion remove(String dataCenter, String dataInfoId, ProcessId sessionProcessId,
                               Map<String, RegisterVersion> removedPublishers) {
        return null;
    }

    @Override
    public DatumVersion put(String dataCenter, String dataInfoId, List<Publisher> updatedPublishers) {
        return null;
    }

    @Override
    public Map<String, Map<String, DatumSummary>> getDatumSummary(String dataCenter, int slotId, Set<String> sessions) {
        return null;
    }

    @Override
    public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId) {
        return null;
    }

    @Override
    public SlotChangeListener getSlotChangeListener() {
        return null;
    }

    @Override
    public Set<ProcessId> getSessionProcessIds(String dataCenter) {
        return null;
    }

    @Override
    public Map<String, Integer> compact(String dataCenter, long tombstoneTimestamp) {
        return null;
    }

    @Override
    public int tombstoneNum(String dataCenter) {
        return 0;
    }

    @Override
    public Map<String, DatumVersion> updateVersion(String dataCenter, int slotId) {
        return null;
    }

    @Override
    public DatumVersion updateVersion(String dataCenter, String dataInfoId) {
        return null;
    }
}
