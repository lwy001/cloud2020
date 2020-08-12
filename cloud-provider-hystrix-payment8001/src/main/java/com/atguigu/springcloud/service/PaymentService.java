package com.atguigu.springcloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lwy
 * @Package com.atguigu.springcloud.service
 * @Description:
 * @date 2020/8/12 11:35
 */
@Service
public class PaymentService {

    @Value("${server.port}")
    private String serverPort;

    public String paymentOk(Integer id){

        return "线程池："+Thread.currentThread().getName()+"**********id:"+id+"\t paymentOk"+serverPort;
    }

    @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String paymentTimeout(Integer id){
        int timeout = id;
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池："+Thread.currentThread().getName()+"***********id:"+id+"\t paymentTimeout(s):"+timeout+"\t"+serverPort;
    }

    public String paymentTimeoutHandler(Integer id){
        return "线程池："+Thread.currentThread().getName()+"*错误应急响应***paymentTimeoutHandler,id:"+id+"OTTO \t serverPort:"+serverPort;
    }
}
