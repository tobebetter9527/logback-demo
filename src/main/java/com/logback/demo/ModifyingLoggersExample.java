package com.logback.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tobebetter9527
 * @description 修改logger的level
 * @create 2020/04/12 15:37
 */
public class ModifyingLoggersExample {
    @Test
    public void testModify() {
        Logger otherLogger = LoggerFactory.getLogger("com.logback.other");
        Logger demoLogger = LoggerFactory.getLogger("com.logback.demo");
        Logger testsLogger = LoggerFactory.getLogger("com.logback.demo.tests");

        otherLogger.debug("otherLogger is logged debug == debug");
        demoLogger.debug("demoLogger is not logged debug < info");
        demoLogger.info("demoLogger is logged info == info");
        testsLogger.info("testsLogger is not logged warn > info");
        testsLogger.warn("testsLogger is logged warn = warn");
    }
}
