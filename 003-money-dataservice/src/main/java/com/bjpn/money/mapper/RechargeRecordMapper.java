package com.bjpn.money.mapper;

import com.bjpn.money.model.RechargeRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);


    //充值失败修改状态为2
    int updateStatusByNo(String tradeNo);


    //根据订单号查询订单是否存在
    RechargeRecord selectRechargeByNo(String tradeNo);

}