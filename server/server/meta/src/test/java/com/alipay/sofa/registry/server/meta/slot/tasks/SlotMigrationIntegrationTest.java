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
package com.alipay.sofa.registry.server.meta.slot.tasks;

import com.alipay.sofa.registry.common.model.metaserver.nodes.DataNode;
import com.alipay.sofa.registry.common.model.slot.Slot;
import com.alipay.sofa.registry.common.model.slot.SlotConfig;
import com.alipay.sofa.registry.common.model.slot.SlotTable;
import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.lifecycle.LifecycleState;
import com.alipay.sofa.registry.server.meta.AbstractTest;
import com.alipay.sofa.registry.server.meta.bootstrap.config.NodeConfig;
import com.alipay.sofa.registry.server.meta.lease.data.DefaultDataServerManager;
import com.alipay.sofa.registry.server.meta.monitor.SlotTableMonitor;
import com.alipay.sofa.registry.server.meta.slot.SlotManager;
import com.alipay.sofa.registry.server.meta.slot.arrange.ScheduledSlotArranger;
import com.alipay.sofa.registry.server.meta.slot.manager.DefaultSlotManager;
import com.alipay.sofa.registry.server.meta.slot.manager.LocalSlotManager;
import com.alipay.sofa.registry.server.meta.slot.util.builder.SlotTableBuilder;
import com.alipay.sofa.registry.server.meta.slot.util.comparator.DataNodeComparator;
import com.alipay.sofa.registry.util.FileUtils;
import com.alipay.sofa.registry.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author chen.zhu
 * <p>
 * Jan 14, 2021
 */
public class SlotMigrationIntegrationTest extends AbstractTest {

    private DefaultSlotManager       defaultSlotManager;

    private SlotManager              raftSlotManager;

    private LocalSlotManager         localSlotManager;

    @Mock
    private DefaultDataServerManager dataServerManager;

    @Mock
    private SlotTableMonitor         slotTableMonitor;

    @BeforeClass
    public static void beforeSlotMigrationIntegrationTestClass() {
        System.setProperty("slot.frozen.milli", "1");
    }

    @Before
    public void beforeSlotMigrationIntegrationTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(randomURL(randomIp()), getDc()),
            new DataNode(randomURL(randomIp()), getDc()), new DataNode(randomURL(randomIp()),
                getDc()), new DataNode(randomURL(randomIp()), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        NodeConfig nodeConfig = mock(NodeConfig.class);
        when(nodeConfig.getLocalDataCenter()).thenReturn(getDc());
        raftSlotManager = localSlotManager = new LocalSlotManager(nodeConfig);
        when(slotTableMonitor.isStableTableStable()).thenReturn(true);
        defaultSlotManager = new DefaultSlotManager(localSlotManager, raftSlotManager);
    }

