/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.registry.server.shared.constant;

/**
 *
 * @author xiaojian.xj
 * @version : ExchangerModeEnum.java, v 0.1 2022年04月16日 17:49 xiaojian.xj Exp $
 */
public enum ExchangerModeEnum {

    LOCAL_DATA_CENTER("LOCAL_DATA_CENTER"),
    REMOTE_DATA_CENTER("REMOTE_DATA_CENTER"),
    ;

    private String code;

    ExchangerModeEnum(String code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }
}
