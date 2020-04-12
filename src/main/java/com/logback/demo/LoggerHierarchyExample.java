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
        // 祖先logger，未指定级别，它将继承root logger的级别，root logger默认debug
        // SLF4J's的抽象logger是没有实现setLevel方法，这里是用ch.qos.logback.classic.Logger
        Logger ancestorLogger = (Logger) LoggerFactory.getLogger("com");
        // 父logger，指定级别为info，不继承祖先logger的级别
        Logger parentLogger = (Logger) LoggerFactory.getLogger("com.logback");
        parentLogger.setLevel(Level.INFO);
        // 子logger，未指定级别，继承最近的父logger的日志级别，为info。
        Logger childLogger = (Logger) LoggerFactory.getLogger("com.logback.demo");
        // 没有父类，默认继承root logger
        Logger otherLogger = (Logger) LoggerFactory.getLogger("com.other.demo");

        ancestorLogger.debug("This message is logged because debug == debug");
        ancestorLogger.trace("This message is not logged because trace < debug");
        parentLogger.warn("This message is logged because WARN > INFO.");
        parentLogger.debug("This message is not logged because DEBUG < INFO.");
        childLogger.info("INFO == INFO");
        childLogger.debug("DEBUG < INFO");

        otherLogger.debug("This otherLogger message is logged because debug == debug");
    }


    @Test
    public void testRootLogger() {
        // 没有指定level，继承root logger的level为debug
        Logger logger = (Logger) LoggerFactory.getLogger("com.logback");
        logger.debug("Hi there!");

        // root logger,重新设定level为error
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.debug("This message is logged because DEBUG == DEBUG.");
        rootLogger.setLevel(Level.ERROR);

        logger.warn("This message is not logged because WARN < ERROR.");
        logger.error("This is logged.");
    }
}
