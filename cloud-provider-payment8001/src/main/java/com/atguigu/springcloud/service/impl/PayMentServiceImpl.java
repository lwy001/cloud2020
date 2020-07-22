package com.atguigu.springcloud.service.impl;

import com.atguigu.springcloud.mapper.PaymentMapper;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 李卫勇
 * @date 2020-07-09 22:34
 */
@Service
public class PayMentServiceImpl implements PaymentService {

    @Resource
    private PaymentMapper paymentDao;

    @Override
    public int create(Payment payment) {
        return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}
