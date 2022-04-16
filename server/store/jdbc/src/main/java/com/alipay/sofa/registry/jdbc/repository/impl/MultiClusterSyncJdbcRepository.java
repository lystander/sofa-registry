/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.jdbc.repository.impl;

import com.alipay.sofa.registry.common.model.metaserver.MultiClusterSyncInfo;
import com.alipay.sofa.registry.jdbc.constant.TableEnum;
import com.alipay.sofa.registry.jdbc.convertor.MultiClusterSyncConvertor;
import com.alipay.sofa.registry.jdbc.domain.MultiClusterSyncDomain;
import com.alipay.sofa.registry.jdbc.mapper.MultiClusterSyncMapper;
import com.alipay.sofa.registry.jdbc.version.config.BaseConfigRepository;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.store.api.config.DefaultCommonConfig;
import com.alipay.sofa.registry.store.api.meta.MultiClusterSyncRepository;
import com.alipay.sofa.registry.store.api.meta.RecoverConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author xiaojian.xj
 * @version : MultiClusterSyncJdbcRepository.java, v 0.1 2022年04月14日 15:12 xiaojian.xj Exp $
 */
public class MultiClusterSyncJdbcRepository implements MultiClusterSyncRepository, RecoverConfig {

  private static final Logger LOG =
      LoggerFactory.getLogger("MULTI-CLUSTER", "[UpdateSyncInfo]");

  @Autowired private MultiClusterSyncMapper multiClusterSyncMapper;

  @Autowired protected DefaultCommonConfig defaultCommonConfig;

  private Configer configer;

  public MultiClusterSyncJdbcRepository() {
    configer = new Configer();
  }

  class Configer extends BaseConfigRepository<MultiClusterSyncDomain> {
    public Configer() {
      super("MultiClusterSyncInfo", LOG);
    }

    @Override
    protected MultiClusterSyncDomain queryExistVersion(MultiClusterSyncDomain entry) {
      return multiClusterSyncMapper.query(entry.getDataCenter(), entry.getRemoteDataCenter());
    }

    @Override
    protected long insert(MultiClusterSyncDomain entry) {
      return multiClusterSyncMapper.save(entry);
    }

    @Override
    protected int updateWithExpectVersion(MultiClusterSyncDomain entry, long exist) {
      return multiClusterSyncMapper.update(entry, exist);
    }
  }

  @Override
  public boolean put(MultiClusterSyncInfo syncInfo) {
    return configer.put(
        MultiClusterSyncConvertor.convert2Domain(
            syncInfo, defaultCommonConfig.getClusterId(tableName())));
  }

  @Override
  public List<MultiClusterSyncInfo> queryAll() {
    List<MultiClusterSyncDomain> domains = multiClusterSyncMapper.queryByCluster(
            defaultCommonConfig.getClusterId(tableName()));
    return MultiClusterSyncConvertor.convert2Infos(domains);
  }

  @Override
  public int remove(String remoteDataCenter, long dataVersion) {
    return multiClusterSyncMapper.remove(defaultCommonConfig.getClusterId(tableName()),
            remoteDataCenter, dataVersion);
  }

  @Override
  public String tableName() {
    return TableEnum.MULTI_CLUSTER_SYNC_INFO.getTableName();
  }
}
