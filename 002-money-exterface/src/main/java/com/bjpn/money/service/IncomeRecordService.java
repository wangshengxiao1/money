package com.bjpn.money.service;

/**
 * 收益业务接口
 */
public interface IncomeRecordService {
    /**
     * 定时器：生成收益计划
     *          //1、查询状态为1 的产品==List
     *         //2、遍历集合 ，查询该产品的投资记录==》List
     *         //3、遍历集合，投资记录生成收益计划
     *         //4、产品状态改为2
     */

    void generatePlan();

    /**
     * 收益返现
     * 1.查询用户收益记录表，查询收益状态为0且到期的收益记录
     * 2.遍历集合，将本金和利息添加到当前投资人账户余额中
     * 3.将收益状态0未返改为1已返
     */
    void cashBack();
}
