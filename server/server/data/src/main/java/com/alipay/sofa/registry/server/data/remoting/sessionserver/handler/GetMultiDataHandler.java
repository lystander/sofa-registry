/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.remoting.sessionserver.handler;

import com.alipay.sofa.registry.common.model.dataserver.GetMultiDataRequest;
import com.alipay.sofa.registry.common.model.slot.MultiSlotAccessGenericResponse;
import com.alipay.sofa.registry.common.model.slot.SlotAccess;
import com.alipay.sofa.registry.common.model.slot.SlotAccessGenericResponse;
import com.alipay.sofa.registry.common.model.store.MultiSubDatum;
import com.alipay.sofa.registry.common.model.store.SubDatum;
import com.alipay.sofa.registry.remoting.Channel;
import com.alipay.sofa.registry.util.ParaCheckUtil;
import com.alipay.sofa.registry.util.StringFormatter;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author xiaojian.xj
 * @version : MultiGetDataHandler.java, v 0.1 2022年06月20日 16:32 xiaojian.xj Exp $
 */
public class GetMultiDataHandler extends BaseGetDataHandler<GetMultiDataRequest> {

  @Override
  public void checkParam(GetMultiDataRequest request) {
    ParaCheckUtil.checkNotBlank(request.getDataInfoId(), "GetMultiDataRequest.dataInfoId");
    ParaCheckUtil.checkNotEmpty(
        request.getSlotTableEpochs(), "GetMultiDataRequest.slotTableEpochs");
    ParaCheckUtil.checkNotEmpty(
        request.getSlotLeaderEpochs(), "GetMultiDataRequest.slotLeaderEpochs");

    for (Entry<String, Long> entry : request.getSlotTableEpochs().entrySet()) {
      String dataCenter = entry.getKey();
      ParaCheckUtil.checkNotNull(entry.getValue(), dataCenter + ".slotTableEpoch");
      ParaCheckUtil.checkNotNull(
          request.getSlotLeaderEpochs().get(dataCenter), dataCenter + ".slotLeaderEpoch");
    }

    checkSessionProcessId(request.getSessionProcessId());
  }

  /**
   * return processor request class name
   *
   * @return
   */
  @Override
  public Class interest() {
    return GetMultiDataRequest.class;
  }

  /**
   * execute
   *
   * @param channel
   * @param request
   * @return
   */
  @Override
  public MultiSlotAccessGenericResponse<MultiSubDatum> doHandle(Channel channel, GetMultiDataRequest request) {
    processSessionProcessId(channel, request.getSessionProcessId());

    int dataCenterSize = request.getSlotLeaderEpochs().size();

    boolean success = true;
    StringBuilder builder = new StringBuilder();
    Map<String, SlotAccess> slotAccessMap = Maps.newHashMapWithExpectedSize(dataCenterSize);
    Map<String, SubDatum> datumMap = Maps.newHashMapWithExpectedSize(dataCenterSize);
    for (Entry<String, Long> entry : request.getSlotLeaderEpochs().entrySet()) {
      String dataCenter = entry.getKey();
      SlotAccessGenericResponse<SubDatum> res = processSingleDataCenter(dataCenter,
              request.getDataInfoId(), entry.getValue(), request.getSlotLeaderEpochs().get(dataCenter), request.getAcceptEncodes());
      if (!res.isSuccess()) {
        success = false;
        builder.append(StringFormatter.format("{}:{}.", dataCenter, res.getMessage()));
      }
      slotAccessMap.put(dataCenter, res.getSlotAccess());
      datumMap.put(dataCenter, res.getData());
    }
    MultiSubDatum data = new MultiSubDatum(request.getDataInfoId(), datumMap);

    return new MultiSlotAccessGenericResponse(success, builder.toString(), data, slotAccessMap);
  }
}
