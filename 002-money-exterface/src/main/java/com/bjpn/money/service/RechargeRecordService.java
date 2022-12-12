package com.bjpn.money.service;

import com.bjpn.money.model.RechargeRecord;

import java.util.List;

/**
 * 充值接口
 */
public interface RechargeRecordService {
    /**
     * 充值添加充值记录
     * @param rechargeRecord
     * @return
     */
    int recharge(RechargeRecord rechargeRecord);

    /**
     * 充值失败修改状态为2
     */
    int modifyStatusByNo(String out_trade_no);


    /**
     * 根据订单号查询订单是否存在
     * @param out_trade_no
     * @return
     */
    RechargeRecord queryRechargeByNo(String out_trade_no);

    /**
     * 根据用户id查询充值记录表
     * @param uid
     * @return
     */
    List<RechargeRecord> queryRechargeRecordByUid(Integer uid);

    /**
     * 支付交易成功
     * @param rechargeRecord
     * @return
     */
    int rechargeSuccess(RechargeRecord rechargeRecord);

    /**
     * 查询充值订单状态为0的订单
     * @return
     */
    List<RechargeRecord> queryRechargeRecordsByZero();
}
