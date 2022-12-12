package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.model.RankTopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    //投资排行榜
    @Override
    public List<RankTopVo> zpop() {
        List<RankTopVo> rankTopVos=new ArrayList<RankTopVo>();
        Set<ZSetOperations.TypedTuple> userMoneys = redisTemplate.opsForZSet().reverseRangeWithScores("userMoney", 0, 5);
        Iterator<ZSetOperations.TypedTuple> iterator = userMoneys.iterator();
        while(iterator.hasNext()){
            RankTopVo rankTopVo = new RankTopVo();
            ZSetOperations.TypedTuple zt= iterator.next();
            rankTopVo.setPhone(zt.getValue().toString());
            rankTopVo.setBidMoneys( zt.getScore());
            rankTopVos.add(rankTopVo);
        }
        return rankTopVos;
    }
}
