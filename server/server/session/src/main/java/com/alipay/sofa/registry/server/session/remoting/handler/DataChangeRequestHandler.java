/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.server.session.remoting.handler;

import com.alipay.sofa.registry.common.model.Node.NodeType;
import com.alipay.sofa.registry.common.model.dataserver.DatumVersion;
import com.alipay.sofa.registry.common.model.sessionserver.DataChangeRequest;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.metadata.AppRevisionCacheRegistry;
import com.alipay.sofa.registry.server.session.push.FirePushService;
import com.alipay.sofa.registry.server.session.scheduler.ExecutorManager;
import com.alipay.sofa.registry.server.session.store.Interests;
import com.alipay.sofa.registry.server.shared.remoting.AbstractClientHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author kezhu.wukz
 * @author shangyu.wh
 * @version $Id: DataChangeRequestHandler.java, v 0.1 2017-12-12 15:09 shangyu.wh Exp $
 */
public class DataChangeRequestHandler extends AbstractClientHandler<DataChangeRequest> {

    private static final Logger      LOGGER = LoggerFactory
                                                .getLogger(DataChangeRequestHandler.class);
    /**
     * store subscribers
     */
    @Autowired
    private Interests                sessionInterests;

    @Autowired
    private SessionServerConfig      sessionServerConfig;

    @Autowired
    private ExecutorManager          executorManager;

    @Autowired
    private AppRevisionCacheRegistry appRevisionCacheRegistry;

    @Autowired
    private FirePushService          firePushService;

    @Override
    protected NodeType getConnectNodeType() {
        return NodeType.DATA;
    }

    @Override
    public Executor getExecutor() {
        return executorManager.getDataChangeRequestExecutor();
    }

    @Override
    public Object doHandle(Channel channel, DataChangeRequest dataChangeRequest) {
        if (sessionServerConfig.isStopPushSwitch()) {
            return null;
        }
        final String dataCenter = dataChangeRequest.getDataCenter();
        for (Map.Entry<String, DatumVersion> e : dataChangeRequest.getDataInfoIds().entrySet()) {
            final String dataInfoId = e.getKey();
            final DatumVersion version = e.getValue();
            Interests.InterestVersionCheck check = sessionInterests.checkInterestVersion(
                dataCenter, dataInfoId, version.getValue());
            if (!check.interested) {
                if (check != Interests.InterestVersionCheck.NoSub) {
                    // log exclude NoSub
                    LOGGER.info("[SkipChange]{},{}, ver={}, {}", dataInfoId, dataCenter, version,
                        check);
                }
                continue;
            }
            firePushService.fireOnChange(dataCenter, dataInfoId, version.getValue());
        }
        return null;
    }

    @Override
    public Class interest() {
        return DataChangeRequest.class;
    }
}