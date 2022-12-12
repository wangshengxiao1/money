package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.RechargeRecordMapper;
import com.bjpn.money.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * 充值接口实现类
 */
@Service(interfaceClass = RechargeRecordService.class,timeout = 20000,version = "1.0.0")
@Component
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;


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

    /**
     * 根据用户id查询充值记录表
     * @param uid
     * @return
     */
    @Override
    public List<RechargeRecord> queryRechargeRecordByUid(Integer uid) {
        return rechargeRecordMapper.selectRechargeRecordByUid(uid);
    }


    /**
     * 支付交易成功,修改状态为1，将余额修改
     * @param rechargeRecord
     * @return
     */
    @Override
    @Transactional
    public int rechargeSuccess(RechargeRecord rechargeRecord) {
        //修改状态为1
        int num = rechargeRecordMapper.updateStatusByRechargeRecord(rechargeRecord);
        if (num!=1){
            //未修改成功手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return num;
        }
        //充值成功更改余额,余额表中添加一个version(先不添加，添加字段xml文件中映射参数都要改)
        num = financeAccountMapper.insertMoneyByBid(rechargeRecord.getUid(),rechargeRecord.getRechargeMoney());
        if (num!=1){
            //未修改成功手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return num;
        }
        return num;
    }

    /**
     * 查询充值订单状态为0的充值订单
     * @return
     */
    @Override
    public List<RechargeRecord> queryRechargeRecordsByZero() {

        return rechargeRecordMapper.selectRechargeRecordsByZero();
    }
}
