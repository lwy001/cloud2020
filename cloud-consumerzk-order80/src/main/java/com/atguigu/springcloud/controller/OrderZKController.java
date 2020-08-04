package com.atguigu.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author 李卫勇
 * @date 2020-08-04 0:35
 */
@RestController
@Slf4j
public class OrderZKController {

    public static final String INVOKE_URL = "http://cloud-provider-payment";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumerZK/payment/zk")
    public String getPayment(){
        log.info("************************");
        String str = restTemplate.getForObject(INVOKE_URL+"/payment/zk", String.class);
        log.info("str:"+str);
        return str;
    }
}
