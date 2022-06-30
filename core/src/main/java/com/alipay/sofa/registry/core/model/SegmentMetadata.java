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
package com.alipay.sofa.registry.core.model;

import java.util.Set;

/**
 * @author xiaojian.xj
 * @version : SegmentMetadata.java, v 0.1 2022年06月28日 10:26 xiaojian.xj Exp $
 */
public class SegmentMetadata {

  private final boolean localSegment;

  private final String segment;

  private final Set<String> zones;

  public SegmentMetadata(boolean localSegment, String segment, Set<String> zones) {
    this.localSegment = localSegment;
    this.segment = segment;
    this.zones = zones;
  }

  public boolean isLocalSegment() {
    return this.localSegment;
  }

  /**
   * Getter method for property <tt>segment</tt>.
   *
   * @return property value of segment
   */
  public String getSegment() {
    return segment;
  }

  /**
   * Getter method for property <tt>zones</tt>.
   *
   * @return property value of zones
   */
  public Set<String> getZones() {
    return zones;
  }

  @Override
  public String toString() {
    return "SegmentMetadata{"
        + "localSegment="
        + localSegment
        + ", segment='"
        + segment
        + '\''
        + ", zones="
        + zones
        + '}';
  }
}
