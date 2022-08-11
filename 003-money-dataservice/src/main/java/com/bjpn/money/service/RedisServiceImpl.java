package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Service(interfaceClass = RedisService.class ,timeout = 20000,version = "1.0.0")
@Component
public class RedisServiceImpl implements RedisService {

    @Autowired(required = false)
    RedisTemplate redisTemplate;
    @Override
    public void push(String phone, String code) {
        //序列化key键
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //将phone作为key键code作为值存入缓存中，下次查找就根据phone来查找
        redisTemplate.opsForValue().set(phone,code);
    }


    //判断验证码是否正确的方法
    @Override
    public String pop(String phone) {
        String code = (String) redisTemplate.opsForValue().get(phone);
        return code;
    }
}
