package com.bjpn.money.mapper;

import com.bjpn.money.model.BidInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    //过去累计成交额
    Double selectBidInfoMoneySum();

    //通过产品id查询该产品投资记录
    List<BidInfo> selectBidInfosByLoanId(Map map);

    //通过产品id查询产品投资记录，不带分页
    List<BidInfo> selectBidInfoListByLoanId(Integer id);
}