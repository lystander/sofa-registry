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
package com.alipay.sofa.registry.server.session.provideData;

import com.alipay.sofa.registry.common.model.ConnectId;
import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.metaserver.ProvideData;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.connections.ConnectionsService;
import com.alipay.sofa.registry.server.session.registry.Registry;
import com.alipay.sofa.registry.server.shared.providedata.AbstractFetchSystemPropertyService;
import com.alipay.sofa.registry.util.ConcurrentUtils;
import com.alipay.sofa.registry.util.LoopRunnable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiaojian.xj
 * @version $Id: FetchClientOffPodsService.java, v 0.1 2021年05月16日 18:01 xiaojian.xj Exp $
 */
public class FetchClientOffPodsService extends AbstractFetchSystemPropertyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchClientOffPodsService.class);

  private final AtomicReference<KeySetView> clientOffPods = new AtomicReference<>();

  private final AtomicReference<ClientOffTable> updating = new AtomicReference<>();

  private final ClientManagerProcessor clientManagerProcessor = new ClientManagerProcessor();

  @Autowired private SessionServerConfig sessionServerConfig;

  @Autowired private ConnectionsService connectionsService;

  @Autowired private Registry sessionRegistry;

  public FetchClientOffPodsService() {
    super(ValueConstants.CLIENT_OFF_PODS_DATA_ID);
    clientOffPods.set(new ConcurrentHashMap<>().newKeySet());
    ConcurrentUtils.createDaemonThread("ClientManagerProcessor", clientManagerProcessor).start();
  }

  @Override
  public boolean doProcess(ProvideData data) {
    if (this.updating.get() != null) {
      return false;
    }
    Set<String> fetch = (Set<String>) data.getProvideData().getObject();
    KeySetView olds = clientOffPods.get();

    SetView toBeRemove = Sets.difference(olds, fetch);
    SetView toBeAdd = Sets.difference(fetch, olds);

    KeySetView<String, Boolean> news = new ConcurrentHashMap<>().newKeySet();
    news.addAll(fetch);

    writeLock.lock();
    try {
      updating.set(new ClientOffTable(toBeAdd, toBeRemove));
      clientOffPods.set(news);
      version.set(data.getVersion());
    } catch (Throwable t) {
      LOGGER.error("update clientOffPods:{} error.", data, t);
    } finally {
      writeLock.unlock();
    }
    return true;
  }

  final class ClientOffTable {
    final Set<String> adds;

    final Set<String> removes;

    public ClientOffTable(Set<String> adds, Set<String> removes) {
      this.adds = adds;
      this.removes = removes;
    }

    /**
     * Getter method for property <tt>adds</tt>.
     *
     * @return property value of adds
     */
    public Set<String> getAdds() {
      return adds;
    }

    /**
     * Getter method for property <tt>removes</tt>.
     *
     * @return property value of removes
     */
    public Set<String> getRemoves() {
      return removes;
    }
  }

  private final class ClientManagerProcessor extends LoopRunnable {

    @Override
    public void runUnthrowable() {
      processUpdating();
    }

    @Override
    public void waitingUnthrowable() {
      ConcurrentUtils.sleepUninterruptibly(
          sessionServerConfig.getClientManagerIntervalMillis(), TimeUnit.MILLISECONDS);
    }
  }

  boolean processUpdating() {
    final ClientOffTable table = updating.getAndSet(null);
    if (table == null) {
      return true;
    }

    Set<String> adds = table.adds;
    Set<String> removes = table.removes;

    if (CollectionUtils.isEmpty(adds) && CollectionUtils.isEmpty(removes)) {
      return true;
    }

    if (CollectionUtils.isNotEmpty(adds)) {
      doTrafficOff(adds);
    }

    if (CollectionUtils.isNotEmpty(removes)) {
      doTrafficOn(removes);
    }
    return true;
  }

  private void doTrafficOff(Set<String> _ipSet) {
    List<ConnectId> conIds = connectionsService.getIpConnects(_ipSet);

    if (CollectionUtils.isNotEmpty(conIds)) {
      LOGGER.info("clientOff conIds: {}", conIds.toString());
    }
    sessionRegistry.remove(conIds);
  }

  private void doTrafficOn(Set<String> _ipList) {
    connectionsService.closeIpConnects(Lists.newArrayList(_ipList));
  }

  /**
   * Getter method for property <tt>clientOffPods</tt>.
   *
   * @return property value of clientOffPods
   */
  public KeySetView getClientOffPods() {
    return clientOffPods.get();
  }
}
