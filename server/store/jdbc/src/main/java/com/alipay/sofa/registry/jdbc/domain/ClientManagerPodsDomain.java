/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.sofa.registry.jdbc.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author xiaojian.xj
 * @version $Id: ClientManagerPodsDomain.java, v 0.1 2021年04月27日 22:37 xiaojian.xj Exp $
 */
public class ClientManagerPodsDomain {

    /** primary key */
    private long id;

    /** local data center */
    private String dataCenter;

    /** local data center */
    private String address;

    /** CLIENT_OFF/CLIENT_OPEN  */
    private String operation;

    /** create time */
    private Date gmtCreate;

    /** last update time */
    private Timestamp gmtModify;

    public ClientManagerPodsDomain() {
    }

    /**
     * Getter method for property <tt>id</tt>.
     *
     * @return property value of id
     */
    public long getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     *
     * @param id value to be assigned to property id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>dataCenter</tt>.
     *
     * @return property value of dataCenter
     */
    public String getDataCenter() {
        return dataCenter;
    }

    /**
     * Setter method for property <tt>dataCenter</tt>.
     *
     * @param dataCenter value to be assigned to property dataCenter
     */
    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    /**
     * Getter method for property <tt>address</tt>.
     *
     * @return property value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter method for property <tt>address</tt>.
     *
     * @param address value to be assigned to property address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Getter method for property <tt>operation</tt>.
     *
     * @return property value of operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Setter method for property <tt>operation</tt>.
     *
     * @param operation value to be assigned to property operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Getter method for property <tt>gmtCreate</tt>.
     *
     * @return property value of gmtCreate
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * Setter method for property <tt>gmtCreate</tt>.
     *
     * @param gmtCreate value to be assigned to property gmtCreate
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * Getter method for property <tt>gmtModify</tt>.
     *
     * @return property value of gmtModify
     */
    public Timestamp getGmtModify() {
        return gmtModify;
    }

    /**
     * Setter method for property <tt>gmtModify</tt>.
     *
     * @param gmtModify value to be assigned to property gmtModify
     */
    public void setGmtModify(Timestamp gmtModify) {
        this.gmtModify = gmtModify;
    }
}