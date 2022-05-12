/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.cache;

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
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : BaseDatumStorage.java, v 0.1 2022年05月12日 11:54 xiaojian.xj Exp $
 */
public class BaseDatumStorage implements DatumStorage {

    private final Logger logger;

    private final        String dataCenter;

    protected final SlotFunction slotFunction = SlotFunctionRegistry.getFunc();

    protected final Map<Integer, PublisherGroups> publisherGroupsMap = Maps.newConcurrentMap();

    public BaseDatumStorage(String dataCenter, Logger logger) {
        this.dataCenter = dataCenter;
        this.logger = logger;
    }

    private PublisherGroups getPublisherGroups(String dataInfoId) {
        final Integer slotId = slotFunction.slotOf(dataInfoId);
        PublisherGroups groups = publisherGroupsMap.get(slotId);
        if (groups == null) {
            logger.warn("[nullGroups] {}, {}", slotId, dataInfoId);
        }
        return groups;
    }

    private PublisherGroups getPublisherGroups(int slotId) {
        PublisherGroups groups = publisherGroupsMap.get(slotId);
        if (groups == null) {
            logger.warn("[nullGroups] {}", slotId);
        }
        return groups;
    }

    @Override
    public Datum get(String dataCenter, String dataInfoId) {
        final PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.getDatum(dataInfoId);
    }

    @Override
    public DatumVersion getVersion(String dataCenter, String dataInfoId) {
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.getVersion(dataInfoId);
    }

    @Override
    public Map<String, DatumVersion> getVersions(String dataCenter, int slotId, Collection<String> targetDataInfoIds) {
        PublisherGroups groups = getPublisherGroups(slotId);
        return groups == null ? Collections.emptyMap() : groups.getVersions(targetDataInfoIds);
    }

    @Override
    public Map<String, Datum> getAll(String dataCenter) {
        Map<String, Datum> m = Maps.newHashMapWithExpectedSize(128);
        publisherGroupsMap.values().forEach(g -> m.putAll(g.getAllDatum()));
        return m;
    }

    @Override
    public Map<String, List<Publisher>> getAllPublisher(String dataCenter) {
        Map<String, List<Publisher>> m = Maps.newHashMapWithExpectedSize(128);
        publisherGroupsMap.values().forEach(g -> m.putAll(g.getAllPublisher()));
        return m;
    }

    @Override
    public Map<String, Integer> getPubCount(String dataCenter) {
        Map<String, Integer> map = Maps.newHashMapWithExpectedSize(128);
        publisherGroupsMap.values().forEach(g -> map.putAll(g.getPubCount()));
        return map;
    }

    @Override
    public Map<String, Publisher> getByConnectId(ConnectId connectId) {
        Map<String, Publisher> m = Maps.newHashMapWithExpectedSize(64);
        publisherGroupsMap.values().forEach(g -> m.putAll(g.getByConnectId(connectId)));
        return m;
    }

    @Override
    public Map<String, Map<String, Publisher>> getPublishers(String dataCenter, int slotId) {
        PublisherGroups groups = getPublisherGroups(slotId);
        if (groups == null) {
            return Collections.emptyMap();
        }
        Map<String, List<Publisher>> publisherMap = groups.getAllPublisher();
        Map<String, Map<String, Publisher>> ret = Maps.newHashMapWithExpectedSize(publisherMap.size());
        for (Map.Entry<String, List<Publisher>> publishers : publisherMap.entrySet()) {
            final String dataInfoId = publishers.getKey();
            final List<Publisher> list = publishers.getValue();
            // only copy the non empty publishers
            if (!list.isEmpty()) {
                Map<String, Publisher> map =
                        ret.computeIfAbsent(dataInfoId, k -> Maps.newHashMapWithExpectedSize(list.size()));
                for (Publisher p : list) {
                    map.put(p.getRegisterId(), p);
                }
            }
        }
        return ret;
    }

    @Override
    public DatumVersion createEmptyDatumIfAbsent(String dataCenter, String dataInfoId) {
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.createGroupIfAbsent(dataInfoId).getVersion();
    }

    @Override
    public Map<String, DatumVersion> clean(
            String dataCenter, int slotId, ProcessId sessionProcessId, CleanContinues cleanContinues) {
        // clean by sessionProcessId, the sessionProcessId could not be null
        ParaCheckUtil.checkNotNull(sessionProcessId, "sessionProcessId");
        PublisherGroups groups = getPublisherGroups(slotId);
        if (groups == null) {
            return Collections.emptyMap();
        }
        return groups.clean(sessionProcessId, cleanContinues);
    }

    // only for http testapi
    @Override
    public DatumVersion remove(String dataCenter, String dataInfoId, ProcessId sessionProcessId) {
        // the sessionProcessId is null when the call from sync leader
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.remove(dataInfoId, sessionProcessId);
    }

    @Override
    public DatumVersion put(String dataCenter, String dataInfoId, List<Publisher> publishers) {
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.put(dataInfoId, publishers);
    }

    @Override
    public DatumVersion put(String dataCenter, Publisher publisher) {
        return put(dataCenter, publisher.getDataInfoId(), Collections.singletonList(publisher));
    }

    @Override
    public DatumVersion remove(
            String dataCenter,
            String dataInfoId,
            ProcessId sessionProcessId,
            Map<String, RegisterVersion> removedPublishers) {
        // the sessionProcessId is null when the call from sync leader
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.remove(dataInfoId, sessionProcessId, removedPublishers);
    }

    @Override
    public Map<String, Map<String, DatumSummary>> getDatumSummary(String dataCenter, int slotId, Set<String> sessions) {
        final PublisherGroups groups = publisherGroupsMap.get(slotId);
        if (groups != null) {
            return groups.getSummary(sessions);
        }

        if (CollectionUtils.isEmpty(sessions)) {
            return Collections.emptyMap();
        }

        Map<String /*sessionIp*/, Map<String /*dataInfoId*/, DatumSummary>> summaries =
                Maps.newHashMapWithExpectedSize(sessions.size());
        for (String sessionIp : sessions) {
            summaries.put(sessionIp, Collections.emptyMap());
        }
        return summaries;
    }

    @Override
    public Map<String, DatumSummary> getDatumSummary(String dataCenter, int slotId) {
        final PublisherGroups groups = publisherGroupsMap.get(slotId);
        return groups != null ? groups.getAllSummary() : Collections.emptyMap();
    }

    @Override
    public Map<String, DatumVersion> updateVersion(String dataCenter, int slotId) {
        PublisherGroups groups = publisherGroupsMap.get(slotId);
        if (groups == null) {
            return Collections.emptyMap();
        }
        return groups.updateVersion();
    }

    @Override
    public DatumVersion updateVersion(String dataCenter, String dataInfoId) {
        PublisherGroups groups = getPublisherGroups(dataInfoId);
        return groups == null ? null : groups.updateVersion(dataInfoId);
    }
}
