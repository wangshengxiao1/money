package com.bjpn.money;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDubboConfiguration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600*24)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



}
