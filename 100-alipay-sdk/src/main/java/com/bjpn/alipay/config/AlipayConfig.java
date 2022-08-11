package com.bjpn.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000121644590";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCx8SlNi9wze/ZSIFNyu/gxwx8vzpvnSXVGUl2/xIMgSy8fdLkbsE9XgJlMkxR9LGB2D06jB4/QLRcn0YPecGglgOw3sMrrjTmond2QagyhQnvpqFXa0F8O5pe5u/ohJB4N6cEtMVZkqtLeDl0s0OnSRWrXayaNNi7p32qlmBfOBs+yJYfdpTFLyFPV+hoFBopg4VZR0cXoV2OK20weW+IQR7QjXC6aFm0zvt9FkozE1sTkUeelX24JmAIsukcr6hnwU6oUVaRnL037K/ZXYYhfWYK0cmj8ZM9A0bRcl8KIr90pPUblg0mdJX2xG3ZbcyIsKQBZua/DDjUnzW5AKXRrAgMBAAECggEBAJOaY1ODD47ZZjMrw2zqBXBp1cXXUbWiVpiVnFSFkuvH0ff6383bC4WfFArT+mv20rE781QC4sWhkZKjFgr/HTT243syuLB5/nkjMnYnsgBWYOkqjMUskZ2PpA4aotq41gFehLKTOYHZvX8bVSjmxv54PK8in+EFMBcqD5H7PsvTsXvNJ1Uh5rPDlpJnbcs1P66F8Vo3V7ICq/gah6HKRzTgjQM1MY+YJSk1cihGZS7CtKlkohj+4Zr3EHCQjYphdeVbJW/K2BA5QobDAPMfaWRkn6cwYfMcj6WPLxEFojaNl1kHpKbFfzxhTeu6vyMIRwQ/iOQcK2JJP/q8pDH7PekCgYEA5kFDHWvTGdJ4dzgFZFGvV8uHZlACVxjITw2OzUwsCojo+Ju/2gBiQzzQYq+U6qvPHo2u2juFJ9IzJwaSWTRgHQmJnftp+HQcCoiwCS6W1DA1vJ28kPOM/TXkmmYjoy9F6dY0eSn05TUGwIymE4jXCnp71uQeadAA/A9/x8wSH8UCgYEAxdaDTkc51DOS4UFogowmwq2CvNUranQFoGvE7UHK6X94JxdRHQPuxQdDuV4Mhp/N7uerr8GY3L0Bo3AHBaAuRvPnYsXnHmFFYijlvawd+OBhVd9Bh2E05Y1GQqJU+IM3o5slCAnghMSkQkI4tcif4OC45994oEztH+kqyzuz1m8CgYBtihqtwoAZRsG8pdAa26l5MQBnMiUK3mVdBmwrStOg52U98Iqi4Y/dwO66JCHKSpYsSOcoNyyvtxZSuJOjEIGxDrqDh/nwEvzMHh7xH1Lt+0l1Q+1TYCpww9TPh0vo9DJkfl1UTxuMd1+Q9tiDbpQvDfogT0IQwL3XLGBz4qe0qQKBgQCcBcn2IPv9W1MZ7MCkPC2RWM+DZsZfpPKPCo764elJ7qpugdgJfwF+R3VkqkRMo5kAMZrX+GE6rKCvKXmQOTxl3pL79cGI1wOmH0TYKfZVQXfiBjkisEnMQ3dGs8k5WBq4H3G7/jYf3wj/7EPXMUrNSczpqPbzzNcxVGzYbNQuZwKBgDg650ggOtlUHQU09G2B7/NfuolXHqDQro+dbdGhdSKZgrHNTT78Yf4YDJEV5tquOs912FVBoukElSCxZyFFJ6Umq5RABlSfSRMya5mfg8fLFVD7jx2zzUiIzLy7XafL5I4aDAiIZIiRTUvfvTA1+3ya6fE/GQDzuQfrM/2hpNIC";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhqv3Ldb52fXZ1p/inIlfh9y0Vf+O0zGjX7NLXbM/S0UtsPpOfnIj1bxc3Og/rNwgXRcbNBSlk3CzQNO4p8M0HqMFiNn69MPuWwdJau+m8oZFXdaeqZPJSY2XBfqqEvX8rGklKj14Cow7WlvSqrcBy6dihxSwBEbuC7wIgMA2DsB2y3Xg32SJ0UncV1GpBdCQewknEy6+B1RPh0p7ibwKPENAS11xBHM/7v2NRqhgUaT1MxzNK55IlxRXn7aMLZeWZCARFVxhbFe3bMFX5NuSnoHHKoY4mrwzvhdPrAlIeDL1uL85pnDTBZlJD0HG3G296IUbsvKaGiIaqG53CpHATwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/100-alipay-sdk/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/100-alipay-sdk/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

