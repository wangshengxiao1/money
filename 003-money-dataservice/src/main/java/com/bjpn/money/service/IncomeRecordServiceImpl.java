package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.BidInfoMapper;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.IncomeRecordMapper;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.LoanInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 收益业务接口实现类
 */
@Service(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
@Component
@Transactional
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    LoanInfoMapper loanInfoMapper;
    @Autowired
    BidInfoMapper bidInfoMapper;
    @Autowired
    IncomeRecordMapper incomeRecordMapper;
    @Autowired
    FinanceAccountMapper financeAccountMapper;


    /**
     *
     *1、查询状态为1 的产品==List
     *2、遍历集合 ，查询该产品的投资记录==》List
     *3、遍历集合，投资记录生成收益计划
     *4、产品状态改为2
     */
    @Override
    public void generatePlan() {
        //查询状态为1的产品,就是已满标产品
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfosByFullStatus();
        //遍历集合
        for (LoanInfo loanInfo:loanInfos){
            //根据产品id查询投资记录，不带分页
            List<BidInfo> bidInfos = bidInfoMapper.selectBidInfoListByLoanId(loanInfo.getId());
            //遍历投资记录生成收益计划
            for (BidInfo bidInfo:bidInfos){
                //创建收益计划对象
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(bidInfo.getLoanId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                //存入时间和收益
                Date date = null;
                Double shouyi = null;
                //根据产品类型判断是否为新手宝计算日期和收益
                if (loanInfo.getProductType()==0){
                    date = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    shouyi = loanInfo.getRate()*bidInfo.getBidMoney()/100/365*loanInfo.getCycle();
                }else {
                    date = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    shouyi = loanInfo.getRate()*bidInfo.getBidMoney()/100/365*loanInfo.getCycle()*30;
                }

                incomeRecord.setIncomeDate(date);
                incomeRecord.setIncomeMoney(new BigDecimal(shouyi).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                incomeRecord.setIncomeStatus(0);
                int i = incomeRecordMapper.insertSelective(incomeRecord);
                if (i != 1){
                    //收益计划没有添加成功回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                }
            }

            //将产品状态改为2已生成收益计划
            loanInfo.setProductStatus(2);
            int num = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if (num != 1){
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

        }
    }

    /**
     * 收益返现
     * 1.查询用户收益记录表，查询收益状态为0且到期的收益记录
     * 2.遍历集合，将本金和利息添加到当前投资人账户余额中
     * 3.将收益状态0未返改为1已返
     */
    @Override
    public void cashBack() {
        //查询收益记录表
        List<IncomeRecord> incomeRecords = incomeRecordMapper.queryIncomeRecordByTimeAndStatu();
        for (IncomeRecord incomeRecord:incomeRecords){
            Double bidMoney = incomeRecord.getBidMoney();
            Double incomeMoney = incomeRecord.getIncomeMoney();
            //回报金额
            Double allMoney = bidMoney + incomeMoney;
            //将回报金额存入个人账户
            int num = financeAccountMapper.insertMoneyByBid(incomeRecord.getUid(),allMoney);
            if (num != 1){
                //返现金额存入失败手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            //返现完成将此条收益记录状态改为1，代表返现完成
            incomeRecord.setIncomeStatus(1);
            int i = incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
            if (i != 1){
                //状态未更改，手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }

    }

    /**
     * //根据用户id查询投资记录收益表，按时间降序
     * @param uid
     * @return
     */
    @Override
    public List<IncomeRecord> queryIncomeRecordByUid(Integer uid) {
        return incomeRecordMapper.selectIncomeRecordByUid(uid);
    }

    /**
     * 按投资金额降序的方式查询投资记录
     * @return
     */
    @Override
    public List<IncomeRecord> queryByMoney() {
        return incomeRecordMapper.selectByMoney();
    }

}
