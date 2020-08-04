package com.atguigu.springcloud.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @author 李卫勇
 * @date 2020-07-10 1:03
 */
@Component
@EnableScheduling
public class Job {

    /**
     * 不支持集群
     */
//    @Scheduled(cron = "* */5 * * * *")
    public void dosome(){
        System.out.println("***********Job***********");
    }
}
