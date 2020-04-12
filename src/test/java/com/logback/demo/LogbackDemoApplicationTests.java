package com.logback.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest
class LogbackDemoApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    public void test(){
        log.info("Example log from {}", Example.class.getSimpleName());
    }

}
