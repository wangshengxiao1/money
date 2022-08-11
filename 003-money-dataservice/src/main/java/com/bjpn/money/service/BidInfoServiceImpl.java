package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.BidInfoMapper;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.util.Constant;
import com.bjpn.money.util.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 产品投标数据接口实现类
 */

@Service(interfaceClass = BidInfoService.class, timeout = 20000, version = "1.0.0")
@Component
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Autowired(required = false)
    RedisTemplate redisTemplate;


    Map<Integer,LoanInfo> map = new HashMap<>();

    /**
     * 累计成交额
     *
     * @return
     */
    @Override
    public Double queryBidInfoMoneySum() {
        //序列化redis key键
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //从缓存中查询数据
        Double bidInfoMoneySum = (Double) redisTemplate.opsForValue().get(Constant.BID_MONEY_SUM);
        if (bidInfoMoneySum == null) {
            bidInfoMoneySum = (Double) redisTemplate.opsForValue().get(Constant.BID_MONEY_SUM);
            if (bidInfoMoneySum == null) {
                //从数据库中查询数据存入到redis缓存中
                bidInfoMoneySum = bidInfoMapper.selectBidInfoMoneySum();
                redisTemplate.opsForValue().set(Constant.BID_MONEY_SUM, bidInfoMoneySum, 20, TimeUnit.SECONDS);
            } else {
                System.out.println("------缓存命中-----");
            }
        } else {
            System.out.println("----缓存命中-----");
        }
        return bidInfoMoneySum;
    }



    /**
     * 通过产品id查询投资记录
     *
     * @param loanId
     * @return
     */
    @Override
    public List<BidInfo> queryBidInfosByLoanId(Integer loanId, PageModel pageModel) {

        Long start = (pageModel.getCunPage() - 1) * pageModel.getPageSize();
        Long content = pageModel.getPageSize().longValue();

        Map<String, Object> map = new HashMap<>();
        map.put("loanId", loanId);
        map.put("start", start);
        map.put("content", content);
        return bidInfoMapper.selectBidInfosByLoanId(map);
    }



    /**
     * 投资方法
     *
     * @param parasMap
     * @return
     */
    @Override
    @Transactional
    //同步锁不应该放在方法上，会让性能大幅下降
    public String invest(Map<String, Object> parasMap) {
        //根据产品id查询产品详情信息
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) parasMap.get("loanId"));
        if(loanInfo.getLeftProductMoney()<(Double) parasMap.get("bidMoney")){
            return "投资金额>剩余可投金额";
        }


        //从map对象中找LoanInfo对象，若没有就创建一个
        Integer loanId = (Integer)parasMap.get("loanId");

        LoanInfo loanInfo1 = map.get(loanId);
        if (loanInfo1==null){
            //根据id创建对象,只有id有值
            loanInfo1 = new LoanInfo((Integer) parasMap.get("loanId"));
            map.put((Integer) parasMap.get("loanId"),loanInfo1);
        }


        //0.添加version
        parasMap.put("version",loanInfo.getVersion());

        int num = 0;
        //同步代码块，锁使用当前产品对象
        synchronized (loanInfo1){
            //1、产品剩余可投金额减少,数据库未报错未更改自动事务失效，使用手动提交和回滚
            num = loanInfoMapper.updateLeftMoneyReduceForInvest(parasMap);
            if (num != 1) {
                //未修改成功回滚，返回错误信息
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "产品剩余可投金额减少失败";
                //throw new RuntimeException("产品剩余可投金额减少失败");
                //return;
            }

            //3、判断产品是否满标（卖光了）
            LoanInfo loanIfo = loanInfoMapper.selectByPrimaryKey((Integer) parasMap.get("loanId"));
            if (loanIfo.getLeftProductMoney() == 0d && loanIfo.getProductStatus() == 0) {
                loanIfo.setProductStatus(1);
                loanIfo.setProductFullTime(new Date());
                num = loanInfoMapper.updateByPrimaryKeySelective(loanIfo);
                if (num != 1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return "产品状态修改失败";
                }
            }
        }


        /**
         * 投资：
         *1、产品剩余可投金额减少
         *2、账户余额减少
         *3、判断产品是否满标（卖光了）
         *4、添加投资记录
         */



        //2、账户余额减少
        num = financeAccountMapper.updateMoneyReduceForInvest(parasMap);
        if (num != 1) {
            //未修改成功回滚，返回错误信息
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "账户余额减少失败";
            //throw new RuntimeException("账户余额减少失败");
        }



        //4、添加投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setBidMoney((Double) parasMap.get("bidMoney"));
        bidInfo.setBidStatus(1);
        bidInfo.setBidTime(new Date());
        bidInfo.setLoanId((Integer) parasMap.get("loanId"));
        bidInfo.setUid((Integer) parasMap.get("userId"));
        num = bidInfoMapper.insertSelective(bidInfo);
        if (num != 1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "投资记录添加失败";
        }

        return "ok";

    }
}
