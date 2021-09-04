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
package com.alipay.sofa.registry.server.session.remoting.console.handler;

import static org.mockito.Mockito.mock;

import com.alipay.sofa.registry.common.model.CommonResponse;
import com.alipay.sofa.registry.common.model.sessionserver.FilterSubscriberIPsRequest;
import com.alipay.sofa.registry.server.session.store.Interests;
import org.junit.Assert;
import org.junit.Test;

public class FilterSubscriberIPsHandlerTest {
  private FilterSubscriberIPsHandler newHandler() {
    FilterSubscriberIPsHandler handler = new FilterSubscriberIPsHandler();
    Assert.assertEquals(FilterSubscriberIPsHandler.class, handler.interest());
    return handler;
  }

  @Test
  public void testHandle() {
    FilterSubscriberIPsHandler handler = newHandler();
    handler.sessionInterests = mock(Interests.class);
    CommonResponse obj =
        (CommonResponse) handler.doHandle(null, new FilterSubscriberIPsRequest("DEFAULT_GROUP", 1));
    Assert.assertTrue(obj.isSuccess());
  }
}
