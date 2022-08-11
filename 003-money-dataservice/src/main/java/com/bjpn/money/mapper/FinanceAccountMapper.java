package com.bjpn.money.mapper;

import com.bjpn.money.model.FinanceAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    //下拉框查询余额
    FinanceAccount selectByUserId(Integer id);

    //减少账户余额
    int updateMoneyReduceForInvest(Map<String, Object> parasMap);

    //向个人账户中存入返现金额
    int insertMoneyByBid(Integer uid,Double allMoney);
}