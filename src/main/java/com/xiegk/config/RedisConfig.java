package com.xiegk.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiegk.bean.RedisMaster;
import com.xiegk.bean.RedisSlave;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义相关的主从 template
 *
 * @author xgk
 * @date
 */
@Configuration
@EnableConfigurationProperties({RedisMasterSlavesProperties.class})
public class RedisConfig {

    /**
     * Master主机 template
     *
     * @param properties 配置
     */
    @Bean("redisMasterTemplate")
    public RedisTemplate<String, Object> redisMasterTemplate(RedisMasterSlavesProperties properties) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // master
        RedisMaster master = properties.getRedisMaster();
        // 连接工厂
        RedisConnectionFactory factory = connectionFactory(master.getHost(), master.getPort(), master.getPassword());
        redisTemplate.setConnectionFactory(factory);
        // 序列化
        return serializerTemplate(redisTemplate);
    }

    /**
     * Slaves从机 template
     *
     * @param properties
     * @return
     */
    @Bean("redisSlaveTemplateList")
    public List<RedisTemplate<String, Object>> redisSlaveTemplateList(RedisMasterSlavesProperties properties) {
        List<RedisTemplate<String, Object>> redisTemplates = new ArrayList<>();
        for (RedisSlave slave : properties.getRedisSlaves()) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            // 连接工厂
            RedisConnectionFactory factory = connectionFactory(slave.getHost(), slave.getPort(), slave.getPassword());
            redisTemplate.setConnectionFactory(factory);
            // 序列化并放置集合
            redisTemplates.add(serializerTemplate(redisTemplate));
        }
        return redisTemplates;
    }

    /**
     * 创建连接工厂
     *
     * @param host
     * @param port
     * @param password
     */
    private RedisConnectionFactory connectionFactory(String host, Integer port, String password) {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration(); // 设置标准配置信息
        redisStandaloneConfig.setHostName(host);        // 主机
        redisStandaloneConfig.setPort(port);            // 端口
        if (password != null && !password.equals(""))   // 密码
            redisStandaloneConfig.setPassword(password);

        // 这里自定义创建 LettuceConnectionFactory
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfig);
        factory.afterPropertiesSet(); // 其它参数设置
        return factory;
    }

    /**
     * 序列化配置
     *
     * @param redisTemplate
     * @return
     */
    private RedisTemplate<String, Object> serializerTemplate(RedisTemplate<String, Object> redisTemplate) {
        // Json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        // String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
