package com.atguigu.springcloud.redis.controller;

import com.atguigu.springcloud.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李卫勇
 * @date 2020-07-20 22:54
 */
@RestController
public class StudentHandler {

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @PostMapping("/set")
//    public void set(
//            @RequestBody Student student
//            ){
//        redisTemplate.opsForValue().set("student", student);
//        Object student1 = redisTemplate.opsForValue().get("student");
//        System.out.println(student1);
//    }


    @PostMapping("/strTest")
    public void strTest(@RequestParam String value){

//        RedisUtil redisUtil = new RedisUtil();
//        System.out.println("=============="+redisTemplate.opsForValue().get("student"));
        System.out.println("**************"+redisUtil.getKey("student"));
//        redisTemplate.delete("student");
        System.out.println(redisUtil.getKey("student"));
        redisUtil.setKey("value", value);
        System.out.println(redisUtil.getKey("value", String.class));
    }


    @GetMapping("/hash")
    public void hash() throws Exception{
        Map map = new ConcurrentHashMap<String, Long>();
        int i = 0;
        i++;
        Long time = new Date().getTime();
        map.put(i+"", time);
        redisUtil.setHash("hash1",i+"",time);
        Thread.sleep(1000);
        i++;
        time = new Date().getTime();
        map.put(i+"", time);
        redisUtil.setHash("hash1",i+"",time);
        Thread.sleep(1000);
        i++;
        time = new Date().getTime();
        map.put(i+"", time);
        redisUtil.setHash("hash1",i+"",time);
        Thread.sleep(1000);
        i++;
        time = new Date().getTime();
        map.put(i+"", time);
        redisUtil.setHash("hash1",i+"",time);
        Thread.sleep(1000);
        redisUtil.setHash("hash2","",map);
        List<Object> hash1 = redisUtil.getAllHash("hash1");
        System.out.println("*********************"+hash1);
        List<Object> hash2 = redisUtil.getAllHash("hash2");
        System.out.println("====================="+hash2);
    }
}
