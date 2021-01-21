/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.registry.server.meta.slot.manager;

import com.alipay.sofa.registry.common.model.metaserver.nodes.DataNode;
import com.alipay.sofa.registry.common.model.slot.DataNodeSlot;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.exception.DisposeException;
import com.alipay.sofa.registry.exception.StopException;
import com.alipay.sofa.registry.jraft.bootstrap.ServiceStateMachine;
import com.alipay.sofa.registry.lifecycle.impl.AbstractLifecycle;
import com.alipay.sofa.registry.lifecycle.impl.LifecycleHelper;
import com.alipay.sofa.registry.server.meta.slot.SlotManager;
import com.alipay.sofa.registry.store.api.annotation.RaftReference;
import com.alipay.sofa.registry.store.api.annotation.RaftReferenceContainer;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * @author chen.zhu
 * <p>
 * Dec 02, 2020
 */
@RaftReferenceContainer
public class DefaultSlotManager extends AbstractLifecycle implements SlotManager {

    @Autowired
    private LocalSlotManager         localSlotManager;

    @RaftReference(uniqueId = LocalSlotManager.LOCAL_SLOT_MANAGER, interfaceType = SlotManager.class)
    private SlotManager              raftSlotManager;

    private ScheduledExecutorService scheduled;

    private ScheduledFuture<?>       future;

    public DefaultSlotManager() {
    }

    public DefaultSlotManager(LocalSlotManager localSlotManager, SlotManager raftSlotManager) {
        this.localSlotManager = localSlotManager;
        this.raftSlotManager = raftSlotManager;
    }

    /**
     * Post construct.
     *
     * @throws Exception the exception
     */
    @PostConstruct
    public void postConstruct() throws Exception {
        LifecycleHelper.initializeIfPossible(this);
        LifecycleHelper.startIfPossible(this);
    }

    /**
     * Pre destroy.
     *
     * @throws Exception the exception
     */
    @PreDestroy
    public void preDestroy() throws Exception {
        LifecycleHelper.stopIfPossible(this);
        LifecycleHelper.disposeIfPossible(this);
    }

    @Override
    protected void doStop() throws StopException {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        super.doStop();
    }

    @Override
    protected void doDispose() throws DisposeException {
        if (scheduled != null) {
            scheduled.shutdownNow();
            scheduled = null;
        }
        super.doDispose();
    }

    /**
     * Refresh.
     *
     * @param slotTable the slot table
     */
    @Override
    public void refresh(SlotTable slotTable) {
        raftSlotManager.refresh(slotTable);
    }

    /**
     * Gets get raft slot manager.
     *
     * @return the get raft slot manager
     */
    public SlotManager getRaftSlotManager() {
        return raftSlotManager;
    }

    /**
     * Gets get slot nums.
     *
     * @return the get slot nums
     */
    @Override
    public int getSlotNums() {
        return getSlotManager().getSlotNums();
    }

    /**
     * Gets get slot replica nums.
     *
     * @return the get slot replica nums
     */
    @Override
    public int getSlotReplicaNums() {
        return getSlotManager().getSlotReplicaNums();
    }

    /**
     * Gets get data node managed slot.
     *
     * @param dataNode        the data node
     * @param ignoreFollowers the ignore followers
     * @return the get data node managed slot
     */
    @Override
    public DataNodeSlot getDataNodeManagedSlot(DataNode dataNode, boolean ignoreFollowers) {
        return getSlotManager().getDataNodeManagedSlot(dataNode, ignoreFollowers);
    }

    /**
     * Gets get slot table.
     *
     * @return the get slot table
     */
    @Override
    public SlotTable getSlotTable() {
        return getSlotManager().getSlotTable();
    }

    private SlotManager getSlotManager() {
        if (isRaftLeader()) {
            return localSlotManager;
        } else {
            return raftSlotManager;
        }
    }

    protected boolean isRaftLeader() {
        return ServiceStateMachine.getInstance().isLeader();
    }

    @VisibleForTesting
    DefaultSlotManager setLocalSlotManager(LocalSlotManager localSlotManager) {
        this.localSlotManager = localSlotManager;
        return this;
    }

    @VisibleForTesting
    DefaultSlotManager setRaftSlotManager(SlotManager raftSlotManager) {
        this.raftSlotManager = raftSlotManager;
        return this;
    }

}
