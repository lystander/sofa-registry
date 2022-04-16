/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.meta.remoting.meta;

import com.alipay.sofa.registry.common.model.elector.LeaderInfo;
import com.alipay.sofa.registry.exception.MetaLeaderQueryException;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.server.meta.MetaLeaderService;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.server.shared.constant.ExchangerModeEnum;
import com.alipay.sofa.registry.server.shared.meta.AbstractMetaLeaderExchanger;
import com.alipay.sofa.registry.store.api.elector.AbstractLeaderElector;
import com.alipay.sofa.registry.util.StringFormatter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;

/**
 * @author xiaojian.xj
 * @version : LocalMetaExchanger.java, v 0.1 2022年04月16日 20:54 xiaojian.xj Exp $
 */
public class LocalMetaExchanger extends AbstractMetaLeaderExchanger {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalMetaExchanger.class);

  public LocalMetaExchanger() {
    super(Exchange.META_SERVER_TYPE, ExchangerModeEnum.LOCAL_DATA_CENTER, LOGGER);
  }

  @Autowired private MetaServerConfig metaServerConfig;

  @Autowired private MetaLeaderService metaLeaderService;

  @Override
  public LeaderInfo queryLeaderFromDb() {
    try {
      return retryer.call(
          () -> {
            AbstractLeaderElector.LeaderInfo hasNoLeader = AbstractLeaderElector.LeaderInfo.HAS_NO_LEADER;
            long leaderEpoch = metaLeaderService.getLeaderEpoch();
            String leader = metaLeaderService.getLeader();
            if (leaderEpoch == hasNoLeader.getEpoch()
                || StringUtils.equals(leader, hasNoLeader.getLeader())) {
              return null;
            }
            return new LeaderInfo(leaderEpoch, leader);
          });
    } catch (Throwable e) {
      throw new MetaLeaderQueryException(
          StringFormatter.format("query meta leader error from db failed"), e);
    }
  }

  @Override
  protected Collection<String> getMetaServerDomains(String dataCenter) {
    return Collections.emptyList();
  }

  @Override
  public int getRpcTimeoutMillis() {
    return metaServerConfig.getMetaNodeExchangeTimeoutMillis();
  }

  @Override
  public int getServerPort() {
    return metaServerConfig.getMetaServerPort();
  }

  @Override
  public int getConnNum() {
    return 3;
  }

  @Override
  protected Collection<ChannelHandler> getClientHandlers() {
    return Collections.emptyList();
  }
}
