package com.bjpn.money.service;

import com.bjpn.money.model.BidInfo;
import com.bjpn.money.util.PageModel;

import java.util.List;
import java.util.Map;

/**
 * 产品投标数据接口
 */
public interface BidInfoService {
    /**
     * 产品投标总成交额
     * @return
     */
    Double queryBidInfoMoneySum();

    /**
     * 通过产品id查询投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfosByLoanId(Integer loanId, PageModel pageModel);

    /**
     * 投资方法
     * @param parasMap
     * @return
     */
    String invest(Map<String, Object> parasMap);
}
