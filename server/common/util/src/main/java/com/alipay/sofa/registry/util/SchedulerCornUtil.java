/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.registry.util;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;

/**
 *
 * @author xiaojian.xj
 * @version $Id: SchedulerCornUtil.java, v 0.1 2020年08月03日 17:39 xiaojian.xj Exp $
 */
public class SchedulerCornUtil {

    public static long calculateInitialDelay(String corn) {
        Date current = new Date();
        Date nextTrigger = nextTrigger(current, corn);
        return nextTrigger.getTime() - current.getTime();
    }

    /**
     * calculate next trigger time
     * @param corn
     * @return
     */
    public static Date nextTrigger(String corn) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(corn);
        Date nextTriggerTime = cronSequenceGenerator.next(new Date());

        return nextTriggerTime;
    }

    public static Date nextTrigger(Date date, String corn) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(corn);
        Date nextTriggerTime = cronSequenceGenerator.next(date);

        return nextTriggerTime;
    }
}