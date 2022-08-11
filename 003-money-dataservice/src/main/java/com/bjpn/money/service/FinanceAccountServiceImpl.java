package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.model.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service(interfaceClass = FinanceAccountService.class,timeout = 20000,version = "1.0.0")
@Component
public class FinanceAccountServiceImpl implements FinanceAccountService {


    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUserId(Integer id) {
        return financeAccountMapper.selectByUserId(id);
    }

    @Override
    public int insertMoney(Integer uid,Double allMoney) {
        return financeAccountMapper.insertMoneyByBid(uid,allMoney);
    }
}
