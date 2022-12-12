package com.bjpn.money;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.service.IncomeRecordService;
import com.bjpn.money.service.RechargeRecordService;
import com.bjpn.money.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpringTask {

    @Reference(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
    IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = RechargeRecordService.class,timeout = 20000,version = "1.0.0")
    RechargeRecordService rechargeRecordService;


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

    //处理订单
    @Scheduled(cron = "0/5 * * * * ?")
    public void doRechargeRecord() throws Exception {
        System.out.println("---------began-------");
        //查询所有订单充值状态为0的订单
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRechargeRecordsByZero();
        //遍历订单根据订单号从支付宝访问订单信息(查询接口)
        for (RechargeRecord rechargeRecord:rechargeRecordList) {
            String out_trade_no = rechargeRecord.getRechargeNo();
            String result = HttpClientUtils.doGet("http://localhost:9007/007-money-alipay/loan/page/queryOrder?out_trade_no=" + out_trade_no);
            //判断返回值
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject alipay = jsonObject.getJSONObject("alipay_trade_query_response");
            String code = alipay.getString("code");
            //判断code为正确的时候再往下走
            if (StringUtils.equals(code,"10000")){
                //code为正确的时候判断支付状态
                String status = alipay.getString("trade_status");
                if (StringUtils.equals("WAIT_BUYER_PAY",status)){
                    //倒计时5分钟，提醒用户尽快支付，根据请求参数中的订单绝对超时时间time_expire，当前时间减去支付时间判断订单剩余支付时间
                    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
                    //当前时间
                    String format1 = simpleDateFormat.format(new Date());
                    //订单生成时间
                    String format2 = simpleDateFormat.format(rechargeRecord.getRechargeTime());

                    Map<String, String> map = new HashMap<>();
                    map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
                    map.put("tos", "13370077805");
                    map.put("msg", "您好,您有一笔未支付订单，请在订单失效前支付(若已支付请忽略)");
                    String doGet = HttpClientUtils.doGet("https://way.jd.com/jixintong/SMSmarketing", map);
                }
                if (StringUtils.equals("TRADE_CLOSED",status)){
                    //支付失败，修改订单状态为2
                    int i = rechargeRecordService.modifyStatusByNo(rechargeRecord.getRechargeNo());

                }
                if (StringUtils.equals("TRADE_SUCCESS",status)){
                    //支付成功，修改订单状态为1，更新余额
                    int i = rechargeRecordService.rechargeSuccess(rechargeRecord);
                    if (i==1){
                        //向客户发送短信提示充值成功，营销短信
                        Map<String, String> map = new HashMap<>();
                        map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
                        map.put("tos", "13370077805");
                        map.put("msg", "您好,您的充值金额将在24小时之内到账");
                        String doGet = HttpClientUtils.doGet("https://way.jd.com/jixintong/SMSmarketing", map);
                    }

                }
            }

        }

        System.out.println("------end-------");

    }
}
