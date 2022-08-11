package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 产品业务接口实现类,暴露接口实现
 */
@Service(interfaceClass = LoanInfoService.class ,timeout = 20000,version = "1.0.0")
@Component
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired(required = false)
    RedisTemplate redisTemplate;

    /**
     * 查询年化利率的方法
     * @return
     */
    @Override
    public Double queryLoanInfoHisRateAvg() {

        //序列化key键
        //redisTemplate.setKeySerializer(new StringRedisSerializer());

        //从缓存中查找
        Double loanInfoHisRateAvg = (Double)redisTemplate.opsForValue().get(Constant.LOAN_INFO_HISTORY_RATE_AVG);
        if (loanInfoHisRateAvg==null){

            //再从缓存中查找一次
            loanInfoHisRateAvg = (Double)redisTemplate.opsForValue().get(Constant.LOAN_INFO_HISTORY_RATE_AVG);
            if (loanInfoHisRateAvg==null){
                System.out.println("--------从数据库中查找---------");
                loanInfoHisRateAvg = loanInfoMapper.selectLoanInfoHisRateAvg();
                //将从数据库中查找的数据添加到缓存中
                redisTemplate.opsForValue().set(Constant.LOAN_INFO_HISTORY_RATE_AVG,loanInfoHisRateAvg,20, TimeUnit.SECONDS);
            }else {
                System.out.println("-----------缓存命中-----------");
            }
        }else {
            System.out.println("------缓存命中-------");
        }

        return loanInfoHisRateAvg;
    }

    /**
     * 根据产品数量和类型查询产品
     * @param map
     * @return
     */
    @Override
    public List<LoanInfo> queryLoanInfosByTypeAndNum(Map<String, Object> map) {
        return loanInfoMapper.selectLoanInfosByTypeAndNum(map);
    }

    /**
     * 根据分页模型查询的方法实现类
     * @param pageModel
     * @return
     */
    @Override
    public List<LoanInfo> queryLoanInfosByTypeAndModel(Integer ptype,PageModel pageModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("ptype",ptype);
        map.put("start",(pageModel.getCunPage()-1)*pageModel.getPageSize());
        map.put("content",pageModel.getPageSize());
        List<LoanInfo> loanInfosByTypeAndNum = loanInfoMapper.selectLoanInfosByTypeAndNum(map);
        return loanInfosByTypeAndNum;
    }

    /**
     * 查询当前类型的所有的数据
     * @param ptype
     * @return
     */
    @Override
    public Long queryLoanInfosByTypeAndCount(Integer ptype) {
        return loanInfoMapper.selectLoanInfosByTypeAndCount(ptype);
    }

    /**
     * 根据id查询当前产品
     * @param loanId
     * @return
     */
    @Override
    public LoanInfo queryLoanInfoById(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }

}
