package com.bjpn.money.service;

import com.bjpn.money.model.User;

/**
 * 用户接口
 */
public interface UserService {
    /**
     * 查询平台用户数
     * @return
     */
    Long queryUserCount();

    /**
     * 查询相同电话号码的人数
     * @param phone
     * @return
     */
    int queryUserCountByPhone(String phone);

    /**
     * 注册的方法
     * @param phone
     * @param loginPassword
     * @return
     */
    User regist(String phone, String loginPassword);

    /**
     * 登录方法
     * @return
     */
    User queryUserByIdCard(String phone,String loginPassword);

    /**
     * 未注册手机号，添加实名
     * @return
     */
    int addRealNameUser(String phone,String realName,String idCard);

    /**
     * 已注册手机号，更新姓名和身份证
     * @param phone
     * @param realName
     * @param idCard
     * @return
     */
    int changeUser(String phone, String realName, String idCard);
}