    @Test
    public void testDataServerAddedOneByOne() throws Exception {
        System.setProperty("slot.leader.max.move", SlotConfig.SLOT_NUM + "");
        ScheduledSlotArranger assigner = new ScheduledSlotArranger(dataServerManager,
            localSlotManager, defaultSlotManager, slotTableMonitor);
        assigner.getLifecycleState().setPhase(LifecycleState.LifecyclePhase.STARTED);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"),
            getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        makeRaftLeader();
        assigner.arrangeSync();

        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());
    }

    @Test
    public void testDataServerAddedAndDeleted() throws Exception {
        System.setProperty("slot.leader.max.move", SlotConfig.SLOT_NUM + "");
        ScheduledSlotArranger assigner = new ScheduledSlotArranger(dataServerManager,
            localSlotManager, defaultSlotManager, slotTableMonitor);
        assigner.getLifecycleState().setPhase(LifecycleState.LifecyclePhase.STARTED);

        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"),
            getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        makeRaftLeader();
        assigner.arrangeSync();
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));

        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        assigner.arrangeSync();
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        loopArrange(assigner);

        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());

        Thread.sleep(2);
        dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"), getDc()),
            new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL("100.88.142.19"),
                getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        loopArrange(assigner);
        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());
    }

    @Test
    public void testDataLeaderBalance() throws Exception {
        ScheduledSlotArranger assigner = new ScheduledSlotArranger(dataServerManager,
            localSlotManager, defaultSlotManager, slotTableMonitor);
        assigner.getLifecycleState().setPhase(LifecycleState.LifecyclePhase.STARTED);

        byte[] bytes = FileUtils.readFileToByteArray(new File(
            "src/test/resources/test/slot-table.json"));
        SlotTable prevSlotTable = JsonUtils.getJacksonObjectMapper()
            .readValue(bytes, InnerSlotTable.class).toSlotTable();
        localSlotManager.refresh(prevSlotTable);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"),
            getDc()), new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL(
            "100.88.142.19"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        makeRaftLeader();

        loopArrange(assigner);

        logger.info(JsonUtils.getJacksonObjectMapper().writerWithDefaultPrettyPrinter()
            .writeValueAsString(localSlotManager.getSlotTable()));
        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());
    }

    @Test
    public void testDataLeaderBalance2() throws Exception {
        ScheduledSlotArranger assigner = new ScheduledSlotArranger(dataServerManager,
            localSlotManager, defaultSlotManager, slotTableMonitor);
        assigner.getLifecycleState().setPhase(LifecycleState.LifecyclePhase.STARTED);
        byte[] bytes = FileUtils.readFileToByteArray(new File(
            "src/test/resources/test/slot-table-2.json"));
        SlotTable prevSlotTable = JsonUtils.getJacksonObjectMapper()
            .readValue(bytes, InnerSlotTable.class).toSlotTable();
        localSlotManager.refresh(prevSlotTable);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(new URL("100.83.52.136"),
            getDc()), new DataNode(new URL("100.88.50.178"), getDc()), new DataNode(new URL(
            "11.166.229.74"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        makeRaftLeader();

        loopArrange(assigner);

        Assert.assertTrue(isSlotTableLeaderBalanced(localSlotManager.getSlotTable(),
            dataServerManager.getClusterMembers()));
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());
    }

    @Test
    public void testNoInfinityLoop() throws Exception {
        ScheduledSlotArranger assigner = new ScheduledSlotArranger(dataServerManager,
            localSlotManager, defaultSlotManager, slotTableMonitor);
        assigner.getLifecycleState().setPhase(LifecycleState.LifecyclePhase.STARTED);

        byte[] bytes = FileUtils.readFileToByteArray(new File(
            "src/test/resources/test/slot-table.json"));
        SlotTable prevSlotTable = JsonUtils.getJacksonObjectMapper()
            .readValue(bytes, InnerSlotTable.class).toSlotTable();
        localSlotManager.refresh(prevSlotTable);
        List<DataNode> dataNodes = Lists.newArrayList(new DataNode(new URL("100.88.142.32"),
            getDc()), new DataNode(new URL("100.88.142.36"), getDc()), new DataNode(new URL(
            "100.88.142.19"), getDc()));
        when(dataServerManager.getClusterMembers()).thenReturn(dataNodes);
        makeRaftLeader();

        loopArrange(assigner);
        assertSlotTableNoDupLeaderFollower(localSlotManager.getSlotTable());
    }

    public static class InnerSlotTable {
        private long         epoch;
        private List<Slot>   slots;
        private List<String> dataServers;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public InnerSlotTable(@JsonProperty("id") long epoch,
                              @JsonProperty("slots") List<Slot> slots,
                              @JsonProperty("dataServers") List<String> dataServers) {
            this.epoch = epoch;
            this.slots = slots;
            this.dataServers = dataServers;
        }

        public long getEpoch() {
            return epoch;
        }

        public List<Slot> getSlots() {
            return slots;
        }

        public List<String> getDataServers() {
            return dataServers;
        }

        public SlotTable toSlotTable() {
            Map<Integer, Slot> slotMap = Maps.newHashMap();
            slots.forEach(slot -> slotMap.put(slot.getId(), slot));
            return new SlotTable(epoch, slotMap.values());
        }
    }

    private SlotTableBuilder createSlotTableBuilder(List<String> currentDataNodeIps) {
        SlotTable slotTable = localSlotManager.getSlotTable();
        DataNodeComparator comparator = new DataNodeComparator(slotTable.getDataServers(), currentDataNodeIps);
        SlotTableBuilder slotTableBuilder = new SlotTableBuilder(slotTable,
                localSlotManager.getSlotNums(), localSlotManager.getSlotReplicaNums());

        slotTableBuilder.init(currentDataNodeIps);

        comparator.getRemoved().forEach(slotTableBuilder::removeDataServerSlots);
        return slotTableBuilder;
    }

    private void loopArrange(ScheduledSlotArranger arranger) {
        for (int i = 0; i < 100; i++) {
            arranger.arrangeSync();
        }
    }
}
