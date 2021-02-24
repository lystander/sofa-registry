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
package com.alipay.sofa.registry.server.session.bootstrap;

import java.util.ArrayList;
import java.util.Collection;

import com.alipay.sofa.registry.server.session.cache.*;
import com.alipay.sofa.registry.server.session.connections.ConnectionsService;
import com.alipay.sofa.registry.server.session.node.service.*;
import com.alipay.sofa.registry.server.session.push.ChangeProcessor;
import com.alipay.sofa.registry.server.session.push.FirePushService;
import com.alipay.sofa.registry.server.session.push.PushDataGenerator;
import com.alipay.sofa.registry.server.session.push.PushProcessor;
import com.alipay.sofa.registry.server.session.remoting.handler.*;
import com.alipay.sofa.registry.server.session.resource.*;
import com.alipay.sofa.registry.server.session.scheduler.timertask.CacheCountTask;
import com.alipay.sofa.registry.server.session.scheduler.timertask.SessionCacheDigestTask;
import com.alipay.sofa.registry.server.session.strategy.*;
import com.alipay.sofa.registry.server.shared.remoting.SlotTableChangeEventHandler;
import com.alipay.sofa.registry.server.shared.resource.MetricsResource;
import com.alipay.sofa.registry.server.shared.resource.SlotGenericResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alipay.sofa.registry.remoting.bolt.exchange.BoltExchange;
import com.alipay.sofa.registry.remoting.exchange.Exchange;
import com.alipay.sofa.registry.remoting.exchange.NodeExchanger;
import com.alipay.sofa.registry.remoting.jersey.exchange.JerseyExchange;
import com.alipay.sofa.registry.server.session.acceptor.WriteDataAcceptor;
import com.alipay.sofa.registry.server.session.acceptor.WriteDataAcceptorImpl;
import com.alipay.sofa.registry.server.session.cache.CacheGenerator;
import com.alipay.sofa.registry.server.session.cache.CacheService;
import com.alipay.sofa.registry.server.session.cache.DatumCacheGenerator;
import com.alipay.sofa.registry.server.session.cache.SessionCacheService;
import com.alipay.sofa.registry.server.session.filter.DataIdMatchStrategy;
import com.alipay.sofa.registry.server.session.filter.IPMatchStrategy;
import com.alipay.sofa.registry.server.session.filter.ProcessFilter;
import com.alipay.sofa.registry.server.session.filter.blacklist.*;
import com.alipay.sofa.registry.server.session.limit.AccessLimitService;
import com.alipay.sofa.registry.server.session.limit.AccessLimitServiceImpl;
import com.alipay.sofa.registry.server.session.listener.*;
import com.alipay.sofa.registry.server.session.node.RaftClientManager;
import com.alipay.sofa.registry.server.session.node.processor.ClientNodeSingleTaskProcessor;
import com.alipay.sofa.registry.server.session.node.processor.ConsoleSyncSingleTaskProcessor;
import com.alipay.sofa.registry.server.session.node.processor.DataNodeSingleTaskProcessor;
import com.alipay.sofa.registry.server.session.node.processor.MetaNodeSingleTaskProcessor;
import com.alipay.sofa.registry.server.session.provideData.ProvideDataProcessor;
import com.alipay.sofa.registry.server.session.provideData.ProvideDataProcessorManager;
import com.alipay.sofa.registry.server.session.provideData.processor.BlackListProvideDataProcessor;
import com.alipay.sofa.registry.server.session.provideData.processor.StopPushProvideDataProcessor;
import com.alipay.sofa.registry.server.session.registry.Registry;
import com.alipay.sofa.registry.server.session.registry.SessionRegistry;
import com.alipay.sofa.registry.server.session.remoting.ClientNodeExchanger;
import com.alipay.sofa.registry.server.session.remoting.DataNodeExchanger;
import com.alipay.sofa.registry.server.session.remoting.MetaNodeExchanger;
import com.alipay.sofa.registry.server.session.scheduler.ExecutorManager;
import com.alipay.sofa.registry.server.session.scheduler.timertask.SyncClientsHeartbeatTask;
import com.alipay.sofa.registry.server.session.slot.SlotTableCache;
import com.alipay.sofa.registry.server.session.slot.SlotTableCacheImpl;
import com.alipay.sofa.registry.server.session.store.*;
import com.alipay.sofa.registry.server.shared.meta.MetaServerService;
import com.alipay.sofa.registry.server.shared.remoting.AbstractClientHandler;
import com.alipay.sofa.registry.server.shared.remoting.AbstractServerHandler;
import com.alipay.sofa.registry.server.session.store.DataStore;
import com.alipay.sofa.registry.server.session.store.Interests;
import com.alipay.sofa.registry.server.session.store.SessionInterests;
import com.alipay.sofa.registry.server.session.store.SessionWatchers;
import com.alipay.sofa.registry.server.session.store.Watchers;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultPublisherHandlerStrategy;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultReceivedConfigDataPushTaskStrategy;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultSessionRegistryStrategy;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultSubscriberHandlerStrategy;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultSyncConfigHandlerStrategy;
import com.alipay.sofa.registry.server.session.strategy.impl.DefaultWatcherHandlerStrategy;
import com.alipay.sofa.registry.server.session.wrapper.AccessLimitWrapperInterceptor;
import com.alipay.sofa.registry.server.session.wrapper.BlacklistWrapperInterceptor;
import com.alipay.sofa.registry.server.session.wrapper.ClientCheckWrapperInterceptor;
import com.alipay.sofa.registry.server.session.wrapper.WrapperInterceptor;
import com.alipay.sofa.registry.server.session.wrapper.WrapperInterceptorManager;
import com.alipay.sofa.registry.task.batcher.TaskProcessor;
import com.alipay.sofa.registry.task.listener.DefaultTaskListenerManager;
import com.alipay.sofa.registry.task.listener.TaskListener;
import com.alipay.sofa.registry.task.listener.TaskListenerManager;
import com.alipay.sofa.registry.util.PropertySplitter;

