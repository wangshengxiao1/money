package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.model.*;
import com.bjpn.money.service.*;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.HttpClientUtils;
import com.bjpn.money.util.PageModel;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class, timeout = 20000, version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = RedisService.class, timeout = 20000, version = "1.0.0")
    RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class, timeout = 20000, version = "1.0.0")
    FinanceAccountService financeAccountService;

    @Reference(interfaceClass = IncomeRecordService.class, timeout = 20000, version = "1.0.0")
    IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = RechargeRecordService.class, timeout = 20000, version = "1.0.0")
    RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = BidInfoService.class, timeout = 20000, version = "1.0.0")
    BidInfoService bidInfoService;

    //跳转注册页面
    @GetMapping("/loan/page/register")
    public String register() {
        return "register";
    }


    //从数据库中查找相同的数据
    @GetMapping("/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(name = "phone", required = true) String phone) {
        int num = userService.queryUserCountByPhone(phone);
        //根据num判断是否有重复，返回map集合集合中存入一些参数
        if (num < 1) {
            return Result.success("手机号码没有被注册");
        }
        return Result.error("手机号码已被注册");
    }

    //注册方法向数据库中添加数据
    @PostMapping("/addUser")
    @ResponseBody
    public Object addUser(@RequestParam(name = "phone", required = true) String phone, @RequestParam(name = "loginPassword", required = true) String loginPassword,@RequestParam(name = "messageCode", required = true) String messageCode) {
        //判断验证码是否正确
        String code = redisService.pop(phone);
        if (!StringUtils.equals(code,messageCode)){
            return Result.error("验证码错误");
        }
        //注册方法返回对象,注册和送大礼
        User user = userService.regist(phone, loginPassword);
        //判断是否为空
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("注册失败");
        }
        return Result.success("注册成功");
    }

    //实名认证方法向数据库中添加或修改数据
    @PostMapping("/addRealName")
    @ResponseBody
    public Object addRealName(@RequestParam(name = "phone", required = true) String phone,
                              @RequestParam(name = "realName", required = true) String realName,
                              @RequestParam(name = "idCard", required = true) String idCard,
                              @RequestParam(name = "messageCode", required = true) String messageCode){
        //判断验证码是否正确
        String code = redisService.pop(phone);
        if (!StringUtils.equals(code,messageCode)){
            System.out.println("测试进来了没");
            return Result.error("验证码错误");
        }

        //判断手机号码是否存在
        int num = userService.queryUserCountByPhone(phone);
        if (num<1){
            //手机号没有被注册，直接往数据库中添加
            num = userService.addRealNameUser(phone,realName,idCard);
            if (num==1){
                //添加成功
                return Result.success("实名认证成功，已添加");
            }else {
                //添加失败
                return Result.error("网络超时，请重新认证");
            }
        }else {
            //存在手机号，直接更新
            num = userService.changeUser(phone,realName,idCard);
            if (num==1){
                //添加成功
                return Result.success("实名认证成功，已更新");
            }else {
                //添加失败
                return Result.error("网络超时，请重新认证");
            }
        }
    }

    //后台发送请求获取验证码
    @GetMapping("/loan/page/messageCode")
    @ResponseBody
    public Object messageCode(@RequestParam(name = "phone", required = true) String phone, HttpServletRequest request) {
        //提交请求
        //获取4位数随机验证码
        String code = Result.generateCode(4);
        //发送get请求，带请求url和map参数,参数格式去京东万象上找
        Map<String, String> map = new HashMap<>();
        map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
        map.put("mobile", phone);
        map.put("content", "【创信】你的验证码是：" + code + "，3分钟内有效！");

        //设置一个模拟报文
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 420842,\n" +
                "        \"TaskID\": 18424321,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    }\n" +
                "}";
        //异常捕获可以不让用户看到报错
        try {
            //result = HttpClientUtils.doGet("https://way.jd.com/chuangxin/dxjk", map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常");
        }

        //将返回值转为josn对象，利用内置方法获取特定参数
        JSONObject jsonObject = JSONObject.parseObject(result);
        //公共返回参数，主要看code和ReturnStatus的返回值
        String code1 = jsonObject.getString("code");
        String code2 = jsonObject.getJSONObject("result").getString("ReturnStatus");

        //对错误返回值进行判断，让用户能够看到失败原因
        if (!StringUtils.equals(code1,"10000")){
            return Result.error("通讯异常，未连接");
        }
        if (!StringUtils.equals(code2,"Success")){
            return Result.error("验证码发送失败");
        }

        //若发送成功，将电话和验证码存入redis中，为了之后输入验证码作匹配是否一致
        //创建redis接口
        redisService.push(phone,code);

        //发送成功返回成功带验证码参数
        return Result.success(code);
    }


    //跳转到实名认证页面
    @GetMapping("/loan/page/realName")
    public String realName(){
        return "realName";
    }

    //后台发送请求查看身份证号和姓名是否匹配
    @GetMapping("/loan/page/checkIdCard")
    @ResponseBody
    public Object checkIdCard(@RequestParam(name = "realName", required = true) String realName,@RequestParam(name = "idCard", required = true) String idCard){
        //发送get请求，带请求真实姓名和身份证号参数,参数格式去京东万象上找
        Map<String, String> map = new HashMap<>();
        map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
        map.put("cardNo", idCard);
        map.put("realName", realName);
        //设置一个模拟报文
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        //使用服务器内部请求工具类
        try {
            //result = HttpClientUtils.doGet("https://way.jd.com/youhuoBeijing/test", map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将result转化为json对象使用内置的方法获取参数
        JSONObject jsonObject = JSONObject.parseObject(result);
        String code = jsonObject.getString("code");
        String ok = jsonObject.getJSONObject("result").getJSONObject("result").getString("isok");
        //对错误返回值进行判断，让用户能够看到失败原因
        if (!StringUtils.equals(code,"10000")){
            return Result.error("通讯异常，未连接");
        }
        if (!StringUtils.equals(ok,"true")){
            return Result.error("请输入正确的身份证号");
        }
        return Result.success("身份证号和姓名匹配");
    }

    //跳转到登录页面
    @GetMapping("/loan/page/login")
    public String login(@RequestParam(name = "ReturnUrl",required = true) String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "login";
    }

    //登录方法
    @PostMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(HttpServletRequest request,@RequestParam(name = "phone",required = true) String phone,@RequestParam(name = "loginPassword",required = true) String loginPassword){
        HttpSession session = request.getSession();
        //根据电话身份证查询数据
        User user = userService.queryUserByIdCard(phone,loginPassword);
        if (!ObjectUtils.allNotNull(user)){
            return Result.error("账号或密码不匹配");
        }
        //登录成功查询一次余额
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUserId(user.getId());
        //将余额存入session中
        Double money = financeAccount.getAvailableMoney();
        request.getSession().setAttribute(Constant.AVAILABLE_MONEY,money);

        //登录成功将对象放入session中
        session.setAttribute(Constant.LOGIN_USER,user);
        return Result.success();
    }

    //进入我的小金库
    @GetMapping("/loan/page/myCenter")
    public String  myCenter(HttpServletRequest request,Model model){
        //进入之前查询最近投资，最近充值，最近收益
        //根据登录信息查询投资记录表收益记录表，充值记录表
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        Integer uid = user.getId();
        //根据用户ID内联查询投资记录表
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoByUid(uid);
        //根据用户id查询充值记录表,按充值时间降序
        List<RechargeRecord> rechargeRecords = rechargeRecordService.queryRechargeRecordByUid(uid);
        //根据用户id查询收益表
        List<IncomeRecord> incomeRecords = incomeRecordService.queryIncomeRecordByUid(uid);
        //将查询到的信息放入model中
        model.addAttribute("bidInfoList",bidInfoList);
        model.addAttribute("rechargeRecords",rechargeRecords);
        model.addAttribute("incomeRecords",incomeRecords);

        return "myCenter";
    }

    //图片上传
    @PostMapping("/loan/uploadHeader")
    public String uploadHeader(HttpServletRequest request, MultipartFile upImg,Model model){
        //上传文件,获取文件原名称
        System.out.println("接收到的fileUp=" + upImg);
        String oldName = upImg.getOriginalFilename();
        //获取文件新名称
        String file = UUID.randomUUID().toString().replace("-",  "") + oldName.substring(oldName.lastIndexOf("."));
        System.out.println("file = " + file);
        //将文件名存入session对象中
        model.addAttribute("file",file);
        request.getSession().setAttribute("file",file);
        //获取文件上传路径
        String path = request.getServletContext().getRealPath("/img");
        System.out.println("path = " + path);
        //获取file流对象
        File file1 = new File( "D:/works/IDEA/money/005-money-web/src/main/resources/static/img/" + file);
        //上传文件
        try {
            upImg.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File file11 = new File("D:/works/IDEA/money/005-money-web/src/main/resources/static/images/");
        return "myCenter";
    }



    //同步退出方法,退出到当前页
   /* @GetMapping("/loan/logout")
    public String logout(HttpServletRequest request,@RequestParam(name = "ReturnUrl",required = true) String ReturnUrl){
        //退出将session对象中的user销毁
        request.getSession().removeAttribute(Constant.LOGIN_USER);

        return "redirect:" + ReturnUrl;
    }*/

    //异步退出方法,退出到当前页
    @GetMapping("/loan/logout")
    @ResponseBody
    public Object logout(HttpServletRequest request){
        //退出将session对象中的user销毁
        request.getSession().removeAttribute(Constant.LOGIN_USER);

        return Result.success();
    }

    //查看全部投资
    @GetMapping("/loan/myInvest")
    public String myInvest(@RequestParam(name = "cunPage" ,defaultValue = "1") Long cunPage,Model model,HttpServletRequest request){
        //获取用户id，能进小金库肯定登录
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        Integer uid = user.getId();
        //创建分页模型
        PageModel pageModel = new PageModel(6);
        //计算总条数
        Long num = bidInfoService.queryCountBidInfoByUid(uid);
        pageModel.setTotalCount(num);
        //通过总条数计算总页数
        if (num%pageModel.getPageSize()==0){
            pageModel.setTotalPage(num/pageModel.getPageSize());
        }else {
            pageModel.setTotalPage(num/pageModel.getPageSize() + 1);
        }
        //设置当前页
        pageModel.setCunPage(cunPage);


        //查询所有投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoByUidAndPage(uid,pageModel);

        //返回分页模型和投资记录
        model.addAttribute("bidInfoList",bidInfoList);
        model.addAttribute("pageModel",pageModel);
        return "myInvest";
    }

    //查看全部充值
    @GetMapping("/loan/myRecharge")
    public String myRecharge(){

        return "myRecharge";
    }

    //查看全部收益计划
    @GetMapping("/loan/myIncome")
    public String myIncome(){


        return "myIncome";
    }

}
