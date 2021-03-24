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
package com.alipay.sofa.registry.server.meta.bootstrap;

import com.alipay.sofa.registry.common.model.store.URL;
import com.alipay.sofa.registry.log.Logger;
import com.alipay.sofa.registry.log.LoggerFactory;
import com.alipay.sofa.registry.metrics.ReporterUtils;
import com.alipay.sofa.registry.net.NetUtil;
import com.alipay.sofa.registry.remoting.ChannelHandler;
import com.alipay.sofa.registry.remoting.Server;
import com.alipay.sofa.registry.remoting.bolt.exchange.BoltExchange;
import com.alipay.sofa.registry.remoting.jersey.exchange.JerseyExchange;
import com.alipay.sofa.registry.server.meta.bootstrap.config.MetaServerConfig;
import com.alipay.sofa.registry.server.shared.remoting.AbstractServerHandler;
import com.alipay.sofa.registry.store.api.elector.AbstractLeaderElector;
import com.alipay.sofa.registry.store.api.elector.LeaderElector;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Resource;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author shangyu.wh
 * @version $Id: MetaServerBootstrap.java, v 0.1 2018-01-16 11:28 shangyu.wh Exp $
 */
public class MetaServerBootstrap {

  private static final Logger LOGGER = LoggerFactory.getLogger(MetaServerBootstrap.class);

  @Autowired private MetaServerConfig metaServerConfig;

  @Autowired private BoltExchange boltExchange;

  @Autowired private JerseyExchange jerseyExchange;

  @Resource(name = "sessionServerHandlers")
  private Collection<AbstractServerHandler> sessionServerHandlers;

  @Resource(name = "dataServerHandlers")
  private Collection<AbstractServerHandler> dataServerHandlers;

  @Resource(name = "metaServerHandlers")
  private Collection<AbstractServerHandler> metaServerHandlers;

  @Autowired private ResourceConfig jerseyResourceConfig;

  @Autowired private ApplicationContext applicationContext;

  @Autowired private LeaderElector leaderElector;

  private Server sessionServer;

  private Server dataServer;

  private Server metaServer;

  private Server httpServer;

  private final AtomicBoolean sessionStart = new AtomicBoolean(false);

  private final AtomicBoolean dataStart = new AtomicBoolean(false);

  private final AtomicBoolean metaStart = new AtomicBoolean(false);

  private final AtomicBoolean httpStart = new AtomicBoolean(false);

  private final Retryer<Boolean> retryer =
      RetryerBuilder.<Boolean>newBuilder()
          .retryIfException()
          .retryIfResult(
              new Predicate<Boolean>() {
                @Override
                public boolean apply(Boolean input) {
                  return !input;
                }
              })
          .withWaitStrategy(WaitStrategies.exponentialWait(1000, 10000, TimeUnit.MILLISECONDS))
          .withStopStrategy(StopStrategies.stopAfterAttempt(10))
          .build();
  /** Do initialized. */
  public void start() {
    try {
      LOGGER.info("the configuration items are as follows: " + metaServerConfig.toString());
      ReporterUtils.enablePrometheusDefaultExports();

      openSessionRegisterServer();

      openDataRegisterServer();

      openMetaRegisterServer();

      openHttpServer();

      retryer.call(
          () -> {
            return !StringUtils.isEmpty(leaderElector.getLeader())
                && leaderElector.getLeaderEpoch() != AbstractLeaderElector.LeaderInfo.initEpoch;
          });
      LOGGER.warn(
          "[MetaBootstrap] leader info: {}, [{}]",
          leaderElector.getLeader(),
          leaderElector.getLeaderEpoch());
      Runtime.getRuntime().addShutdownHook(new Thread(this::doStop));
    } catch (Throwable e) {
      LOGGER.error("Bootstrap Meta Server got error!", e);
      throw new RuntimeException("Bootstrap Meta Server got error!", e);
    }
  }

  public void destroy() {
    doStop();
  }

  private void doStop() {
    try {
      LOGGER.info("{} Shutting down Meta Server..", new Date().toString());

      stopServer();

    } catch (Throwable e) {
      LOGGER.error("Shutting down Meta Server error!", e);
    }
    LOGGER.info("{} Meta server is now shutdown...", new Date().toString());
  }

