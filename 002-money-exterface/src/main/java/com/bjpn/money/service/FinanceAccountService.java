package com.bjpn.money.service;

import com.bjpn.money.model.FinanceAccount;

/**
 * 下拉框刷新余额方法
 */
public interface FinanceAccountService {
    FinanceAccount queryFinanceAccountByUserId(Integer id);

    /**
     * 往个人账户中添加充值金额
     * @param uid
     * @return
     */
    int insertMoney(Integer uid,Double allMoney);
}
