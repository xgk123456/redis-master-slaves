package com.xiegk.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 主机Master 只进行写操作, 从机Slave 只进行读操作
 *
 * @author xgk
 * @date
 */
@Component
public class RedisUtils {
    // 主机
    @Autowired
    @Qualifier("redisMasterTemplate")
    public RedisTemplate<String, Object> redisMasterTemplate;
    // 从机
    @Autowired
    @Qualifier("redisSlaveTemplateList")
    public List<RedisTemplate<String, Object>> redisSlaveTemplateList;

    private int slaveIndex = 0;

    // 选择用那台从机(通过取余方式决定)
    private int buildSlaveIndex() {
        this.slaveIndex++;
        return this.slaveIndex % 2;
    }

    // 定义的写操作方法 ------------------------------------------
    public boolean set(String key, Object value) {
        try {
            redisMasterTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 定义的读操作方法 ------------------------------------------
    public Object get(String key) {
        return key == null ? null : redisSlaveTemplateList.get(buildSlaveIndex()).opsForValue().get(key);
    }

}
