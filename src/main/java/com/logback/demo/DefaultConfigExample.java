package com.logback.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tobebetter9527
 * @description 删掉logback.xml,运行一下代码还是能打印的。
 * @create 2020/04/12 11:19
 */
public class DefaultConfigExample {
    @Test
    public void testDefault() {
        Logger logger = LoggerFactory.getLogger("com.logback");
        logger.debug("This message is logged because debug == debug");
        logger.trace("This message is not logged");
    }
}
