package com.xiegk.bean;

import lombok.Data;

/**
 * @author xgk
 * @date
 */
@Data
public class RedisMaster {
    private String host;
    private Integer port;
    private String password;
    // 其它参数...
}
