package com.bjpn.money.mapper;

import com.bjpn.money.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //查询总人数
    Long selectUserCount();

    //查询电话号码相同的人数
    int selectUserCountByPhone(String phone);

    //登录的方法
    User selectUserByIdCard(String phone, String loginPassword);


    //根据电话添加真实姓名和身份证
    int updateRealNameAndIdCardByPhone(User user);
}