  private void openSessionRegisterServer() {
    try {
      if (sessionStart.compareAndSet(false, true)) {
        sessionServer =
            boltExchange.open(
                new URL(
                    NetUtil.getLocalAddress().getHostAddress(),
                    metaServerConfig.getSessionServerPort()),
                sessionServerHandlers.toArray(new ChannelHandler[sessionServerHandlers.size()]));

        LOGGER.info(
            "Open session node register server port {} success!",
            metaServerConfig.getSessionServerPort());
      }
    } catch (Exception e) {
      sessionStart.set(false);
      LOGGER.error(
          "Open session node register server port {} error!",
          metaServerConfig.getSessionServerPort(),
          e);
      throw new RuntimeException("Open session node register server error!", e);
    }
  }

  private void openDataRegisterServer() {
    try {
      if (dataStart.compareAndSet(false, true)) {
        dataServer =
            boltExchange.open(
                new URL(
                    NetUtil.getLocalAddress().getHostAddress(),
                    metaServerConfig.getDataServerPort()),
                dataServerHandlers.toArray(new ChannelHandler[dataServerHandlers.size()]));

        LOGGER.info(
            "Open data node register server port {} success!",
            metaServerConfig.getDataServerPort());
      }
    } catch (Exception e) {
      dataStart.set(false);
      LOGGER.error(
          "Open data node register server port {} error!", metaServerConfig.getDataServerPort(), e);
      throw new RuntimeException("Open data node register server error!", e);
    }
  }

  private void openMetaRegisterServer() {
    try {
      if (metaStart.compareAndSet(false, true)) {
        metaServer =
            boltExchange.open(
                new URL(
                    NetUtil.getLocalAddress().getHostAddress(),
                    metaServerConfig.getMetaServerPort()),
                metaServerHandlers.toArray(new ChannelHandler[metaServerHandlers.size()]));

        LOGGER.info("Open meta server port {} success!", metaServerConfig.getMetaServerPort());
      }
    } catch (Exception e) {
      metaStart.set(false);
      LOGGER.error("Open meta server port {} error!", metaServerConfig.getMetaServerPort(), e);
      throw new RuntimeException("Open meta server error!", e);
    }
  }

  private void openHttpServer() {
    try {
      if (httpStart.compareAndSet(false, true)) {
        bindResourceConfig();
        httpServer =
            jerseyExchange.open(
                new URL(
                    NetUtil.getLocalAddress().getHostAddress(),
                    metaServerConfig.getHttpServerPort()),
                new ResourceConfig[] {jerseyResourceConfig});
        LOGGER.info("Open http server port {} success!", metaServerConfig.getHttpServerPort());
      }
    } catch (Exception e) {
      httpStart.set(false);
      LOGGER.error("Open http server port {} error!", metaServerConfig.getHttpServerPort(), e);
      throw new RuntimeException("Open http server error!", e);
    }
  }

  private void bindResourceConfig() {
    registerInstances(Path.class);
    registerInstances(Provider.class);
  }

  private void registerInstances(Class<? extends Annotation> annotationType) {
    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(annotationType);
    if (beans != null && beans.size() > 0) {
      beans.forEach((beanName, bean) -> jerseyResourceConfig.registerInstances(bean));
    }
  }

  private void stopServer() {
    if (sessionServer != null && sessionServer.isOpen()) {
      sessionServer.close();
    }
    if (dataServer != null && dataServer.isOpen()) {
      dataServer.close();
    }
    if (metaServer != null && metaServer.isOpen()) {
      metaServer.close();
    }
    if (httpServer != null && httpServer.isOpen()) {
      httpServer.close();
    }
  }

  /**
   * Getter method for property <tt>sessionStart</tt>.
   *
   * @return property value of sessionStart
   */
  public boolean getSessionStart() {
    return sessionStart.get();
  }

  /**
   * Getter method for property <tt>dataStart</tt>.
   *
   * @return property value of dataStart
   */
  public boolean getDataStart() {
    return dataStart.get();
  }

  /**
   * Getter method for property <tt>metaStart</tt>.
   *
   * @return property value of metaStart
   */
  public boolean getMetaStart() {
    return metaStart.get();
  }

  /**
   * Getter method for property <tt>httpStart</tt>.
   *
   * @return property value of httpStart
   */
  public boolean getHttpStart() {
    return httpStart.get();
  }
}
