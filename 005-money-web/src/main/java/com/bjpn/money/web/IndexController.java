package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.service.UserService;
import com.bjpn.money.util.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//首页展示信息
@Controller
public class IndexController {


    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;


    @RequestMapping("/index")
    public String index(Model model){
        //查询历年的年化利率（历年平均利率）
        Double loanInfoHisRateAvg = loanInfoService.queryLoanInfoHisRateAvg();
        model.addAttribute(Constant.LOAN_INFO_HISTORY_RATE_AVG,loanInfoHisRateAvg);

        //查询所有注册的人数
        Long userCount = userService.queryUserCount();
        model.addAttribute(Constant.USER_COUNT,userCount);

        //累计成交额
        Double bidInfoMoneySum = bidInfoService.queryBidInfoMoneySum();
        model.addAttribute(Constant.BID_MONEY_SUM,bidInfoMoneySum);

        //根究产品类型和数量查询产品信息,可以将参数添加到map集合中
        Map<String, Object> map = new HashMap<>();

        //新手宝
        map.put("ptype",0);
        map.put("start",0);
        map.put("content",1);
        List<LoanInfo> loanInfosByTypeAndNum_X = loanInfoService.queryLoanInfosByTypeAndNum(map);
        model.addAttribute("loanInfosByTypeAndNum_X",loanInfosByTypeAndNum_X);

        //优选标
        map.put("ptype",1);
        map.put("start",0);
        map.put("content",4);
        List<LoanInfo> loanInfosByTypeAndNum_Y = loanInfoService.queryLoanInfosByTypeAndNum(map);
        model.addAttribute("loanInfosByTypeAndNum_Y",loanInfosByTypeAndNum_Y);

        //散标
        map.put("ptype",2);
        map.put("start",0);
        map.put("content",8);
        List<LoanInfo> loanInfosByTypeAndNum_S = loanInfoService.queryLoanInfosByTypeAndNum(map);
        model.addAttribute("loanInfosByTypeAndNum_S",loanInfosByTypeAndNum_S);


        return "index";
    }
}
