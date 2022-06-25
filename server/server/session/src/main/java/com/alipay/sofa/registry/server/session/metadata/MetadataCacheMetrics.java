/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.server.session.metadata;

import io.prometheus.client.Counter;

/**
 * @author xiaojian.xj
 * @version : MetadataCacheMetrics.java, v 0.1 2022年06月24日 19:44 xiaojian.xj Exp $
 */
public class MetadataCacheMetrics {

  static final class Fetch {
    static final Counter FETCH_REVISION_COUNTER =
        Counter.build()
            .namespace("metadata")
            .subsystem("revision")
            .name("fetch_revision_total")
            .help("fetch revision")
            .labelNames("hit")
            .register();
    static final Counter.Child REVISION_CACHE_HIT_COUNTER = FETCH_REVISION_COUNTER.labels("Y");
    static final Counter.Child REVISION_CACHE_MISS_COUNTER = FETCH_REVISION_COUNTER.labels("N");

    static final Counter FETCH_APPS_COUNTER =
        Counter.build()
            .namespace("metadata")
            .subsystem("apps")
            .name("fetch_apps_total")
            .help("query apps")
            .labelNames("hit")
            .register();
    static final Counter.Child APPS_CACHE_HIT_COUNTER = FETCH_APPS_COUNTER.labels("Y");
    static final Counter.Child APPS_CACHE_MISS_COUNTER = FETCH_APPS_COUNTER.labels("N");
  }
}
