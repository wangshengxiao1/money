package com.bjpn.money.service;

import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.PageModel;

import java.util.List;
import java.util.Map;

/**
 * 产品业务接口
 */
public interface LoanInfoService {
    /**
     * 查询年化利率
     * @return
     */
    Double queryLoanInfoHisRateAvg();

    /**
     * 根据产品数量和信息查询
     * @param map
     * @return
     */
    List<LoanInfo> queryLoanInfosByTypeAndNum(Map<String, Object> map);


    /**
     * 分页查询的方法接口,按类型查
     * @param pageModel
     * @return
     */
    List<LoanInfo> queryLoanInfosByTypeAndModel(Integer ptype,PageModel pageModel);

    /**
     * 查询当前类型数据的总数
     * @param ptype
     * @return
     */
    Long queryLoanInfosByTypeAndCount(Integer ptype);


    /**
     * 根据id查询当前产品
     * @param loanId
     * @return
     */
    LoanInfo queryLoanInfoById(Integer loanId);
}
