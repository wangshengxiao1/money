package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.User;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户投资
 */
@Controller
public class BidController {


    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;


    @PostMapping("/loan/page/invest")
    @ResponseBody
    public Object invest(@RequestParam(name = "bidMoney",required = true)Double bidMoney,
                         @RequestParam(name = "loanId",required = true)Integer loanId,
                         HttpServletRequest request){

        //是否登录
        User user =(User) request.getSession().getAttribute(Constant.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请先登陆后，再投资...");
        }

        //是否实名认证
        if(!ObjectUtils.allNotNull(user.getName(),user.getIdCard())){
            return Result.error("请实名认证后，再投资...");
        }

        //基础验证
        if (!ObjectUtils.allNotNull(bidMoney)){
            return Result.error("请输入投资金额，再投资...");
        }
        if (bidMoney%100>0){
            return Result.error("请输入金额为100整数倍，再投资...");
        }

        //正则表达式验证是否为正整数，不知道为啥不起作用
        String reg = "^[0-9]*[1-9][0-9]*$";
        Pattern compile = Pattern.compile(reg);
        String newBidMoney = bidMoney.toString();
        Matcher matcher = compile.matcher(newBidMoney);
        boolean b = matcher.find();
//        if (!b){
//            return Result.error("请输入金额为正整数，再投资...");
//        }

        //业务验证：
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);

        //是否在起投金额之间
        if(loanInfo.getBidMinLimit()>bidMoney||loanInfo.getBidMaxLimit()<bidMoney){
            return Result.error("投资金额应在"+loanInfo.getBidMinLimit()+"--"+loanInfo.getBidMaxLimit()+"之间");
        }

        //由于数据库实时更新，需要再查一遍
        loanInfo = loanInfoService.queryLoanInfoById(loanId);
        //是否在剩余可投金额之内
        if(loanInfo.getLeftProductMoney()<bidMoney){
            return Result.error("投资金额应在"+loanInfo.getBidMinLimit()+"--"+loanInfo.getLeftProductMoney()+"之间");
        }


        //投资：
        Map<String,Object> parasMap=new HashMap<>();

        parasMap.put("loanId",loanId);
        parasMap.put("bidMoney",bidMoney);
        parasMap.put("userId",user.getId());
        parasMap.put("user",user);

        //调用投资方法
        String result=bidInfoService.invest(parasMap);
        if(!StringUtils.equals("ok",result)){
            return Result.error(result);
        }



        return Result.success();
    }
}
