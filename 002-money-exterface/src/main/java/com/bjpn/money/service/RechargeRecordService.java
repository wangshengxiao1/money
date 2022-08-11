package com.bjpn.money.service;

import com.bjpn.money.model.RechargeRecord;

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
}
