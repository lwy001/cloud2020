package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentFeignHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 李卫勇
 * @date 2020-08-07 23:43
 */
@RestController
@Slf4j
public class PaymentFeignHystrixController {

    @Resource
    private PaymentFeignHystrixService paymentFeignService;

    @GetMapping(value = "/consumer/payment/payment/ok/{id}")
    public String getPaymentById(@PathVariable("id") Integer id){

        String result = paymentFeignService.paymentOk(id);
        log.info("****getPaymentById******refult:"+result);
        return result;
    }

    @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "8000")
    })
    @GetMapping("/consumer/payment/payment/timeout/{id}")
    public String paymentFeignTimeout(@PathVariable("id") Integer id){
        String result = paymentFeignService.paymentTimeout(id);
        log.info("*****paymentFeignTimeout*****refult:"+result);
        return result;
    }

    public String paymentTimeoutHandler(Integer id){
        return "线程池："+Thread.currentThread().getName()+"*错误应急响应***paymentTimeoutHandler,id:"+id+"\t OTTO  80";
    }
}
