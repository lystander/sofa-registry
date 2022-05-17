/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.data.slot;

import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.filter.SyncSlotAcceptorManager;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.server.shared.remoting.ClientSideExchanger;
import com.alipay.sofa.registry.task.TaskErrorSilenceException;
import com.alipay.sofa.registry.util.StringFormatter;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author xiaojian.xj
 * @version : SyncLeaderTask.java, v 0.1 2022年05月10日 21:33 xiaojian.xj Exp $
 */
public class SyncLeaderTask implements Runnable {

    private final String localDataCenter;
    private final String syncDataCenter;
    private final boolean syncLocalDataCenter;

    private final long                startTimestamp = System.currentTimeMillis();
    private final long                slotTableEpoch;
    private final Slot                slot;
    private final SlotDiffSyncer      syncer;
    private final ClientSideExchanger clientSideExchanger;
    private final SyncContinues           continues;
    private final SyncSlotAcceptorManager acceptorManager;

    private final Logger SYNC_DIGEST_LOGGER;
    private final Logger SYNC_ERROR_LOGGER;


    public SyncLeaderTask(
            String localDataCenter,
            String syncDataCenter,
            long slotTableEpoch,
            Slot slot,
            SlotDiffSyncer syncer,
            ClientSideExchanger clientSideExchanger,
            SyncContinues continues,
            SyncSlotAcceptorManager acceptorManager,
            Logger syncDigestLogger,
            Logger syncErrorLogger) {
        this.localDataCenter = localDataCenter;
        this.syncDataCenter = syncDataCenter;
        syncLocalDataCenter = StringUtils.equals(localDataCenter, syncDataCenter);

        this.slotTableEpoch = slotTableEpoch;
        this.slot = slot;
        this.syncer = syncer;
        this.clientSideExchanger = clientSideExchanger;
        this.continues = continues;
        this.acceptorManager = acceptorManager;

        this.SYNC_DIGEST_LOGGER = syncDigestLogger;
        this.SYNC_ERROR_LOGGER = syncErrorLogger;
    }

    @Override
    public void run() {
        boolean success = false;
        try {
            success =
                    syncer.syncSlotLeader(
                            localDataCenter, syncDataCenter, syncLocalDataCenter, slot.getId(), slot.getLeader(), clientSideExchanger, slotTableEpoch, continues, acceptorManager);
            if (!success) {
                throw new RuntimeException(StringFormatter.format("{} sync leader failed", syncDataCenter));
            }
        } catch (Throwable e) {
            SYNC_ERROR_LOGGER.error(
                    "[syncLeader]syncLocal={}, syncDataCenter={}, failed={}, slot={}", syncDataCenter, syncDataCenter, slot.getLeader(), slot.getId(), e);
            // rethrow silence exception, notify the task is failed
            throw TaskErrorSilenceException.INSTANCE;
        } finally {
            SYNC_DIGEST_LOGGER.info(
                    "[syncLeader]{},{},{},{},{},span={}",
                    success ? 'Y' : 'N',
                    syncLocalDataCenter ? 'Y' : 'N',
                    syncDataCenter,
                    slot.getId(),
                    slot.getLeader(),
                    System.currentTimeMillis() - startTimestamp);
        }
    }

    @Override
    public String toString() {
        return "SyncLeaderTask{" +
                "syncDataCenter=" + syncDataCenter +
                ", syncLocalDataCenter=" + syncLocalDataCenter +
                ", slotTableEpoch=" + slotTableEpoch +
                ", slot=" + slot +
                '}';
    }
}
