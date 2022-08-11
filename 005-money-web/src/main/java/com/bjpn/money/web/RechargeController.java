package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpn.money.config.AlipayConfig;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.model.User;
import com.bjpn.money.service.FinanceAccountService;
import com.bjpn.money.service.RechargeRecordService;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.HttpClientUtils;
import com.bjpn.money.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RechargeController {

    @Reference(interfaceClass = RechargeRecordService.class,timeout = 20000,version = "1.0.0")
    RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = FinanceAccountService.class,timeout = 20000,version = "1.0.0")
    FinanceAccountService financeAccountService;

    @GetMapping("/loan/page/toRecharge")
    //跳转到充值页面
    public String toRecharge(HttpServletRequest request){
        //判断是否登录
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        if (!ObjectUtils.allNotNull(user)){
            //获取的值为空，跳转到登录页面
            return "redirect:/loan/page/login";
        }
        //是否实名认证
        if (!ObjectUtils.allNotNull(user.getIdCard(),user.getName())){
            //未实名认证，跳转到实名认证
            return "redirect:/loan/page/realName";
        }
        return "toRecharge";
    }



    @PostMapping("/loan/page/aliPay")
    //充值流程
    public String aliPay(Double rechargeMoney, Model model, HttpServletRequest request){
        //选择支付宝支付，生成充值记录，重新在实名认证和登录认证一遍
        User user = (User)request.getSession().getAttribute(Constant.LOGIN_USER);
        //是否登录
        if (!ObjectUtils.allNotNull(user)){
            //获取的值为空，跳转到登录页面
            return "redirect:/loan/page/login";
        }
        //是否实名认证
        if (!ObjectUtils.allNotNull(user.getIdCard(),user.getName())){
            //未实名认证，跳转到实名认证
            return "redirect:/loan/page/realName";
        }
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = simpleDateFormat.format(new Date());
        rechargeRecord.setRechargeNo(format + Result.generateCode(2) + user.getId() + Result.generateCode(2));
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus(0 + "");
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝支付");

        int num = rechargeRecordService.recharge(rechargeRecord);
        if (num != 1){
            //添加充值记录失败
            model.addAttribute("trade_msg","订单生成失败");
            return "toRechargeBack";
        }

        //支付流程开始,目标跳转到支付页面
        //跳转到form表单页面，表单携带路径和参数跳转到007模块再重复同样的动作
        model.addAttribute("rechargeRecord",rechargeRecord);
        return "toAliPay";
    }

    //同步返回交易信息
    @GetMapping("/loan/page/payBack")
    public String payBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //调用SDK验证签名,验证是否安全
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            //验证出异常
            return "404";
        }

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //验签成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            //out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);

            String result = "{\n" +
                    "\t\"alipay_trade_query_response\": {\n" +
                    "\t\t\"code\": \"10000\",\n" +
                    "\t\t\"msg\": \"Success\",\n" +
                    "\t\t\"buyer_logon_id\": \"ncq***@sandbox.com\",\n" +
                    "\t\t\"buyer_pay_amount\": \"0.00\",\n" +
                    "\t\t\"buyer_user_id\": \"2088622987504223\",\n" +
                    "\t\t\"buyer_user_type\": \"PRIVATE\",\n" +
                    "\t\t\"invoice_amount\": \"0.00\",\n" +
                    "\t\t\"out_trade_no\": \"20228101051859\",\n" +
                    "\t\t\"point_amount\": \"0.00\",\n" +
                    "\t\t\"receipt_amount\": \"0.00\",\n" +
                    "\t\t\"send_pay_date\": \"2022-08-10 10:06:39\",\n" +
                    "\t\t\"total_amount\": \"0.01\",\n" +
                    "\t\t\"trade_no\": \"2022081022001404220502518525\",\n" +
                    "\t\t\"trade_status\": \"TRADE_SUCCESS\"\n" +
                    "\t},\n" +
                    "\t\"sign\": \"pa0tqU263LXDIDdtKXmaUwXuo7nXYAMUR4FbYdYNMBVSh/H9rdojk/AMiYduBVLcSuvFLssGL+ww2fB89jV7h+HAs06/mbbiRDj6UdxZeg+xnZzwKKO1fjU0+vFyR+JNTF8v2UjI5HasmVF1vBdf44wwDffSslj3yZqZjqbc1eDwf14lM2PeYXHXktjKI7CSJumqGqS5Yl5XdkddiWKfVu9tFnhWbk0XWLwlprSIO9vApxT1OUhSk4bzzdmyQKtSBykYFa3fRPgcly1Hp9HeOFcZuGlWxAjj0qjyTjNnH4JN55gpp3iqDyo6hfUZAcKins6kd65RyRYqTVxhaYH4pg==\"\n" +
                    "}";
            //调用007的查询接口
            try {
                result = HttpClientUtils.doGet("http://localhost:9007/007-money-alipay/loan/page/queryOrder?out_trade_no=" + out_trade_no);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //对返回值进行解析
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject alipay = jsonObject.getJSONObject("alipay_trade_query_response");
            //判断code
            String code = alipay.getString("code");
            if (!StringUtils.equals(code,"10000")){
                //发送短信，请尽快支付(若已支付请忽略)
                Map<String, String> map = new HashMap<>();
                map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
                map.put("tos", "13370077805");
                map.put("msg", "您好,您有订单未支付,请在到期前尽快支付(若已支付请忽略)");
                //使用HttpClientUtils发送信息
                String doGet = HttpClientUtils.doGet("https://way.jd.com/jixintong/SMSmarketing", map);
            }

            //判断交易状态
            String trade_status = alipay.getString("trade_status");
            if (StringUtils.equals(trade_status,"WAIT_BUYER_PAY")){
                //等待付款，向客户发送短信
                Map<String, String> map = new HashMap<>();
                map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
                map.put("tos", "13370077805");
                map.put("msg", "您好,您有订单未支付,请在到期前尽快支付(若已支付请忽略)");
                String doGet = HttpClientUtils.doGet("https://way.jd.com/jixintong/SMSmarketing", map);

            }

            if (StringUtils.equals(trade_status,"TRADE_CLOSED")){
                //未付款交易超时关闭，或支付完成后全额退款，支付失败状态改为2，向客户发送短信
                //根据订单号修改状态,修改失败无所谓，定时器会重新修改的
                rechargeRecordService.modifyStatusByNo(out_trade_no);
                Map<String, String> map = new HashMap<>();
                map.put("appkey", "31d8450ad65b94d3e33d6f41c66c68d1");
                map.put("tos", "13370077805");
                map.put("msg", "您好,您有订单未支付,请在到期前尽快支付(若已支付请忽略)");
                String doGet = HttpClientUtils.doGet("https://way.jd.com/jixintong/SMSmarketing", map);

            }

            if (StringUtils.equals(trade_status,"TRADE_SUCCESS")){
                //若订单记录消失，模拟下单
                //根据订单号查询订单记录是否存在
                RechargeRecord rechargeRecord = rechargeRecordService.queryRechargeByNo(out_trade_no);
                if (!ObjectUtils.allNotNull(rechargeRecord)){



                }
                //交易支付成功，修改支付状态为1，往账户余额中添加充值金额
                //若订单号存在，根据订单号查询用户的uid，根据用户uid往用户账户中添加
                Integer uid = rechargeRecord.getUid();
                double money = Double.parseDouble(total_amount);
                int num = financeAccountService.insertMoney(uid,money);



            }

        }else {
            response.getWriter().println("验签失败");
        }
        //——请在这里编写您的程序（以上代码仅作参考）——




        return null;
    }



    @PostMapping("/loan/page/wxPay")
    public String wxPay(Double rechargeMoney){
        System.out.println("----wxPay-----"+rechargeMoney);
        return "";
    }

}
