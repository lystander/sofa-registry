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
package com.alipay.sofa.registry.server.meta.remoting.handler;

import com.alipay.sofa.registry.common.model.ServerDataBox;
import com.alipay.sofa.registry.common.model.console.PersistenceData;
import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.metaserver.FetchProvideDataRequest;
import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyRequest;
import com.alipay.sofa.registry.common.model.metaserver.FetchSystemPropertyResult;
import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.meta.provide.data.ClientManagerService;
import com.alipay.sofa.registry.server.meta.provide.data.ProvideDataService;
import com.alipay.sofa.registry.store.api.DBResponse;
import com.alipay.sofa.registry.store.api.OperationStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.alipay.sofa.registry.common.model.constants.ValueConstants.CLIENT_OFF_PODS_DATA_ID;

/**
 * Handle session node's query request
 *
 * @author xiaojian.xj
 * @version $Id: FetchSystemPropertyRequestHandler.java, v 0.1 2021-05-06 15:12 xiaojian.xj Exp $
 */
public class FetchSystemPropertyRequestHandler extends BaseMetaServerHandler<FetchSystemPropertyRequest> {

  private static final Logger DB_LOGGER =
      LoggerFactory.getLogger(FetchSystemPropertyRequestHandler.class, "[DBService]");

  @Autowired private ProvideDataService provideDataService;

  @Autowired private ClientManagerService clientManagerService;

  @Override
  public void checkParam(FetchSystemPropertyRequest request) {
      Assert.isTrue(request != null, "get system data request is null.");
      Assert.isTrue(StringUtils.isNotEmpty(request.getDataInfoId()), "get system data request dataInfoId is empty.");
  }


    @Override
  public Object doHandle(Channel channel, FetchSystemPropertyRequest request) {
    try {
      DB_LOGGER.info("get system data {}", request);

      OperationStatus status;
      ProvideData data;
      if (CLIENT_OFF_PODS_DATA_ID.equals(request.getDataInfoId())) {
        DBResponse<ProvideData> ret = clientManagerService.queryClientOffSet();
        status = ret.getOperationStatus();
        data = ret.getEntity();
      } else {
        DBResponse<PersistenceData> ret =
                provideDataService.queryProvideData(request.getDataInfoId());
        status = ret.getOperationStatus();
        PersistenceData persistenceData = ret.getEntity();
        data = new ProvideData(
                new ServerDataBox(persistenceData.getData()),
                request.getDataInfoId(),
                persistenceData.getVersion());
      }

      if (status == OperationStatus.SUCCESS) {
        FetchSystemPropertyResult result;
        if (data.getVersion() > request.getVersion()) {
          result = new FetchSystemPropertyResult(true, data);
        } else {
          result = new FetchSystemPropertyResult(false);
        }
        if (DB_LOGGER.isInfoEnabled()) {
            DB_LOGGER.info("get SystemProperty {} from DB success!", result);
        }
        return result;
      } else if (status == OperationStatus.NOTFOUND) {
        ProvideData provideData =
            new ProvideData(null, request.getDataInfoId(), null);
        DB_LOGGER.warn(
            "has not found system data from DB dataInfoId:{}", request.getDataInfoId());
        return provideData;
      } else {
        DB_LOGGER.error("get Data DB status error!");
        throw new RuntimeException("Get Data DB status error!");
      }

    } catch (Exception e) {
      DB_LOGGER.error(
          "get persistence Data dataInfoId {} from db error!",
          request.getDataInfoId());
      throw new RuntimeException("Get persistence Data from db error!", e);
    }
  }

  @Override
  public Class interest() {
    return FetchProvideDataRequest.class;
  }
}
