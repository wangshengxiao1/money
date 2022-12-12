package com.bjpn.money.mapper;

import com.bjpn.money.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    //根据时间和状态码查询需要返现的投资记录
    List<IncomeRecord> queryIncomeRecordByTimeAndStatu();


    //根据用户id查询投资记录收益表按时间降序
    List<IncomeRecord> selectIncomeRecordByUid(Integer uid);

    //根据投资金额排序查询投资记录表，排行榜
    List<IncomeRecord> selectByMoney();
}