/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.data.providedata;

import com.alipay.sofa.registry.common.model.console.MultiSegmentSyncSwitch;
import com.alipay.sofa.registry.common.model.console.PersistenceData;
import com.alipay.sofa.registry.common.model.constants.MultiValueConstants;
import com.alipay.sofa.registry.common.model.slot.filter.RemoteSyncDataAcceptorManager;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.server.data.bootstrap.DataServerConfig;
import com.alipay.sofa.registry.server.data.providedata.FetchMultiSyncService.MultiSyncStorage;
import com.alipay.sofa.registry.server.shared.providedata.AbstractFetchPersistenceSystemProperty;
import com.alipay.sofa.registry.server.shared.providedata.SystemDataStorage;
import com.alipay.sofa.registry.store.api.meta.ProvideDataRepository;
import com.alipay.sofa.registry.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author xiaojian.xj
 * @version : FetchMultiSyncService.java, v 0.1 2022年07月20日 15:27 xiaojian.xj Exp $
 */
public class FetchMultiSyncService
    extends AbstractFetchPersistenceSystemProperty<MultiSyncStorage, MultiSyncStorage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchMultiSyncService.class);

  @Autowired private DataServerConfig dataServerConfig;

  @Autowired private ProvideDataRepository provideDataRepository;

  @Resource private RemoteSyncDataAcceptorManager remoteSyncDataAcceptorManager;

  private static final MultiSyncStorage INIT =
      new MultiSyncStorage(INIT_VERSION, new MultiSegmentSyncSwitch());

  public FetchMultiSyncService() {
    super(MultiValueConstants.DATA_SERVER_MULTI_SYNC_DATA_ID, INIT);
  }

  @Override
  protected int getSystemPropertyIntervalMillis() {
    return dataServerConfig.getSystemPropertyIntervalMillis();
  }

  @Override
  protected MultiSyncStorage fetchFromPersistence() {
    PersistenceData persistenceData =
        provideDataRepository.get(MultiValueConstants.SESSION_SERVER_MULTI_PUSH_DATA_ID);
    if (persistenceData == null) {
      return INIT;
    }

    MultiSegmentSyncSwitch read =
        JsonUtils.read(persistenceData.getData(), MultiSegmentSyncSwitch.class);
    return new MultiSyncStorage(persistenceData.getVersion(), read);
  }

  @Override
  protected boolean doProcess(MultiSyncStorage expect, MultiSyncStorage update) {

    try {
      if (!compareAndSet(expect, update)) {
        LOGGER.error("update multi sync switch:{} fail.", update);
        return false;
      }
      LOGGER.info("Fetch multi sync switch, prev={}, current={}", expect, update);

      remoteSyncDataAcceptorManager.updateFrom(getMultiSyncSwitch());
    } catch (Throwable t) {
      LOGGER.error("update multi sync switch:{} error.", update, t);
    }
    return true;
  }

  public boolean multiSync() {
    return this.storage.get().multiSegmentSyncSwitch.isMultiSync();
  }

  public MultiSegmentSyncSwitch getMultiSyncSwitch() {
    return this.storage.get().multiSegmentSyncSwitch;
  }

  public static class MultiSyncStorage extends SystemDataStorage {

    final MultiSegmentSyncSwitch multiSegmentSyncSwitch;

    public MultiSyncStorage(
        long version,
        MultiSegmentSyncSwitch multiSegmentSyncSwitch) {
      super(version);
      this.multiSegmentSyncSwitch = multiSegmentSyncSwitch;
    }

    boolean isMultiSync() {
      return multiSegmentSyncSwitch.isMultiSync();
    }


  }
}
