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

    /**
     * 根据用户ID内联查询投资信息
     * @param uid
     * @return
     */
    List<BidInfo> queryBidInfoByUid(Integer uid);

    /**
     * 查询投资总条数
     * @param uid
     * @return
     */
    Long queryCountBidInfoByUid(Integer uid);

    /**
     * 分页查询投资记录
     * @param uid
     * @param pageModel
     * @return
     */
    List<BidInfo> queryBidInfoByUidAndPage(Integer uid, PageModel pageModel);
}
