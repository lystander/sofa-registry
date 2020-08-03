/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.alipay.sofa.registry.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author xiaojian.xj
 * @version $Id: SchedulerCornUtilTest.java, v 0.1 2020年08月03日 19:58 xiaojian.xj Exp $
 */
public class SchedulerCornUtilTest {

    /**
     * execute at 05:10, 15:10, 25:10, 35:10, 45:10, 55:10
     */
    public final static String CACHE_PRINTER_CRON = "10 5,15,25,35,45,55 * * * ?";

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.CHINA);
    @Test
    public void testCorn() {
        Date trigger1 = null;
        try {
            trigger1 = SchedulerCornUtil.nextTrigger(formatter.parse("2020-08-03 20:15:00"), CACHE_PRINTER_CRON);
            Assert.assertEquals("calculate next trigger error.", "2020-08-03 20:15:10", formatter.format(trigger1));

            trigger1 = SchedulerCornUtil.nextTrigger(formatter.parse("2020-08-03 20:15:20"), CACHE_PRINTER_CRON);
            Assert.assertEquals("calculate next trigger error.", "2020-08-03 20:25:10", formatter.format(trigger1));

            trigger1 = SchedulerCornUtil.nextTrigger(formatter.parse("2020-08-03 20:30:00"), CACHE_PRINTER_CRON);
            Assert.assertEquals("calculate next trigger error.", "2020-08-03 20:35:10", formatter.format(trigger1));

            SchedulerCornUtil.nextTrigger(CACHE_PRINTER_CRON);

            long initialDlay = SchedulerCornUtil.calculateInitialDelay(CACHE_PRINTER_CRON);
            System.out.println(initialDlay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}