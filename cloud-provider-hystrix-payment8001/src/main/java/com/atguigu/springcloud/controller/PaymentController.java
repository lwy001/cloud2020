package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lwy
 * @Package com.atguigu.springcloud.controller
 * @Description:
 * @date 2020/8/12 11:49
 */
@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @GetMapping("/payment/payment/ok/{id}")
    public String paymentOk(@PathVariable("id") Integer id){
        String result = paymentService.paymentOk(id);
        log.info("***********result:"+result);
        return result;
    }

    @GetMapping("/payment/payment/timeout/{id}")
    public String paymentTimeout(@PathVariable("id") Integer id){
        String result = paymentService.paymentTimeout(id);
        log.info("***********result:"+result);
        return result;
    }


}
