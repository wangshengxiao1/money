package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.RechargeRecordMapper;
import com.bjpn.money.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 充值接口实现类
 */
@Service(interfaceClass = RechargeRecordService.class,timeout = 20000,version = "1.0.0")
@Component
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    RechargeRecordMapper rechargeRecordMapper;


    /**
     * 添加充值记录
     * @param rechargeRecord
     * @return
     */
    @Override
    public int recharge(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    /**
     * 添加失败修改状态为2
     * @param out_trade_no
     */
    @Override
    public int modifyStatusByNo(String out_trade_no) {
        int num = rechargeRecordMapper.updateStatusByNo(out_trade_no);
        return num;
    }


    /**
     * 根据订单号查询订单是否存在
     * @param out_trade_no
     * @return
     */
    @Override
    public RechargeRecord queryRechargeByNo(String out_trade_no) {
        return rechargeRecordMapper.selectRechargeByNo(out_trade_no);
    }
}
