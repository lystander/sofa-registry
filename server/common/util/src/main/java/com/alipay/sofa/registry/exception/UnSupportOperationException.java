/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.exception;

/**
 *
 * @author xiaojian.xj
 * @version : UnSupportOperationException.java, v 0.1 2022年05月12日 20:57 xiaojian.xj Exp $
 */
public class UnSupportOperationException extends SofaRegistryRuntimeException {

    public UnSupportOperationException(String operation) {
        super("not support operation: " + operation);
    }
}
