package com.bjpn.money;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan(basePackages = "com.bjpn.money.mapper")
@EnableTransactionManagement//开启事务
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
