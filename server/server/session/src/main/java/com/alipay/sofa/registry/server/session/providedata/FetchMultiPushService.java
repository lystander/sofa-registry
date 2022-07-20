/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.session.providedata;

import com.alipay.sofa.registry.common.model.console.MultiSegmentPushSwitch;
import com.alipay.sofa.registry.common.model.console.PersistenceData;
import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.session.bootstrap.SessionServerConfig;
import com.alipay.sofa.registry.server.session.providedata.FetchMultiPushService.MultiPushStorage;
import com.alipay.sofa.registry.server.shared.providedata.AbstractFetchPersistenceSystemProperty;
import com.alipay.sofa.registry.server.shared.providedata.SystemDataStorage;
import com.alipay.sofa.registry.store.api.meta.ProvideDataRepository;
import com.alipay.sofa.registry.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : FetchMultiPushService.java, v 0.1 2022年07月20日 14:29 xiaojian.xj Exp $
 */
public class FetchMultiPushService
    extends AbstractFetchPersistenceSystemProperty<MultiPushStorage, MultiPushStorage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchMultiPushService.class);

  @Autowired private SessionServerConfig sessionServerConfig;

  @Autowired private ProvideDataRepository provideDataRepository;

  private static final MultiPushStorage INIT =
      new MultiPushStorage(INIT_VERSION, new MultiSegmentPushSwitch());

  public FetchMultiPushService() {
    super(MultiValueConstants.SESSION_SERVER_MULTI_PUSH_DATA_ID, INIT);
  }

  @Override
  protected int getSystemPropertyIntervalMillis() {
    return sessionServerConfig.getSystemPropertyIntervalMillis();
  }

  @Override
  protected MultiPushStorage fetchFromPersistence() {
    PersistenceData persistenceData =
        provideDataRepository.get(MultiValueConstants.SESSION_SERVER_MULTI_PUSH_DATA_ID);
    if (persistenceData == null) {
      return INIT;
    }

    MultiSegmentPushSwitch read =
        JsonUtils.read(persistenceData.getData(), MultiSegmentPushSwitch.class);
    return new MultiPushStorage(
        persistenceData.getVersion(), read);
  }

  @Override
  protected boolean doProcess(MultiPushStorage expect, MultiPushStorage update) {

    try {
      if (!compareAndSet(expect, update)) {
        LOGGER.error("update multi push switch:{} fail.", update);
        return false;
      }
      LOGGER.info("Fetch multi push switch, prev={}, current={}", expect, update);

    } catch (Throwable t) {
      LOGGER.error("update multi push switch:{} error.", update, t);
    }
    return true;
  }

  // todo xiaojian.xj
  public boolean multiPush(String dataInfoId) {
    return this.storage.get().multiSegmentPushSwitch.isMultiPush();
  }

  public static class MultiPushStorage extends SystemDataStorage {
    final MultiSegmentPushSwitch multiSegmentPushSwitch;

    public MultiPushStorage(
        long version,
        MultiSegmentPushSwitch multiSegmentPushSwitch) {
      super(version);
      this.multiSegmentPushSwitch = multiSegmentPushSwitch;
    }
  }
}
