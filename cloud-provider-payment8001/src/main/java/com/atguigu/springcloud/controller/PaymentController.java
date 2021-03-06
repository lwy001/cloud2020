package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 李卫勇
 * @date 2020-07-09 22:35
 */
@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create(
            @RequestBody Payment payment
    ){
        int resoult = paymentService.create(payment);
        log.info("******插入结果："+resoult);
        if (resoult > 0){
            return new CommonResult(200,"插入数据库成功,serverPort:"+serverPort,resoult);
        }else {
            return new CommonResult(444,"插入数据库失败,serverPort:"+serverPort,null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(
            @PathVariable("id") Long id
    ){
        Payment payment = paymentService.getPaymentById(id);
        log.info("***2222***查询结果："+payment.toString());
        if (payment!=null){
            return new CommonResult(200,"查询成功,serverPort:"+serverPort,payment);
        }else {
            return new CommonResult(444,"没有对应记录，查询ID"+id,null);
        }
    }


    @GetMapping("/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("*************"+service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info("##################"+instance.getServiceId()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;
    }


    @GetMapping("/payment/feign/timeout")
    public String paymentFeignTimeout(){
        log.info("/payment/feign/timeout");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.info("错误："+e.getMessage(),e);
        }
        return serverPort;
    }
}
