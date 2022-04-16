/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.shared.meta;

import com.alipay.sofa.registry.common.model.elector.LeaderInfo;
import com.alipay.sofa.registry.remoting.exchange.RequestException;
import com.alipay.sofa.registry.remoting.exchange.message.Response;

/**
 *
 * @author xiaojian.xj
 * @version : MetaLeaderExchanger.java, v 0.1 2022年04月16日 15:54 xiaojian.xj Exp $
 */
public interface MetaLeaderExchanger {

    /**
     * send request to remote cluster meta leader
     * @param dataCenter
     * @param requestBody
     * @return
     * @throws RequestException
     */
    Response sendRequest(String dataCenter, Object requestBody) throws RequestException;

    /**
     * learn leader from remote resp
     * @param dataCenter
     * @param leaderInfo
     */
    boolean learn(String dataCenter, LeaderInfo leaderInfo);

    /**
     * reset leader from remoteMetaDomain
     * @param dataCenter
     */
    LeaderInfo resetLeader(String dataCenter);

    /**
     * get leader info
     * @param dataCenter
     * @return
     */
    LeaderInfo getLeader(String dataCenter);

    /**
     * remove leader
     * @param dataCenter
     */
    void removeLeader(String dataCenter);

}
