/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.session.cache;


import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.common.model.store.Subscriber;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.scheduler.task.Constant;
import com.alipay.sofa.registry.server.session.store.SessionDataStore;
import com.alipay.sofa.registry.server.session.store.SessionInterests;
import com.alipay.sofa.registry.util.SchedulerCornUtil;
import com.alipay.sofa.registry.util.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author xiaojian.xj
 * @version $Id: SessionCacheDigestTask.java, v 0.1 2020年08月03日 14:37 xiaojian.xj Exp $
 */
public class SessionCacheDigestTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionCacheDigestTask.class);

    @Autowired
    private SessionDataStore sessionDataStore;

    @Autowired
    private SessionInterests sessionInterests;

    @Autowired
    private SessionServerConfig sessionServerConfig;

    /**
     * session data print
     */
    public void start() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
                new NamedThreadFactory("SessionCacheDigestTask"));
        executor.scheduleAtFixedRate(() -> {

            try {
                Collection<String> storeDataInfoIds = sessionDataStore.getStoreDataInfoIds();
                Collection<String> interestDataInfoIds = sessionInterests.getInterestDataInfoIds();
                Set<String> dataInfoIds = new HashSet<>();

                dataInfoIds.addAll(storeDataInfoIds);
                dataInfoIds.addAll(interestDataInfoIds);

                dataInfoIds.stream().forEach(dataInfoId -> {
                    Collection<Publisher> publishers = sessionDataStore.getStoreDataByDataInfoId(dataInfoId);
                    Collection<Subscriber> subscribers = sessionInterests.getInterests(dataInfoId);

                    LOGGER.info("[dataInfo] {}, {}, {}, {}, [{}];[{}]", sessionServerConfig.getSessionServerDataCenter(),
                            dataInfoId, CollectionUtils.isEmpty(publishers) ? 0 : publishers.size(),
                            CollectionUtils.isEmpty(subscribers) ? 0 : subscribers.size(),
                            logPubOrSub(publishers), logPubOrSub(subscribers));

                });

            } catch (Throwable t) {
                LOGGER.error("[SessionCacheDigestTask] cache digest error", t);
            }

        }, SchedulerCornUtil.calculateInitialDelay(Constant.CACHE_PRINTER_CRON)/1000, 600, TimeUnit.SECONDS);
    }

    private String logPubOrSub(Collection<? extends BaseInfo> infos) {

        return Optional.ofNullable(infos).orElse(new ArrayList<>()).stream()
                .filter(info -> info != null)
                .map(info -> logUrl(info.getSourceAddress()))
                .collect(Collectors.joining(","));
    }

    private String logUrl(URL url) {
        return url == null ? "null" : url.getAddressString();
    }

}