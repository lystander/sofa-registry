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
package com.alipay.sofa.registry.server.session.converter;

import com.alipay.sofa.registry.common.model.AppRegisterServerDataBox;
import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.store.AppPublisher;
import com.alipay.sofa.registry.common.model.store.Publisher;
import com.alipay.sofa.registry.server.session.cache.AppRevisionCacheRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiaojian.xj
 * @version $Id: AppPublisherConverter.java, v 0.1 2020年11月24日 20:50 xiaojian.xj Exp $
 */
public class AppPublisherConverter {

    public static Publisher convert(AppPublisher appPublisher,
                                    AppRevisionCacheRegistry appRevisionCacheRegistry) {
        Converter<AppPublisher, Publisher> converter = source -> {
            Publisher publisher = new Publisher();

            fillCommonRegion(publisher, source);
            List<ServerDataBox> dataList = new ArrayList<>();
            for (AppRegisterServerDataBox appRegisterServerDataBox : source.getAppDataList()) {

                //todo
                //appRevisionCacheRegistry.getParam(appPublisher.getAppName(), appRegisterServerDataBox.getRevision());
                dataList.add(new ServerDataBox(appRegisterServerDataBox.extract(publisher.getDataId())));
            }

            publisher.setDataList(dataList);
            return publisher;
        };

        return converter.convert(appPublisher);

    }

    private static void fillCommonRegion(Publisher publisher, AppPublisher source) {

        publisher.setAppName(source.getAppName());
        //ZONE MUST BE CURRENT SESSION ZONE
        publisher.setCell(source.getCell());
        publisher.setClientId(source.getClientId());
        publisher.setDataId(source.getDataId());
        publisher.setGroup(source.getGroup());
        publisher.setInstanceId(source.getInstanceId());
        publisher.setRegisterId(source.getRegisterId());
        publisher.setProcessId(source.getProcessId());
        publisher.setVersion(source.getVersion());
        publisher.setRegisterTimestamp(source.getRegisterTimestamp());

        publisher.setClientRegisterTimestamp(source.getClientRegisterTimestamp());
        publisher.setSourceAddress(source.getSourceAddress());

        publisher.setClientVersion(source.getClientVersion());
        publisher.setDataInfoId(source.getDataInfoId());
    }

}