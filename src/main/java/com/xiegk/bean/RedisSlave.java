package com.xiegk.bean;

import lombok.Data;

/**
 * @author xgk
 * @date
 */
@Data
public class RedisSlave {
    private String host;
    private Integer port;
    private String password;
    // 其它参数...
}
