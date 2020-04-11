package com.logback.demo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tobebetter9527
 * @description 用户
 * @create 2020/04/11 22:21
 */
@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
}
