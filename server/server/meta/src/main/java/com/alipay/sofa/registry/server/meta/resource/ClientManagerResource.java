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
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

/**
 * The type Clients open resource.
 *
 * @author xiaojian.xj
 * @version $Id : ClientManagerResource.java, v 0.1 2018-11-22 19:04 xiaojian.xj Exp $$
 */
@Path("api/clientManager")
@Produces(MediaType.APPLICATION_JSON)
public class ClientManagerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManagerResource.class);

    /** Client off */
    @POST
    @Path("/clientOff")
    public CommonResponse clientOff(@FormParam("ips") String ips) {
        if (StringUtils.isEmpty(ips)) {
            return CommonResponse.buildFailedResponse("ips is empty");
        }
        String[] ipArray = StringUtils.split(ips.trim(), ';');
        List<String> ipList = Arrays.asList(ipArray);

        return CommonResponse.buildSuccessResponse();
    }

    /** Client Open */
    @POST
    @Path("/clientOpen")
    public CommonResponse clientOn(@FormParam("ips") String ips) {
        if (StringUtils.isEmpty(ips)) {
            return CommonResponse.buildFailedResponse("ips is empty");
        }
        String[] ipArray = StringUtils.split(ips.trim(), ';');
        List<String> ipList = Arrays.asList(ipArray);

        return CommonResponse.buildSuccessResponse();
    }

}
