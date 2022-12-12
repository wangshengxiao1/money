package com.bjpn.money.service;

import com.bjpn.money.model.RankTopVo;

import java.util.List;

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

    /**
     * 根据向Redis中存入的数据进行投资排行
     * @return
     */
    List<RankTopVo> zpop();
}
