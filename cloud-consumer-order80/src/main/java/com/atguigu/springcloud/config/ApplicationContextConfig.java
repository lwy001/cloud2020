package com.atguigu.springcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author 李卫勇
 * @date 2020-07-23 1:24
 */
@Configuration
public class ApplicationContextConfig {


    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }


}
