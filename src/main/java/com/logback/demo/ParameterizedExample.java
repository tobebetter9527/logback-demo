package com.logback.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author tobebetter9527
 * @description TODO
 * @create 2020/04/11 22:07
 */
@Slf4j
public class ParameterizedExample {

    @Test
    public void testParameterizedExample() {
        String message = "This is a String";
        Integer zero = 0;
        User user = new User().setId("123").setName("Tim");
        try {
            log.debug("Logging message: {}", message);
            log.debug("Going to divide {} by {}", 42, zero);
            log.debug("user is {}", user);
            int result = 42 / zero;
        } catch (Exception e) {
            log.error("Error dividing {} by {} ", 42, zero, e);
        }
    }


}
