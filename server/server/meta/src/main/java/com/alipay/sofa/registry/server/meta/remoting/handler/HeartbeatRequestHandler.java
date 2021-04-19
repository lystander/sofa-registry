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

import com.alipay.sofa.registry.common.model.GenericResponse;
import com.alipay.sofa.registry.common.model.Node;
import com.alipay.sofa.registry.common.model.metaserver.cluster.VersionedList;
import com.alipay.sofa.registry.common.model.metaserver.inter.heartbeat.BaseHeartBeatResponse;
import com.alipay.sofa.registry.common.model.metaserver.inter.heartbeat.HeartbeatRequest;
import com.alipay.sofa.registry.common.model.metaserver.nodes.DataNode;
import com.alipay.sofa.registry.common.model.metaserver.nodes.MetaNode;
import com.alipay.sofa.registry.common.model.metaserver.nodes.SessionNode;
import com.alipay.sofa.registry.common.model.slot.SlotConfig;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.exception.SofaRegistryMetaLeaderException;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.server.meta.MetaLeaderService;
import com.alipay.sofa.registry.server.meta.bootstrap.config.NodeConfig;
import com.alipay.sofa.registry.server.meta.metaserver.impl.DefaultCurrentDcMetaServer;
import com.alipay.sofa.registry.server.meta.monitor.data.DataMessageListener;
import com.alipay.sofa.registry.server.meta.monitor.heartbeat.HeartbeatListener;
import com.alipay.sofa.registry.server.meta.monitor.session.SessionMessageListener;
import com.alipay.sofa.registry.server.shared.slot.SlotTableUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handle session/data node's heartbeat request
 *
 * @author shangyu.wh
 * @version $Id: RenewNodesRequestHandler.java, v 0.1 2018-03-30 19:58 shangyu.wh Exp $
 */
@Component
public class HeartbeatRequestHandler extends BaseMetaServerHandler<HeartbeatRequest<Node>> {

  private static final Logger logger = LoggerFactory.getLogger(HeartbeatRequestHandler.class);

  @Autowired private DefaultCurrentDcMetaServer currentDcMetaServer;

  @Autowired private MetaLeaderService metaLeaderService;

  @Autowired(required = false)
  private List<DataMessageListener> dataMessageListeners;

  @Autowired(required = false)
  private List<SessionMessageListener> sessionMessageListeners;

  @Autowired private NodeConfig nodeConfig;

  /**
   * Do handle object.
   *
   * @param channel the channel
   * @param heartbeat the heartbeat
   * @return the object
   */
  @Override
  public Object doHandle(Channel channel, HeartbeatRequest<Node> heartbeat) {
    Node renewNode = null;
    try {
      renewNode = heartbeat.getNode();
      onHeartbeat(heartbeat, channel);

      SlotTable slotTable = currentDcMetaServer.getSlotTable();
      if (!SlotTableUtils.isValidSlotTable(slotTable)) {
        return new GenericResponse<BaseHeartBeatResponse>()
            .fillFailed("slot-table not valid, check meta-server log for detail");
      }
      BaseHeartBeatResponse response = null;

      final VersionedList<MetaNode> metaServerInfo = currentDcMetaServer.getClusterMeta();
      final VersionedList<SessionNode> sessionMetaInfo =
          currentDcMetaServer.getSessionServerManager().getSessionServerMetaInfo();

      switch (renewNode.getNodeType()) {
        case SESSION:
        case DATA:
          response =
              new BaseHeartBeatResponse(
                  true,
                  metaServerInfo,
                  slotTable,
                  sessionMetaInfo,
                  metaLeaderService.getLeader(),
                  metaLeaderService.getLeaderEpoch());
          break;
        case META:
          response =
              new BaseHeartBeatResponse(
                  true,
                  metaServerInfo,
                  slotTable,
                  metaLeaderService.getLeader(),
                  metaLeaderService.getLeaderEpoch());
          break;
        default:
          break;
      }

      return new GenericResponse<BaseHeartBeatResponse>().fillSucceed(response);
    } catch (Throwable e) {
      if (e instanceof SofaRegistryMetaLeaderException) {
        SofaRegistryMetaLeaderException exception = (SofaRegistryMetaLeaderException) e;
        BaseHeartBeatResponse response =
            new BaseHeartBeatResponse(false, exception.getLeader(), exception.getEpoch());
        return new GenericResponse<BaseHeartBeatResponse>().fillFailData(response);
      }

      logger.error("Node {} renew error!", renewNode, e);
      return new GenericResponse<BaseHeartBeatResponse>()
          .fillFailed("Node " + renewNode + "renew error!");
    }
  }

  @SuppressWarnings("unchecked")
  private void onHeartbeat(HeartbeatRequest heartbeat, Channel channel) {
    new DefaultHeartbeatListener(nodeConfig.getLocalDataCenter(), channel).onHeartbeat(heartbeat);
    Node node = heartbeat.getNode();
    switch (node.getNodeType()) {
      case SESSION:
        currentDcMetaServer
            .getSessionServerManager()
            .renew((SessionNode) node, heartbeat.getDuration());
        onSessionHeartbeat(heartbeat);
        return;
      case DATA:
        currentDcMetaServer.getDataServerManager().renew((DataNode) node, heartbeat.getDuration());
        onDataHeartbeat(heartbeat);
        return;
      case META:
        currentDcMetaServer.renew((MetaNode) node);
        return;
      default:
        break;
    }
    throw new IllegalArgumentException("node type not correct: " + node.getNodeType());
  }

