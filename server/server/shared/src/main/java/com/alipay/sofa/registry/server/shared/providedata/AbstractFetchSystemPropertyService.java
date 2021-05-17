/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.shared.providedata;

import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyResult;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.shared.config.ServerShareConfig;
import com.alipay.sofa.registry.server.shared.meta.MetaServerService;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.LoopRunnable;
import com.alipay.sofa.registry.util.StringFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author xiaojian.xj
 * @version $Id: AbstractFetchSystemPropertyService.java, v 0.1 2021年05月16日 13:32 xiaojian.xj Exp $
 */
public abstract class AbstractFetchSystemPropertyService implements FetchSystemPropertyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchSystemPropertyService.class);

    private static final Long INIT_VERSION = -1L;

    private final String dataInfoId;

    private AtomicLong version = new AtomicLong(INIT_VERSION);

    private final WatchDog watchDog = new WatchDog();

    @Autowired private ServerShareConfig serverShareConfig;

    @Autowired protected MetaServerService metaNodeService;


    private final class WatchDog extends LoopRunnable {

        @Override
        public void runUnthrowable() {
            long current = version.get();
            FetchSystemPropertyResult response = metaNodeService.fetchSystemProperty(dataInfoId, current);

            Assert.isTrue(response != null, StringFormatter.format("[FetchSystemProperty]dataInfoId:{} fetch data error.", dataInfoId));

            if (!response.isVersionUpgrade()) {
                return;
            }

            Assert.isTrue(response.getProvideData() != null, StringFormatter.format("[FetchSystemProperty]dataInfoId:{}, versionUpgrade:{}, but provideData is null.", dataInfoId, true));

            Long update = response.getProvideData().getVersion();
            Assert.isTrue(update > current,
                    StringFormatter.format("[FetchSystemProperty]dataInfoId:{}, versionUpgrade:{}, current:{}, update:{}",
                    dataInfoId, true, current, update));

            if (current == version.get() && processorData(response)){
                version.compareAndSet(current, update);
            } else {
                LOGGER.error("[FetchSystemProperty]dataInfoId:{}, data:{}, process fail.", dataInfoId, response);
            }

        }

        @Override
        public void waitingUnthrowable() {
            ConcurrentUtils.sleepUninterruptibly(serverShareConfig.getSystemPropertyIntervalMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public AbstractFetchSystemPropertyService(String dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    @Override
    public void load() {
        ConcurrentUtils.createDaemonThread(StringFormatter.format("FetchSystemProperty-{}", dataInfoId), watchDog).start();
    }
}