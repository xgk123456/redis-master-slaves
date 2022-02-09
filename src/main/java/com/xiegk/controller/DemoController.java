package com.xiegk.controller;

import com.xiegk.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * demo 控制层
 *
 * @author xgk
 * @date
 */
@RestController
@RequestMapping("/demos")
public class DemoController {

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/{content}")
    public String masterSlaveDemo(@PathVariable("content") String content) {
        String mykey = "master:slave:demo";
        // 主机写
        boolean setRes = redisUtils.set(mykey, content);
        // 从机读
        String getContent = redisUtils.get(mykey).toString();
        return "设置状态：" + setRes + ", 读取到的内容：" + getContent;
    }

}
