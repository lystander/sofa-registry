/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.common.model.store.DataInfo;
import org.apache.commons.lang.StringUtils;

/**
 * @author xiaojian.xj
 * @version : PublisherGroupType.java, v 0.1 2022年05月24日 22:21 xiaojian.xj Exp $
 */
public enum PublisherGroupType {
  REGISTRY_MAPPING("REGISTRY_MAPPING."),
  REGISTRY_REVISION("REGISTRY_REVISION"),
  SERVICE("REGISTRY"),;

  private final String code;

  PublisherGroupType(String code) {
    this.code = code;
  }

  public static boolean isServiceMapping(String dataInfoId) {
    return StringUtils.startsWith(dataInfoId, REGISTRY_MAPPING.code);
  }

  public static boolean isRevision(String dataInfoId) {
    DataInfo dataInfo = DataInfo.valueOf(dataInfoId);
    return StringUtils.equals(dataInfo.getGroup(), REGISTRY_REVISION.code);
  }

  public static PublisherGroupType of(String dataInfoId) {
    if (isServiceMapping(dataInfoId)) {
      return REGISTRY_MAPPING;
    } else if (isRevision(dataInfoId)) {
      return REGISTRY_REVISION;
    }
    return SERVICE;
  }

  /**
   * Getter method for property <tt>code</tt>.
   *
   * @return property value of code
   */
  public String getCode() {
    return code;
  }
}
