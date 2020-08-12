package com.atguigu.springcloud.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 李卫勇
 * @date 2020-08-07 23:29
 */
@Component
@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT")
public interface PaymentFeignHystrixService {

    @GetMapping(value = "/payment/payment/ok/{id}")
    String paymentOk(@PathVariable("id") Integer id);

    @GetMapping("/payment/payment/timeout/{id}")
    String paymentTimeout(@PathVariable("id") Integer id);
}
