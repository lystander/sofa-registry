/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.metadata;

import com.alipay.sofa.registry.common.model.DataInfoIdGenerator;
import com.alipay.sofa.registry.common.model.appmeta.InterfaceMapping;
import com.alipay.sofa.registry.common.model.store.MultiSubDatum;
import com.alipay.sofa.registry.exception.SofaRegistryRuntimeException;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.cache.DatumKey;
import com.alipay.sofa.registry.server.session.cache.Key;
import com.alipay.sofa.registry.server.session.cache.SessionCacheService;
import com.alipay.sofa.registry.server.session.cache.Value;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author xiaojian.xj
 * @version : BaseMetadataCache.java, v 0.1 2022年06月25日 15:31 xiaojian.xj Exp $
 */
public abstract class BaseMetadataCache<T> extends SessionCacheService {

    protected final Logger LOG = LoggerFactory.getLogger("METADATA-EXCHANGE", getClass().getSimpleName());


    @Autowired
    protected SessionServerConfig sessionServerConfig;

    public T query(String dataInfoId) {
        try {

            // TODO multi datacenter
            Set<String> dataCenters = Sets.newHashSet();
            dataCenters.add(sessionServerConfig.getSessionServerDataCenter());

            Map<String, Long> getDatumVersions = Maps.newHashMapWithExpectedSize(dataCenters.size());
            for (String dataCenter : dataCenters) {
                getDatumVersions.put(dataCenter, Long.MIN_VALUE);
            }

            Key key = new Key(
                    DatumKey.class.getName(),
                    new DatumKey(dataInfoIdOf(dataInfoId), getDatumVersions.keySet()));
            Value value = readWriteCacheMap.getIfPresent(key);

            if (value != null) {
                hitMetric();
                return decode((MultiSubDatum) value.getPayload());
            }
            value = readWriteCacheMap.get(key);
            if (value == null) {
                LOG.error("query fail, dataInfoId: {}", dataInfoId);
                return null;
            }
            return decode((MultiSubDatum) value.getPayload());
        } catch (Throwable e) {
            LOG.error("query error, dataInfoId: {}", dataInfoId, e);
            throw new SofaRegistryRuntimeException("query failed");
        }
    }

    protected abstract String dataInfoIdOf(String dataInfoId);

    protected abstract void hitMetric();

    protected abstract T decode(MultiSubDatum datum) throws Exception;
}
