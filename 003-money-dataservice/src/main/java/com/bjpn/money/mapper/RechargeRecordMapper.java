package com.bjpn.money.mapper;

import com.bjpn.money.model.RechargeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

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


    //根据用户id查询充值记录表
    List<RechargeRecord> selectRechargeRecordByUid(Integer uid);

    //根据充值对象修改订单状态
    int updateStatusByRechargeRecord(RechargeRecord rechargeRecord);

    //查询充值订单状态为0的充值订单
    List<RechargeRecord> selectRechargeRecordsByZero();
}