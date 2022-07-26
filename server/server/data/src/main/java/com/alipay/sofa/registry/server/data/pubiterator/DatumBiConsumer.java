/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.pubiterator;

import com.alipay.sofa.registry.common.model.RegisterVersion;
import com.alipay.sofa.registry.common.model.dataserver.DatumSummary;
import com.alipay.sofa.registry.common.model.slot.filter.SyncAcceptorRequest;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.server.data.cache.PublisherEnvelope;
import com.alipay.sofa.registry.server.data.cache.PublisherGroup;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author xiaojian.xj
 * @version : DatumBiConsumer.java, v 0.1 2022年07月23日 12:42 xiaojian.xj Exp $
 */
public class DatumBiConsumer {

  public static BiConsumer<String, PublisherGroup> publisherGroupsBiConsumer(
      Map<String, Map<String, DatumSummary>> summaries,
      Set<String> sessions,
      SyncSlotAcceptorManager syncSlotAcceptorManager) {
    return (dataInfoId, publisherGroup) -> {
      Map<String /*sessionIp*/, Map<String /*registerId*/, RegisterVersion>> publisherVersions =
          Maps.newHashMapWithExpectedSize(sessions.size());
      for (String sessionIp : sessions) {
        summaries.computeIfAbsent(sessionIp, v -> Maps.newHashMapWithExpectedSize(64));
        publisherVersions.computeIfAbsent(sessionIp, k -> Maps.newHashMapWithExpectedSize(64));
      }

      if (!syncSlotAcceptorManager.accept(
          SyncAcceptorRequest.buildRequest(dataInfoId, publisherGroup.getServiceGroupType()))) {
        return;
      }

      publisherGroup.foreach(
          publisherGroupBiConsumer(publisherVersions, sessions, syncSlotAcceptorManager));
      Map<String, DatumSummary> sessionSummary =
          Maps.newHashMapWithExpectedSize(sessions.size());

      for (Entry<String, Map<String, RegisterVersion>> entry : publisherVersions.entrySet()) {
        sessionSummary.put(entry.getKey(), new DatumSummary(dataInfoId, entry.getValue()));
      }

      for (Entry<String, DatumSummary> entry : sessionSummary.entrySet()) {
        if (entry.getValue().isEmpty()) {
          continue;
        }
        Map<String, DatumSummary> summaryMap = summaries.get(entry.getKey());
        summaryMap.put(dataInfoId, entry.getValue());
      }
    };
  }

  private static BiConsumer<String, PublisherEnvelope> publisherGroupBiConsumer(
      Map<String, Map<String, RegisterVersion>> publisherVersions,
      Set<String> sessions,
      SyncSlotAcceptorManager syncSlotAcceptorManager) {

    return (registerId, envelope) -> {
      RegisterVersion v = envelope.getVersionIfPub();
      // v = null when envelope is unpub
      if (v == null
          || !syncSlotAcceptorManager.accept(
              SyncAcceptorRequest.buildRequest(envelope.getPublisher().getPublishSource()))) {
        return;
      }

      if (sessions.contains(envelope.getSessionProcessId().getHostAddress())) {
        publisherVersions.get(envelope.getSessionProcessId().getHostAddress()).put(registerId, v);
      }
    };
  }

  public static BiConsumer<String, PublisherGroup> publisherGroupsBiConsumer(
      Map<String, DatumSummary> summaries, SyncSlotAcceptorManager syncSlotAcceptorManager) {
    return (dataInfoId, publisherGroup) -> {
      if (!syncSlotAcceptorManager.accept(
          SyncAcceptorRequest.buildRequest(dataInfoId, publisherGroup.getServiceGroupType()))) {
        return;
      }

      Map<String /*registerId*/, RegisterVersion> publisherVersions =
          Maps.newHashMapWithExpectedSize(publisherGroup.pubSize());
      publisherGroup.foreach(publisherGroupBiConsumer(publisherVersions, syncSlotAcceptorManager));
      DatumSummary summary = new DatumSummary(dataInfoId, publisherVersions);
      if (!summary.isEmpty()) {
        summaries.put(dataInfoId, summary);
      }
    };
  }

  public static BiConsumer<String, PublisherEnvelope> publisherGroupBiConsumer(
      Map<String, RegisterVersion> publisherVersions,
      SyncSlotAcceptorManager syncSlotAcceptorManager) {
    return (registerId, envelope) -> {
      RegisterVersion v = envelope.getVersionIfPub();
      // v = null when envelope is unpub
      if (v == null
          || !syncSlotAcceptorManager.accept(
              SyncAcceptorRequest.buildRequest(envelope.getPublisher().getPublishSource()))) {
        return;
      }
      publisherVersions.put(registerId, v);
    };
  }
}
