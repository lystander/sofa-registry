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
package com.alipay.sofa.registry.server.session.push;

import com.alipay.sofa.registry.common.model.SubscriberUtils;
import com.alipay.sofa.registry.common.model.store.BaseInfo;
import com.alipay.sofa.registry.common.model.store.MultiSubDatum;
import com.alipay.sofa.registry.common.model.store.PushData;
import com.alipay.sofa.registry.common.model.store.SubDatum;
import com.alipay.sofa.registry.common.model.store.Subscriber;
import com.alipay.sofa.registry.core.model.ScopeEnum;
import com.alipay.sofa.registry.trace.TraceID;
import com.alipay.sofa.registry.util.StringFormatter;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public abstract class PushTask {
  protected final TraceID taskID;
  protected volatile long expireTimestamp;

  protected final MultiSubDatum datum;
  protected final Map<String, Subscriber> subscriberMap;
  protected final Subscriber subscriber;

  protected final PushingTaskKey pushingTaskKey;
  protected final PushTrace trace;

  protected int retryCount;
  private int pushTotalDataCount = -1;
  private Map<String, Integer> pushDataCount;
  private String pushEncode = StringUtils.EMPTY;
  private int encodeSize = 0;

  protected PushTask(
      PushCause pushCause,
      InetSocketAddress addr,
      Map<String, Subscriber> subscriberMap,
      MultiSubDatum datum) {
    this.taskID = TraceID.newTraceID();
    this.datum = datum;
    this.subscriberMap = subscriberMap;
    this.subscriber = subscriberMap.values().iterator().next();
    this.trace =
        PushTrace.trace(
            datum,
            addr,
            subscriber.getAppName(),
            pushCause,
            subscriberMap.size(),
            SubscriberUtils.getMinRegisterTimestamp(subscriberMap.values()));
    this.pushingTaskKey =
        new PushingTaskKey(
            subscriber.getDataInfoId(), addr, subscriber.getScope(), subscriber.getClientVersion());
  }

  protected abstract boolean commit();

  protected abstract PushData createPushData();

  protected void expireAfter(long intervalMs) {
    this.expireTimestamp = System.currentTimeMillis() + intervalMs;
  }

  public boolean hasPushed() {
    if (subscriberMap.size() == 1) {
      return subscriber.hasPushed();
    }
    for (Subscriber s : subscriberMap.values()) {
      if (!s.hasPushed()) {
        return false;
      }
    }
    return true;
  }

  public boolean isSingletonReg() {
    return trace.pushCause.pushType == PushType.Reg && subscriberMap.size() == 1;
  }

  protected boolean afterThan(PushTask t) {
    if (isSingletonReg() && t.isSingletonReg()) {
      return subscriber.getVersion() > t.subscriber.getVersion();
    }
    for (Entry<String, Long> entry : datum.getVersion().entrySet()) {
      // return true if one of any datum.version > t.datum.version
      if (entry.getValue() > t.datum.getVersion(entry.getKey())) {
        return true;
      }
    }
    return false;
  }

  protected Map<String, Long> getMaxPushedVersion() {
    if (datum == null || CollectionUtils.isEmpty(datum.getDatumMap())) {
      return Collections.emptyMap();
    }

    Map<String, Long> ret = Maps.newHashMapWithExpectedSize(datum.getDatumMap().size());
    for (Entry<String, SubDatum> entry : datum.getDatumMap().entrySet()) {
      ret.put(
          entry.getKey(),
          SubscriberUtils.getMaxPushedVersion(entry.getKey(), subscriberMap.values()));
    }
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(512);
    sb.append("PushTask{")
        .append(subscriber.getDataInfoId())
        .append(",ID=")
        .append(taskID)
        .append(",createT=")
        .append(trace.pushCreateTimestamp)
        .append(",expireT=")
        .append(expireTimestamp)
        .append(",DC=")
        .append(datum.dataCenters())
        .append(",ver=")
        .append(datum.getVersion())
        .append(",addr=")
        .append(pushingTaskKey.addr)
        .append(",scope=")
        .append(subscriber.getScope())
        .append(",subIds=")
        .append(subscriberMap.keySet())
        .append(",subCtx=")
        .append(subscriber.printPushContext())
        .append(",retry=")
        .append(retryCount);
    return sb.toString();
  }

  /**
   * Getter method for property <tt>pushTotalDataCount</tt>.
   *
   * @return property value of pushTotalDataCount
   */
  public int getPushTotalDataCount() {
    return pushTotalDataCount;
  }

  /**
   * Getter method for property <tt>pushDataCount</tt>.
   *
   * @return property value of pushDataCount
   */
  public Map<String, Integer> getPushDataCount() {
    return pushDataCount;
  }

  /**
   * Setter method for property <tt>pushDataCount</tt>.
   *
   * @param pushDataCount value to be assigned to property pushDataCount
   */
  public void setPushDataCount(Map<String, Integer> pushDataCount) {
    if (pushDataCount == null) {
      pushDataCount = Collections.emptyMap();
    }
    this.pushDataCount = pushDataCount;
  }

  public void setPushEncode(String pushEncode) {
    this.pushEncode = pushEncode;
  }

  public void setEncodeSize(int encodeSize) {
    this.encodeSize = encodeSize;
  }

  public String getPushEncode() {
    return pushEncode;
  }

  public int getEncodeSize() {
    return encodeSize;
  }

  protected static final class PushingTaskKey {
    protected final InetSocketAddress addr;
    protected final String dataInfoId;
    protected final ScopeEnum scopeEnum;
    protected final BaseInfo.ClientVersion clientVersion;

    protected PushingTaskKey(
        String dataInfoId,
        InetSocketAddress addr,
        ScopeEnum scopeEnum,
        BaseInfo.ClientVersion clientVersion) {
      this.dataInfoId = dataInfoId;
      this.addr = addr;
      this.scopeEnum = scopeEnum;
      this.clientVersion = clientVersion;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PushingTaskKey that = (PushingTaskKey) o;
      return Objects.equals(addr, that.addr)
          && Objects.equals(dataInfoId, that.dataInfoId)
          && scopeEnum == that.scopeEnum
          && clientVersion == that.clientVersion;
    }

    @Override
    public int hashCode() {
      return Objects.hash(addr, dataInfoId, scopeEnum, clientVersion);
    }

    @Override
    public String toString() {
      return StringFormatter.format("PushingKey{{},scope={},{}", dataInfoId, scopeEnum, addr);
    }
  }
}
