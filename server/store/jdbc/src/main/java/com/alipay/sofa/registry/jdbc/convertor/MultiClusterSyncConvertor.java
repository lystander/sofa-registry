/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.jdbc.convertor;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.jdbc.domain.MultiClusterSyncDomain;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSyncConvertor.java, v 0.1 2022年04月15日 14:20 xiaojian.xj Exp $
 */
public class MultiClusterSyncConvertor {

  public static MultiClusterSyncDomain convert2Domain(MultiClusterSyncInfo info, String clusterId) {
    if (info == null) {
      return null;
    }
    return new MultiClusterSyncDomain(clusterId,
        info.getRemoteDataCenter(),
        info.getRemoteMetaAddress(),
        info.getDataVersion());
  }

  public static MultiClusterSyncInfo convert2Info(MultiClusterSyncDomain domain) {
    if (domain == null) {
      return null;
    }
    return new MultiClusterSyncInfo(
        domain.getDataCenter(),
        domain.getRemoteDataCenter(),
        domain.getRemoteMetaAddress(),
        domain.getDataVersion());
  }

  public static List<MultiClusterSyncInfo> convert2Infos(List<MultiClusterSyncDomain> domains) {
    if (CollectionUtils.isEmpty(domains)) {
      return Collections.EMPTY_LIST;
    }
    return domains.stream()
        .map(MultiClusterSyncConvertor::convert2Info)
        .collect(Collectors.toList());
  }
}
