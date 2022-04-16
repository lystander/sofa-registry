/** Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved. */
package com.alipay.sofa.registry.jdbc.version.config;

import com.alipay.sofa.registry.log.Logger;

/**
 * @author xiaojian.xj
 * @version : ConfigRepository.java, v 0.1 2022年04月15日 14:41 xiaojian.xj Exp $
 */
public abstract class BaseConfigRepository<T extends ConfigEntry> {

  private final String name;

  private final Logger logger;

  public BaseConfigRepository(String name, Logger logger) {
    this.name = name;
    this.logger = logger;
  }

  public boolean put(T entry) {
    if (entry == null) {
      logger.error("name: {} update config entry is null.", name);
      return false;
    }

    try {
      T exist = queryExistVersion(entry);
      if (exist == null) {
        // it should throw duplicate key exception when parallel invocation
        insert(entry);
        return true;
      }
      return put(entry, exist.getDataVersion());
    } catch (Throwable t) {
      logger.error("name: {} update config entry:{} error.", name, entry, t);
      return false;
    }

  }

  public boolean put(T entry, long expectVersion) {
    if (entry == null) {
      logger.error("name: {} update config entry is null.", name);
      return false;
    }
    try {
      if (entry.getDataVersion() <= expectVersion) {
        logger.error("update config entry fail, update.version:{} <= expectVersion:{}",
                entry.getDataVersion(), expectVersion);
        return false;
      }

      int affect = updateWithExpectVersion(entry, expectVersion);
      if (affect == 0) {
        logger.error("update config entry fail, affect=0, entry:{}", entry);
        return false;
      }
      return true;
    } catch (Throwable t) {
      logger.error("name: {} update config entry:{} error.", name, entry, t);
      return false;
    }
  }

  protected abstract T queryExistVersion(T entry);

  protected abstract long insert(T entry);

  protected abstract int updateWithExpectVersion(T entry, long exist);

}
