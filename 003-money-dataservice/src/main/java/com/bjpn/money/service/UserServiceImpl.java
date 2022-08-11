package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.UserMapper;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.MyThreadPoolUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现类
 */
@Service(interfaceClass = UserService.class ,timeout = 20000,version = "1.0.0")
@Component
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Autowired(required = false)
    RedisTemplate redisTemplate;

    /**
     * 查询总人数的方法,先从缓存中查询看是否能命中
     * @return
     */
    @Override
    public Long queryUserCount() {
        //将key键序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //先从缓存中查找
        Long userCount = (Long)redisTemplate.opsForValue().get(Constant.USER_COUNT);
        //判断查找的值是否为空
        if (userCount==null){
            //再从缓存中查找一次
            userCount = (Long)redisTemplate.opsForValue().get(Constant.USER_COUNT);
            if (userCount==null){
                //从数据库中查找，将找到的值放入缓存中
                userCount = userMapper.selectUserCount();
                redisTemplate.opsForValue().set(Constant.USER_COUNT,userCount,20, TimeUnit.SECONDS);
            }else {
                System.out.println("----缓存命中-----");
            }
        }else {
            System.out.println("-----缓存命中-----");
        }
        return userCount;
    }

    /**
     * 查询所有相同的电话
     * @param phone
     * @return
     */
    @Override
    public int queryUserCountByPhone(String phone) {

        return userMapper.selectUserCountByPhone(phone);
    }

    /**
     * 注册的方法
     * @param phone
     * @param loginPassword
     * @return
     */
    @Override
    public User regist(String phone, String loginPassword) {
        //创建user对象作为传入的参数和返回值
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        int i = userMapper.insertSelective(user);
        //判断是否添加成功，添加成功将888余额添加到账户中
        if (i==1){
            //使用线程池工具
            ThreadPoolExecutor threadPool = MyThreadPoolUtils.getThreadPool();
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    FinanceAccount financeAccount = new FinanceAccount();

                    //用jdbc在插入过程中获取主键
                    //Connection connection;
                    //connection.prepareStatement("", Statement.RETURN_GENERATED_KEYS);
                    financeAccount.setUid(user.getId());
                    financeAccount.setAvailableMoney(888.0);
                    //业务上看赠送红包不用必须成功
                    int i1 = financeAccountMapper.insertSelective(financeAccount);
                }
            });
        }
        return user;
    }

    /**
     * 登录的方法
     * @param phone
     * @param loginPassword
     * @return
     */
    @Override
    public User queryUserByIdCard(String phone, String loginPassword) {
        User user = userMapper.selectUserByIdCard(phone, loginPassword);
        //登录成功更改最后登录时间
        if (ObjectUtils.allNotNull(user)) {

            //使用线程池工具
            ThreadPoolExecutor threadPool = MyThreadPoolUtils.getThreadPool();
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    user.setLastLoginTime(new Date());
                    userMapper.updateByPrimaryKeySelective(user);
                }

            });
        }
        return user;
    }

    /**
     * 未注册手机号，添加实名
     * @return
     */
    @Override
    public int addRealNameUser(String phone,String realName,String idCard) {
        User user = new User();
        user.setPhone(phone);
        user.setName(realName);
        user.setIdCard(idCard);
        return userMapper.insertSelective(user);
    }


    /**
     * 已注册手机号，更新姓名和身份证
     * @param phone
     * @param realName
     * @param idCard
     * @return
     */
    @Override
    public int changeUser(String phone,String realName, String idCard) {
        User user = new User();
        user.setPhone(phone);
        user.setName(realName);
        user.setIdCard(idCard);
        int num = userMapper.updateRealNameAndIdCardByPhone(user);
        return num;
    }
}