  private void onSessionHeartbeat(HeartbeatRequest<SessionNode> heartbeat) {
    if (sessionMessageListeners == null || sessionMessageListeners.isEmpty()) {
      return;
    }
    sessionMessageListeners.forEach(
        listener -> {
          try {
            listener.onHeartbeat(heartbeat);
          } catch (Throwable th) {
            logger.error("[onDataHeartbeat]", th);
          }
        });
  }

  private void onDataHeartbeat(HeartbeatRequest<DataNode> heartbeat) {
    if (dataMessageListeners == null || dataMessageListeners.isEmpty()) {
      return;
    }
    dataMessageListeners.forEach(
        listener -> {
          try {
            listener.onHeartbeat(heartbeat);
          } catch (Throwable th) {
            logger.error("[onDataHeartbeat]", th);
          }
        });
  }

  /**
   * Interest class.
   *
   * @return the class
   */
  @Override
  public Class interest() {
    return HeartbeatRequest.class;
  }

  public static class DefaultHeartbeatListener implements HeartbeatListener<Node> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHeartbeatListener.class);

    public static final String KEY_TIMESTAMP_GAP_THRESHOLD = "timestamp.gap.threshold";

    private static final long timeGapThreshold = Long.getLong(KEY_TIMESTAMP_GAP_THRESHOLD, 2000);

    private final String dataCenter;

    private final Channel channel;

    private volatile boolean isValidChannel = true;

    /**
     * Constructor.
     *
     * @param dataCenter the data center
     * @param channel the channel
     */
    public DefaultHeartbeatListener(String dataCenter, Channel channel) {
      this.dataCenter = dataCenter;
      this.channel = channel;
    }

    /**
     * On heartbeat.
     *
     * @param heartbeat the heartbeat
     */
    @Override
    public void onHeartbeat(HeartbeatRequest<Node> heartbeat) {
      checkIfDataCenterMatched(heartbeat);
      checkIfTimeSynced(heartbeat);
      checkIfSlotBasicInfoMatched(heartbeat);
      closeIfChannelNotValid();
    }

    private void closeIfChannelNotValid() {
      if (!isValidChannel) {

        channel.close();
      }
    }

    private void checkIfTimeSynced(HeartbeatRequest<Node> heartbeat) {
      long timestamp = heartbeat.getTimestamp();
      if (System.currentTimeMillis() - timestamp > timeGapThreshold) {
        logger.error(
            "[checkIfTimeSynced] {} timestamp[{}] is far behind mine[{}]",
            heartbeat.getNode(),
            timestamp,
            System.currentTimeMillis());
      }
    }

    private void checkIfSlotBasicInfoMatched(HeartbeatRequest<Node> heartbeat) {

      if (heartbeat.getNode() instanceof MetaNode) {
        return;
      }

      SlotConfig.SlotBasicInfo slotBasicInfo = heartbeat.getSlotBasicInfo();
      if (!SlotConfig.FUNC.equals(slotBasicInfo.getSlotFunc())) {
        logger.error(
            "[checkIfSlotBasicInfoMatched] {} slot function not match(meta-server: [{}], receive: [{}]",
            heartbeat.getNode(),
            SlotConfig.FUNC,
            slotBasicInfo.getSlotFunc());
        isValidChannel = false;
      }
      if (SlotConfig.SLOT_NUM != slotBasicInfo.getSlotNum()) {
        logger.error(
            "[checkIfSlotBasicInfoMatched] {} slot number not match(meta-server: [{}], receive: [{}]",
            heartbeat.getNode(),
            SlotConfig.SLOT_NUM,
            slotBasicInfo.getSlotNum());
        isValidChannel = false;
      }
      if (SlotConfig.SLOT_REPLICAS != slotBasicInfo.getSlotReplicas()) {
        logger.error(
            "[checkIfSlotBasicInfoMatched] {} slot replicas not match(meta-server: [{}], receive: [{}]",
            heartbeat.getNode(),
            SlotConfig.SLOT_REPLICAS,
            slotBasicInfo.getSlotReplicas());
        isValidChannel = false;
      }
    }

    private void checkIfDataCenterMatched(HeartbeatRequest<Node> heartbeat) {
      String dc = heartbeat.getDataCenter();
      if (!this.dataCenter.equalsIgnoreCase(dc)) {
        logger.error(
            "[checkIfDataCenterMatched] {} datacenter not match(meta-server: [{}], node: [{}]",
            heartbeat.getNode(),
            this.dataCenter,
            dc);
        isValidChannel = false;
      }
    }
  }

  /**
   * Sets set node config.
   *
   * @param nodeConfig the node config
   * @return the set node config
   */
  public HeartbeatRequestHandler setNodeConfig(NodeConfig nodeConfig) {
    this.nodeConfig = nodeConfig;
    return this;
  }

  /**
   * Sets set meta leader elector.
   *
   * @param metaLeaderElector the meta leader elector
   * @return the set meta leader elector
   */
  public HeartbeatRequestHandler setMetaLeaderElector(MetaLeaderService metaLeaderElector) {
    this.metaLeaderService = metaLeaderElector;
    return this;
  }
}
