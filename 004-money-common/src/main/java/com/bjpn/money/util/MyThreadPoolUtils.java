package com.bjpn.money.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类(单例)
 */
public class MyThreadPoolUtils<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyThreadPoolUtils.class);

    // 等待队列长度
    private static final int BLOCKING_QUEUE_LENGTH = 1000;
    // 闲置线程存活时间
    private static final int KEEP_ALIVE_TIME = 60;
    // 闲置线程存活时间单位
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    // 线程名称
    private static final String THREAD_NAME = "myPool";

    // 线程池执行器
    private static ThreadPoolExecutor executor;

    // 私有化构造子,阻止外部直接实例化对象
    private MyThreadPoolUtils() {
    }

    /**
     * 获取单例的线程池对象--单例的双重校验
     *
     * @return 线程池
     */
    public static ThreadPoolExecutor getThreadPool() {
        if (executor == null) {
            synchronized (MyThreadPoolUtils.class) {
                if (executor == null) {
                    // 获取处理器数量
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    // 根据cpu数量,计算出合理的线程并发数
                    int maximumPoolSize = cpuNum * 2 + 1;
                    //
                    executor = new ThreadPoolExecutor(
                            // 核心线程数
                            maximumPoolSize - 1,
                            // 最大线程数
                            maximumPoolSize,
                            // 活跃时间
                            KEEP_ALIVE_TIME,
                            // 活跃时间单位
                            KEEP_ALIVE_TIME_UNIT,
                            // 线程队列
                            new LinkedBlockingDeque<>(BLOCKING_QUEUE_LENGTH),
                            // 线程工厂
                            Executors.defaultThreadFactory(),
                            // 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略（这里可以自定义拒绝策略）
                            new ThreadPoolExecutor.AbortPolicy() {
                                @Override
                                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                    LOGGER.warn("线程等待队列已满，当前运行线程总数：{}，活动线程数：{}，等待运行任务数：{}",
                                            e.getPoolSize(),
                                            e.getActiveCount(),
                                            e.getQueue().size());
                                }
                            }
                    );
                }
            }
        }
        return executor;
    }
}