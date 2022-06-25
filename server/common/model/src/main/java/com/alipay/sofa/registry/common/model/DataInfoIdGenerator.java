/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.common.model;

import com.alipay.sofa.registry.common.model.constants.ValueConstants;
import com.alipay.sofa.registry.common.model.store.DataInfo;
import com.alipay.sofa.registry.common.model.store.WordCache;

/**
 * @author xiaojian.xj
 * @version : DataInfoIdGenerator.java, v 0.1 2022年06月24日 20:44 xiaojian.xj Exp $
 */
public class DataInfoIdGenerator {

  public static String revisionId(String revision) {
    DataInfo dataInfo =
        new DataInfo(
            ValueConstants.DEFAULT_INSTANCE_ID,
            revision,
            PublisherGroupType.REGISTRY_REVISION.getCode());
    return WordCache.getWordCache(dataInfo.getDataInfoId());
  }

  public static String serviceAppsId(String dataInfoId) {
      return WordCache.getWordCache(PublisherGroupType.REGISTRY_MAPPING.getCode() + dataInfoId);

  }
}
