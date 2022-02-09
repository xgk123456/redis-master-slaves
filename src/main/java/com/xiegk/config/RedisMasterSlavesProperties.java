package com.xiegk.config;

import com.xiegk.bean.RedisMaster;
import com.xiegk.bean.RedisSlave;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author xgk
 * @date
 */
@ConfigurationProperties(prefix = "redis.ms")
@Data
public class RedisMasterSlavesProperties {
    // 主机
    private RedisMaster redisMaster;
    // 从机
    private List<RedisSlave> redisSlaves;
}
