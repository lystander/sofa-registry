/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.cache;

import com.alipay.sofa.registry.cache.CacheCleaner;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.Weigher;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author xiaojian.xj
 * @version : SessionDatumCacheService.java, v 0.1 2022年06月24日 20:17 xiaojian.xj Exp $
 */
public class SessionDatumCacheService extends SessionCacheService {

    @Autowired
    private SessionServerConfig sessionServerConfig;

    @PostConstruct
    public void init() {
        this.readWriteCacheMap = CacheBuilder.newBuilder()
                .maximumWeight(sessionServerConfig.getCacheDatumMaxWeight())
                .weigher((Weigher<Key, Value>) (key, value) -> key.size() + value.size())
                .expireAfterWrite(sessionServerConfig.getCacheDatumExpireSecs(), TimeUnit.SECONDS)
                .removalListener(new RemoveListener())
                .build(
                        new CacheLoader<Key, Value>() {
                            @Override
                            public Value load(Key key) {
                                return generatePayload(key);
                            }
                        });
        CacheCleaner.autoClean(readWriteCacheMap, 10);
    }
}
