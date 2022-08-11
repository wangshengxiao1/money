package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.service.FinanceAccountService;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FinaceController {

    @Reference(interfaceClass = FinanceAccountService.class, timeout = 20000, version = "1.0.0")
    FinanceAccountService financeAccountService;
    //下拉框查看余额的方法
    @GetMapping("/loan/page/queryFinance")
    @ResponseBody
    public Object queryFinance(HttpServletRequest request){
        //获取登录对象的UID
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        //判断是否已经登录
        if (!ObjectUtils.allNotNull(user)){
            return Result.error("请先登录");
        }
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUserId(user.getId());
        if (!ObjectUtils.allNotNull(financeAccount)){
            return Result.error("系统正在升级，请稍等");
        }
        //将余额存入session中
        Double money = financeAccount.getAvailableMoney();
        request.getSession().setAttribute(Constant.AVAILABLE_MONEY,money);
        if (money>=10000){
            return Result.success("****");
        }
        return Result.success( financeAccount.getAvailableMoney().toString());
    }
}
