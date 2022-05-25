/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.common.model.store.DataInfo;
import org.apache.commons.lang.StringUtils;

/**
 * @author xiaojian.xj
 * @version : PublisherGroupType.java, v 0.1 2022年05月24日 22:21 xiaojian.xj Exp $
 */
public enum PublisherGroupType {
  SERVICE_MAPPING("SERVICE_MAPPING"),
  SERVICE_REVISION("SERVICE_REVISION"),;

  private final String code;

  PublisherGroupType(String code) {
    this.code = code;
  }

  public static boolean isServiceMapping(String dataInfoId) {
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    return StringUtils.equalsIgnoreCase(dataInfo.getGroup(), SERVICE_MAPPING.code);
  }

  public static boolean isRevision(String dataInfoId) {
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    return StringUtils.equalsIgnoreCase(dataInfo.getGroup(), SERVICE_REVISION.code);
  }


}
