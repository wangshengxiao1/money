package com.bjpn.money.mapper;

import com.bjpn.money.model.LoanInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    //查询年华利率的方法
    Double selectLoanInfoHisRateAvg();

    //根据产品数量和信息查询，还有分页
    List<LoanInfo> selectLoanInfosByTypeAndNum(Map<String, Object> map);


    //根据类型查询所有数据
    Long selectLoanInfosByTypeAndCount(Integer ptype);

    //根据投资金额减少剩余募集金额
    int updateLeftMoneyReduceForInvest(Map<String, Object> parasMap);

    //查询状态为1的产品
    List<LoanInfo> selectLoanInfosByFullStatus();
}