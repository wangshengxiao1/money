package com.bjpn.money.util;

import java.util.HashMap;
import java.util.Map;

public class Result {
    //不带参数的map集合，返回失败和成功信息
    public static Map<String,Object> success(){
        Map<String,Object> map=new HashMap<>();
        map.put("code",1);
        map.put("message","");
        map.put("success",true);
        return map;
    }
    public static Map<String,Object> success(String message){
        Map<String,Object> map=new HashMap<>();
        map.put("code",1);
        map.put("message",message);
        map.put("success",true);
        return map;
    }

    public static Map<String,Object> error(){
        Map<String,Object> map=new HashMap<>();
        map.put("code",0);
        map.put("message","");
        map.put("success",false);
        return map;
    }
    public static Map<String,Object> error(String message){
        Map<String,Object> map=new HashMap<>();
        map.put("code",0);
        map.put("message",message);
        map.put("success",false);
        return map;
    }

    //生成4位随机码
    public static String generateCode(int len){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            //获取0到9之间的随机数四舍五入
            stringBuilder.append(Math.round(Math.random()*9));
        }

        return stringBuilder.toString();
    }
}
