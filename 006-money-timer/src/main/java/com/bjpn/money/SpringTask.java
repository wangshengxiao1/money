package com.bjpn.money;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.service.IncomeRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SpringTask {

    @Reference(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
    IncomeRecordService incomeRecordService;


    /**
     * 定时器：生成收益计划
     *          //1、查询状态为1 的产品==List
     *         //2、遍历集合 ，查询该产品的投资记录==》List
     *         //3、遍历集合，投资记录生成收益计划
     *         //4、产品状态改为2
     */
    //生成收益计划
    /*@Scheduled(cron = "0/5 * * * * ?")
    public void generatePlan(){
        System.out.println("-----began----");
        incomeRecordService.generatePlan();
        System.out.println("--------end----------");
    }*/

    //收益返现，监控是否到期，每隔5秒监控一次到期返现
    @Scheduled(cron = "0/5 * * * * ?")
    public void cashBack(){
        System.out.println("-----began----");


        /**
         * 1.查询用户收益记录表，查询收益状态为0且到期的收益记录
         * 2.遍历集合，将本金和利息添加到当前投资人账户余额中
         * 3.将收益状态0未返改为1已返
         */
        incomeRecordService.cashBack();
        System.out.println("--------end----------");

    }
}
