package com.bjpn.money.service;

/**
 * 验证码缓存
 */
public interface RedisService {
    /**
     * 将生成的验证码存入缓存中
     * @param phone
     * @param code
     */
    void push(String phone, String code);

    /**
     * 判断验证码是否正确
     * @param phone
     * @return
     */
    String pop(String phone);
}