/**
 * @author shangyu.wh
 * @version $Id: SessionServerConfiguration.java, v 0.1 2017-11-14 11:39 synex Exp $
 */
@Configuration
@Import(SessionServerInitializer.class)
@EnableConfigurationProperties
public class SessionServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SessionServerBootstrap sessionServerBootstrap() {
        return new SessionServerBootstrap();
    }

    @Configuration
    public static class SessionServerConfigBeanConfiguration {
        @Bean
        public CommonConfig commonConfig() {
            return new CommonConfig();
        }

        @Bean
        @ConditionalOnMissingBean
        public SessionServerConfig sessionServerConfig(CommonConfig commonConfig) {
            return new SessionServerConfigBean(commonConfig);
        }

        @Bean(name = "PropertySplitter")
        public PropertySplitter propertySplitter() {
            return new PropertySplitter();
        }

    }

    @Configuration
    public static class SessionRemotingConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "boltExchange")
        public Exchange boltExchange() {
            return new BoltExchange();
        }

        @Bean
        public Exchange jerseyExchange() {
            return new JerseyExchange();
        }

        @Bean
        public NodeExchanger clientNodeExchanger() {
            return new ClientNodeExchanger();
        }

        @Bean
        public DataNodeExchanger dataNodeExchanger() {
            return new DataNodeExchanger();
        }

        @Bean
        public RaftClientManager raftClientManager() {
            return new RaftClientManager();
        }

        @Bean
        public MetaNodeExchanger metaNodeExchanger() {
            return new MetaNodeExchanger();
        }

        @Bean
        public SlotTableCache slotTableCache() {
            return new SlotTableCacheImpl();
        }

        @Bean(name = "serverHandlers")
        public Collection<AbstractServerHandler> serverHandlers() {
            Collection<AbstractServerHandler> list = new ArrayList<>();
            list.add(publisherHandler());
            list.add(subscriberHandler());
            list.add(watcherHandler());
            list.add(clientNodeConnectionHandler());
            list.add(cancelAddressRequestHandler());
            list.add(syncConfigHandler());
            list.add(appRevisionRegisterHandler());
            list.add(appRevisionRegisterPbHandler());
            list.add(publisherPbHandler());
            list.add(subscriberPbHandler());
            list.add(syncConfigPbHandler());
            return list;
        }

        @Bean(name = "sessionSyncHandlers")
        public Collection<AbstractServerHandler> serverSyncHandlers() {
            Collection<AbstractServerHandler> list = new ArrayList<>();
            list.add(dataSlotDiffDigestRequestHandler());
            list.add(dataSlotDiffPublisherRequestHandler());
            return list;
        }

        @Bean
        public AbstractServerHandler publisherHandler() {
            return new PublisherHandler();
        }

        @Bean
        public AbstractServerHandler syncConfigHandler() {
            return new SyncConfigHandler();
        }

        @Bean
        public AbstractServerHandler subscriberHandler() {
            return new SubscriberHandler();
        }

        @Bean
        public AbstractServerHandler watcherHandler() {
            return new WatcherHandler();
        }

        @Bean
        public AbstractServerHandler clientNodeConnectionHandler() {
            return new ClientNodeConnectionHandler();
        }

        @Bean
        public AbstractServerHandler cancelAddressRequestHandler() {
            return new CancelAddressRequestHandler();
        }

        @Bean
        public AbstractServerHandler dataSlotDiffDigestRequestHandler() {
            return new DataSlotDiffDigestRequestHandler();
        }

        @Bean
        public AbstractServerHandler dataSlotDiffPublisherRequestHandler() {
            return new DataSlotDiffPublisherRequestHandler();
        }

        @Bean
        public AbstractServerHandler publisherPbHandler() {
            return new PublisherPbHandler();
        }

        @Bean
        public AbstractServerHandler appRevisionRegisterPbHandler() {
            return new AppRevisionRegisterPbHandler();
        }

        @Bean
        public AbstractServerHandler subscriberPbHandler() {
            return new SubscriberPbHandler();
        }

        @Bean
        public AbstractServerHandler syncConfigPbHandler() {
            return new SyncConfigPbHandler();
        }

        @Bean
        public AbstractServerHandler appRevisionRegisterHandler() {
            return new AppRevisionRegisterHandler();
        }

        @Bean(name = "dataClientHandlers")
        public Collection<AbstractClientHandler> dataClientHandlers() {
            Collection<AbstractClientHandler> list = new ArrayList<>();
            list.add(dataChangeRequestHandler());
            list.add(dataPushRequestHandler());
            return list;
        }

        @Bean(name = "metaClientHandlers")
        public Collection<AbstractClientHandler> metaClientHandlers() {
            Collection<AbstractClientHandler> list = new ArrayList<>();
            list.add(metaNodeConnectionHandler());
            list.add(notifyProvideDataChangeHandler());
            list.add(loadbalanceMetricsHandler());
            list.add(configureLoadbalanceHandler());
            list.add(slotTableChangeEventHandler());
            return list;
        }

        @Bean
        public AbstractClientHandler metaNodeConnectionHandler() {
            return new MetaNodeConnectionHandler();
        }

        @Bean
        public AbstractClientHandler dataChangeRequestHandler() {
            return new DataChangeRequestHandler();
        }

        @Bean
        public AbstractClientHandler dataPushRequestHandler() {
            return new DataPushRequestHandler();
        }

        @Bean
        public AbstractClientHandler notifyProvideDataChangeHandler() {
            return new NotifyProvideDataChangeHandler();
        }

        @Bean
        public AbstractClientHandler loadbalanceMetricsHandler() {
            return new LoadbalanceMetricsHandler();
        }

        @Bean
        public AbstractClientHandler configureLoadbalanceHandler() {
            return new ConfigureLoadbalanceHandler();
        }

        @Bean
        public SlotTableChangeEventHandler slotTableChangeEventHandler() {
            return new SlotTableChangeEventHandler();
        }
    }

    @Configuration
    public static class ResourceConfiguration {

        @Bean
        public ResourceConfig jerseyResourceConfig() {
            ResourceConfig resourceConfig = new ResourceConfig();
            resourceConfig.register(JacksonFeature.class);
            return resourceConfig;
        }

        @Bean
        @ConditionalOnMissingBean(name = "sessionOpenResource")
        public SessionOpenResource sessionOpenResource() {
            return new SessionOpenResource();
        }

        @Bean
        public SessionDigestResource sessionDigestResource() {
            return new SessionDigestResource();
        }

        @Bean
        @ConditionalOnMissingBean(name = "healthResource")
        public HealthResource healthResource() {
            return new HealthResource();
        }

        @Bean
        public ClientsOpenResource clientsOpenResource() {
            return new ClientsOpenResource();
        }

        @Bean
        public ConnectionsResource connectionsResource() {
            return new ConnectionsResource();
        }

        @Bean
        public SlotGenericResource slotGenericResource() {
            return new SlotGenericResource();
        }

        @Bean
        public MetricsResource metricsResource() {
            return new MetricsResource();
        }
    }

    @Configuration
    public static class SessionRegistryConfiguration {
        @Bean
        @ConditionalOnMissingBean(name = "sessionRegistry")
        public Registry sessionRegistry() {
            return new SessionRegistry();
        }

        @Bean
        @ConditionalOnMissingBean
        public Interests sessionInterests() {
            return new SessionInterests();
        }

        @Bean
        @ConditionalOnMissingBean
        public Watchers sessionWatchers() {
            return new SessionWatchers();
        }

        @Bean
        @ConditionalOnMissingBean
        public DataStore sessionDataStore() {
            return new SlotSessionDataStore();
        }
    }

    @Configuration
    public static class SessionNodeConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DataNodeService dataNodeService() {
            return new DataNodeServiceImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public MetaServerService metaServerService() {
            return new MetaServerServiceImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public ClientNodeService clientNodeService() {
            return new ClientNodeServiceImpl();
        }

        @Bean
        public AppRevisionNodeService appRevisionNodeService() {
            return new AppRevisionNodeServiceImpl();
        }

        @Bean
        @ConditionalOnMissingBean
        public FirePushService firePushService() {
            return new FirePushService();
        }

        @Bean
        @ConditionalOnMissingBean
        public PushProcessor pushProcessor() {
            return new PushProcessor();
        }

        @Bean
        @ConditionalOnMissingBean
        public ChangeProcessor changeProcessor() {
            return new ChangeProcessor();
        }

        @Bean
        @ConditionalOnMissingBean
        public PushDataGenerator pushDataGenerator() {
            return new PushDataGenerator();
        }
    }

    @Configuration
    public static class SessionCacheConfiguration {

        @Bean
        public CacheService sessionCacheService() {
            return new SessionCacheService();
        }

        @Bean(name = "com.alipay.sofa.registry.server.session.cache.DatumKey")
        public CacheGenerator datumCacheGenerator() {
            return new DatumCacheGenerator();
        }

        @Bean
        public AppRevisionCacheRegistry appRevisionCacheRegistry() {
            return new AppRevisionCacheRegistry();
        }
    }

    @Configuration
    public static class SessionTaskConfiguration {

        @Bean
        public TaskProcessor dataNodeSingleTaskProcessor() {
            return new DataNodeSingleTaskProcessor();
        }

        @Bean
        public TaskProcessor metaNodeSingleTaskProcessor() {
            return new MetaNodeSingleTaskProcessor();
        }

        @Bean
        public TaskProcessor clientNodeSingleTaskProcessor() {
            return new ClientNodeSingleTaskProcessor();
        }

        @Bean
        public TaskProcessor consoleSyncSingleTaskProcessor() {
            return new ConsoleSyncSingleTaskProcessor();
        }

        @Bean
        public TaskListener watcherRegisterFetchTaskListener(TaskListenerManager taskListenerManager) {
            TaskListener taskListener = new WatcherRegisterFetchTaskListener(
                metaNodeSingleTaskProcessor());
            taskListenerManager.addTaskListener(taskListener);
            return taskListener;
        }

        @Bean
        public TaskListener provideDataChangeFetchTaskListener(TaskListenerManager taskListenerManager) {
            TaskListener taskListener = new ProvideDataChangeFetchTaskListener(
                metaNodeSingleTaskProcessor());
            taskListenerManager.addTaskListener(taskListener);
            return taskListener;
        }

        @Bean
        public TaskListener receivedConfigDataPushTaskListener(TaskListenerManager taskListenerManager) {
            TaskListener taskListener = new ReceivedConfigDataPushTaskListener(
                clientNodeSingleTaskProcessor());
            taskListenerManager.addTaskListener(taskListener);
            return taskListener;
        }

        @Bean
        public TaskListenerManager taskListenerManager() {
            return new DefaultTaskListenerManager();
        }

    }

    @Configuration
    public static class ExecutorConfiguration {

        @Bean
        public ExecutorManager executorManager(SessionServerConfig sessionServerConfig) {
            return new ExecutorManager(sessionServerConfig);
        }

    }

    @Configuration
    public static class SessionTimerTaskConfiguration {

        @Bean
        public SyncClientsHeartbeatTask syncClientsHeartbeatTask() {
            return new SyncClientsHeartbeatTask();
        }

        @Bean
        public SessionCacheDigestTask sessionCacheDigestTask() {
            return new SessionCacheDigestTask();
        }

        @Bean
        public CacheCountTask cacheCountTask() {
            return new CacheCountTask();
        }
    }

    @Configuration
    public static class SessionStrategyConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public SessionRegistryStrategy sessionRegistryStrategy() {
            return new DefaultSessionRegistryStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public SyncConfigHandlerStrategy syncConfigHandlerStrategy() {
            return new DefaultSyncConfigHandlerStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public PublisherHandlerStrategy publisherHandlerStrategy() {
            return new DefaultPublisherHandlerStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public SubscriberHandlerStrategy subscriberHandlerStrategy() {
            return new DefaultSubscriberHandlerStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public WatcherHandlerStrategy watcherHandlerStrategy() {
            return new DefaultWatcherHandlerStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public ReceivedConfigDataPushTaskStrategy receivedConfigDataPushTaskStrategy() {
            return new DefaultReceivedConfigDataPushTaskStrategy();
        }

        @Bean
        public AppRevisionHandlerStrategy appRevisionHandlerStrategy() {
            return new DefaultAppRevisionHandlerStrategy();
        }
    }

    @Configuration
    public static class AccessLimitServiceConfiguration {
        @Bean
        public AccessLimitService accessLimitService(SessionServerConfig sessionServerConfig) {
            return new AccessLimitServiceImpl(sessionServerConfig);
        }
    }

    @Configuration
    public static class SessionFilterConfiguration {

        @Bean
        public IPMatchStrategy ipMatchStrategy() {
            return new DefaultIPMatchStrategy();
        }

        @Bean
        public DataIdMatchStrategy dataIdMatchStrategy() {
            return new DefaultDataIdMatchStrategy();
        }

        @Bean
        @ConditionalOnMissingBean
        public ProcessFilter processFilter() {
            return new BlacklistMatchProcessFilter();
        }

        @Bean
        public BlacklistManager blacklistManager() {
            return new BlacklistManagerImpl();
        }

        @Bean
        public WrapperInterceptorManager wrapperInterceptorManager() {
            return new WrapperInterceptorManager();
        }

        @Bean
        public WrapperInterceptor clientCheckWrapperInterceptor(WrapperInterceptorManager wrapperInterceptorManager) {
            ClientCheckWrapperInterceptor clientCheckWrapperInterceptor = new ClientCheckWrapperInterceptor();
            wrapperInterceptorManager.addInterceptor(clientCheckWrapperInterceptor);
            return clientCheckWrapperInterceptor;
        }

        @Bean
        public WrapperInterceptor blacklistWrapperInterceptor(WrapperInterceptorManager wrapperInterceptorManager) {
            BlacklistWrapperInterceptor blacklistWrapperInterceptor = new BlacklistWrapperInterceptor();
            wrapperInterceptorManager.addInterceptor(blacklistWrapperInterceptor);
            return blacklistWrapperInterceptor;
        }

        @Bean
        public WrapperInterceptor accessLimitWrapperInterceptor(WrapperInterceptorManager wrapperInterceptorManager) {
            AccessLimitWrapperInterceptor accessLimitWrapperInterceptor = new AccessLimitWrapperInterceptor();
            wrapperInterceptorManager.addInterceptor(accessLimitWrapperInterceptor);
            return accessLimitWrapperInterceptor;
        }

    }

    @Configuration
    public static class SessionRenewDatumConfiguration {

        @Bean
        public WriteDataAcceptor writeDataAcceptor() {
            return new WriteDataAcceptorImpl();
        }
    }

    @Configuration
    public static class SessionConnectionsConfiguration {
        @Bean
        public ConnectionsService connectionsService() {
            return new ConnectionsService();
        }
    }

    @Configuration
    public static class SessionProvideDataConfiguration {

        @Bean
        public ProvideDataProcessor provideDataProcessorManager() {
            return new ProvideDataProcessorManager();
        }

        @Bean
        public ProvideDataProcessor blackListProvideDataProcessor(ProvideDataProcessor provideDataProcessorManager) {
            ProvideDataProcessor blackListProvideDataProcessor = new BlackListProvideDataProcessor();
            ((ProvideDataProcessorManager) provideDataProcessorManager)
                .addProvideDataProcessor(blackListProvideDataProcessor);
            return blackListProvideDataProcessor;
        }

        @Bean
        public ProvideDataProcessor stopPushProvideDataProcessor(ProvideDataProcessor provideDataProcessorManager) {
            ProvideDataProcessor stopPushProvideDataProcessor = new StopPushProvideDataProcessor();
            ((ProvideDataProcessorManager) provideDataProcessorManager)
                .addProvideDataProcessor(stopPushProvideDataProcessor);
            return stopPushProvideDataProcessor;
        }
    }
}