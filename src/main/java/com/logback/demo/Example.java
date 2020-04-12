package com.logback.demo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tobebetter9527
 * @description logback的demo
 * @create 2020/04/11 21:12
 */
@Slf4j
public class Example {

    public static void main(String[] args) {
        log.info("Example log from {}", Example.class.getSimpleName());
    }

}
