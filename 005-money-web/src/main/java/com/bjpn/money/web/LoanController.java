package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.RankTopVo;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.IncomeRecordService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.service.RedisService;
import com.bjpn.money.util.PageModel;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看更多散标类产品
 */
@Controller
public class LoanController {

    @Reference(interfaceClass = LoanInfoService.class, timeout = 20000, version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class, timeout = 20000, version = "1.0.0")
    BidInfoService bidInfoService;

    @Reference(interfaceClass = IncomeRecordService.class, timeout = 20000, version = "1.0.0")
    IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = RedisService.class ,timeout = 20000,version = "1.0.0")
    RedisService redisService;

    //同一类型产品分页展示
    @GetMapping("loan/loan")
    public String loan(HttpServletRequest request, @RequestParam(defaultValue = "1") Long cunPage, @RequestParam(name = "ptype", required = true, defaultValue = "2") Integer ptype, Model model) {
       /* Map<String, Object> map = new HashMap();
        map.put("ptype",ptype);
        map.put("start",0);
        map.put("content",9);
        List<LoanInfo> LoanInfosByTypeAndNum = loanInfoService.queryLoanInfosByTypeAndNum(map);
        model.addAttribute("LoanInfosByTypeAndNum",LoanInfosByTypeAndNum);*/



        //使用封装的分页模型，先判断session中是否有分页模型对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null){
            //session中么有pageModel对象，创建一个放入session对象中
            pageModel = new PageModel(9);
            request.getSession().setAttribute("pageModel",pageModel);
        }

        //查询当前类型所有条数
        Long loanInfosByTypeAndCount = loanInfoService.queryLoanInfosByTypeAndCount(ptype);


        //将数据条数存入分页模型中
        pageModel.setTotalCount(loanInfosByTypeAndCount);

        //根据查询的数据计算总页数
        if (loanInfosByTypeAndCount%pageModel.getPageSize()==0){
            //若能被除尽,将总页数添加到分页模型中
            pageModel.setTotalPage(loanInfosByTypeAndCount/pageModel.getPageSize());
        }else {
            //若除不尽
            pageModel.setTotalPage(loanInfosByTypeAndCount/pageModel.getPageSize() + 1);
        }

        //设置当前页，作出提前判断
        if (cunPage > pageModel.getTotalPage() || cunPage < 1){
            cunPage = 1l;
        }
        pageModel.setCunPage(cunPage);

        //将分页模型存入查询方法中，根据类型来查
        List<LoanInfo> loanInfosByTypeAndModel = loanInfoService.queryLoanInfosByTypeAndModel(ptype, pageModel);
        model.addAttribute("loanInfosByTypeAndModel", loanInfosByTypeAndModel);


        //投资排行榜,降序查询投资记录表
        //List<IncomeRecord> incomeRecordList = incomeRecordService.queryByMoney();
        //model.addAttribute("incomeRecordList",incomeRecordList);


        //新的投资排行榜，从Redis中取出数据
        List<RankTopVo> rankTopVos = redisService.zpop();
        model.addAttribute("rankTopVos",rankTopVos);

        return "loan";
    }

    //根据产品id查询产品，详情页展示
    @GetMapping("/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,@RequestParam(defaultValue = "1") Long cunPage,@RequestParam(name = "loanId",required = true,defaultValue = "1")Integer loanId,Model model){
        //根据产品id查询产品信息
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        model.addAttribute("loanInfo",loanInfo);

        //展现产品投资记录


        //使用封装的分页模型，先判断session中是否有分页模型对象
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null){
            //session中么有pageModel对象，创建一个放入session对象中
            pageModel = new PageModel(9);
            request.getSession().setAttribute("pageModel",pageModel);
        }

        //当前页面值
        pageModel.setCunPage(cunPage);

        //通过产品id查询投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfosByLoanId(loanId,pageModel);
        model.addAttribute("bidInfoList",bidInfoList);
        return "loanInfo";
    }

    //单独查询当前产品的投资人的方法
    @RequestMapping("/loan/user")
    @ResponseBody
    public Object users(HttpServletRequest request,@RequestParam(name = "cunPage",required = true) Long cunPage,@RequestParam(name = "loanId",required = true)Integer loanId){


        //从session对象中取分页模型
        PageModel pageModel = (PageModel) request.getSession().getAttribute("pageModel");
        if (pageModel==null){
            //session中么有pageModel对象，创建一个放入session对象中
            System.out.println("没有对象");
            pageModel = new PageModel(9);
            request.getSession().setAttribute("pageModel",pageModel);
        }

        //获取所有投资人数
        //Long bidInfoCount = bidInfoService.queryAll(loanId);
        //获取总页数
        /*if (bidInfoCount%pageModel.getPageSize()==0){
            pageModel.setTotalPage(bidInfoCount/pageModel.getPageSize());
        }else {
            pageModel.setTotalPage(bidInfoCount/pageModel.getPageSize() + 1);
        }*/

        pageModel.setCunPage(cunPage);
        System.out.println(pageModel);
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfosByLoanId(loanId, pageModel);


        return bidInfoList;
    }
}
