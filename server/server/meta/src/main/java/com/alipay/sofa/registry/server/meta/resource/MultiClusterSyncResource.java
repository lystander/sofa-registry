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
package com.alipay.sofa.registry.server.meta.resource;

import com.alipay.sofa.registry.common.model.CommonResponse;
import com.alipay.sofa.registry.common.model.console.PersistenceDataBuilder;
import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.store.api.meta.MultiClusterSyncRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author xiaojian.xj
 * @version : RecoverConfigResource.java, v 0.1 2021年09月25日 00:02 xiaojian.xj Exp $
 */
@Path("api/multi/cluster")
@Produces(MediaType.APPLICATION_JSON)
public class MultiClusterSyncResource {

  private static final Logger LOG =
      LoggerFactory.getLogger("MULTI-CLUSTER-CONFIG", "[MultiClusterSyncResource]");

  @Autowired private MultiClusterSyncRepository multiClusterSyncRepository;

  @POST
  @Path("/save")
  public CommonResponse saveConfig(
      @FormParam("remoteDataCenter") String remoteDataCenter,
      @FormParam("remoteMetaAddress") String remoteMetaAddress) {
    if (StringUtils.isBlank(remoteDataCenter) || StringUtils.isBlank(remoteMetaAddress)) {
      return CommonResponse.buildFailedResponse(
          "remoteDataCenter, remoteMetaAddress is not allow empty.");
    }

    boolean ret =
        multiClusterSyncRepository.insert(
            new MultiClusterSyncInfo(
                remoteDataCenter, remoteMetaAddress, PersistenceDataBuilder.nextVersion()));

    LOG.info(
        "[saveConfig]save multi cluster sync config, result:{}, remoteDataCenter:{}, remoteMetaAddress:{}",
        ret,
        remoteDataCenter,
        remoteMetaAddress);

    CommonResponse response = new CommonResponse();
    response.setSuccess(ret);
    return response;
  }

  @POST
  @Path("/update")
  public CommonResponse updateConfig(
      @FormParam("remoteDataCenter") String remoteDataCenter,
      @FormParam("remoteMetaAddress") String remoteMetaAddress,
      @FormParam("expectVersion") String expectVersion) {
    if (StringUtils.isBlank(remoteDataCenter)
        || StringUtils.isBlank(remoteMetaAddress)
        || StringUtils.isBlank(expectVersion)) {
      return CommonResponse.buildFailedResponse(
          "remoteDataCenter, remoteMetaAddress, expectVersion is not allow empty.");
    }

    boolean ret =
        multiClusterSyncRepository.update(
            new MultiClusterSyncInfo(
                remoteDataCenter, remoteMetaAddress, PersistenceDataBuilder.nextVersion()),
            NumberUtils.toLong(expectVersion));

    LOG.info(
        "[updateConfig]update multi cluster sync config, result:{}, remoteDataCenter:{}, remoteMetaAddress:{}, expectVersion:{}",
        ret,
        remoteDataCenter,
        remoteMetaAddress,
        expectVersion);

    CommonResponse response = new CommonResponse();
    response.setSuccess(ret);
    return response;
  }

  // todo xiaojian.xj remove 
  @POST
  @Path("/remove")
  public CommonResponse removeConfig(
      @FormParam("remoteDataCenter") String remoteDataCenter,
      @FormParam("expectVersion") String expectVersion) {
    if (StringUtils.isBlank(remoteDataCenter) || StringUtils.isBlank(expectVersion)) {
      return CommonResponse.buildFailedResponse(
          "remoteDataCenter, expectVersion is not allow empty.");
    }

    int ret =
        multiClusterSyncRepository.remove(remoteDataCenter, NumberUtils.toLong(expectVersion));

    LOG.info(
        "[removeConfig]update multi cluster sync config, result:{}, remoteDataCenter:{}, expectVersion:{}",
        ret,
        remoteDataCenter,
        expectVersion);

    CommonResponse response = new CommonResponse();
    response.setSuccess(ret > 0);
    return response;
  }
}
