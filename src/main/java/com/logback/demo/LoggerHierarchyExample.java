package com.logback.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * @author tobebetter9527
 * @description Logger hierarchy example
 * @create 2020/04/11 21:47
 */
public class LoggerHierarchyExample {

    @Test
    public void testLoggerHierarchy() {
        Logger parentLogger = (Logger) LoggerFactory.getLogger("com.logback");
        parentLogger.setLevel(Level.INFO);
        Logger childLogger = (Logger) LoggerFactory.getLogger("com.logback.demo");

        parentLogger.warn("This message is logged because WARN > INFO.");
        parentLogger.debug("This message is not logged because DEBUG < INFO.");
        childLogger.info("INFO == INFO");
        childLogger.debug("DEBUG < INFO");
    }


    @Test
    public void testRootLogger() {
        Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.logback");
        logger.debug("Hi there!");

        Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.debug("This message is logged because DEBUG == DEBUG.");
        rootLogger.setLevel(Level.ERROR);

        logger.warn("This message is not logged because WARN < ERROR.");
        logger.error("This is logged.");
    }
}
