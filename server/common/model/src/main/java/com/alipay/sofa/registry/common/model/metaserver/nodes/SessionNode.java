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
package com.alipay.sofa.registry.common.model.metaserver.nodes;

import com.alipay.sofa.registry.common.model.Node;
import com.alipay.sofa.registry.common.model.ProcessId;
import com.alipay.sofa.registry.common.model.store.URL;

/**
 * @author shangyu.wh
 * @version $Id: SessionNodeRegister.java, v 0.1 2018-01-11 16:32 shangyu.wh Exp $
 */
public class SessionNode implements Node {

  private URL nodeUrl;

  private String regionId;

  private String name;

  private ProcessId processId;

  /**
   * constructor
   *
   * @param nodeUrl
   * @param regionId
   */
  public SessionNode(URL nodeUrl, String regionId) {
    this.nodeUrl = nodeUrl;
    this.regionId = regionId;
  }

  @Override
  public NodeType getNodeType() {
    return NodeType.SESSION;
  }

  @Override
  public URL getNodeUrl() {
    return nodeUrl;
  }

  /**
   * get ip address from nodeUrl
   *
   * @return
   */
  public String getIp() {
    return nodeUrl == null ? "" : nodeUrl.getIpAddress();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SessionNode)) {
      return false;
    }

    SessionNode that = (SessionNode) o;

    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }

    if (regionId != null ? !regionId.equals(that.regionId) : that.regionId != null) {
      return false;
    }

    return nodeUrl != null
        ? (nodeUrl.getAddressString() != null
            ? nodeUrl.getAddressString().equals(that.nodeUrl.getAddressString())
            : that.nodeUrl.getAddressString() != null)
        : that.nodeUrl != null;
  }

  public ProcessId getProcessId() {
    return processId;
  }

  public SessionNode setProcessId(ProcessId processId) {
    this.processId = processId;
    return this;
  }

  /**
   * Hash code int.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (regionId != null ? regionId.hashCode() : 0);
    result =
        31 * result
            + (nodeUrl != null
                ? (nodeUrl.getAddressString() != null ? nodeUrl.getAddressString().hashCode() : 0)
                : 0);
    return result;
  }

  /**
   * Setter method for property <tt>nodeUrl</tt>.
   *
   * @param nodeUrl value to be assigned to property nodeUrl
   */
  public void setNodeUrl(URL nodeUrl) {
    this.nodeUrl = nodeUrl;
  }

  /**
   * Getter method for property <tt>regionId</tt>.
   *
   * @return property value of regionId
   */
  public String getRegionId() {
    return regionId;
  }

  /**
   * Setter method for property <tt>regionId</tt>.
   *
   * @param regionId value to be assigned to property regionId
   */
  public void setRegionId(String regionId) {
    this.regionId = regionId;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter method for property <tt>name</tt>.
   *
   * @param name value to be assigned to property name
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SessionNode{");
    sb.append("ip=").append(getIp());
    sb.append('}');
    return sb.toString();
  }
}
