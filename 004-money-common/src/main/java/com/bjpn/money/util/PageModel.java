package com.bjpn.money.util;

import java.io.Serializable;

/**
 * 分页模型
 *
 * 共20条3页　当前为第2页　 首页 上一页 下一页 尾页:
 * 不变：总记录数  总页数  首页   当前页   尾页   一页显示多少条===》Map==面向对象编程思想==》对象，形成分页模型（PageModel）
 * 变：上一页 下一页
 *
 * 分析  抽象  封装
 *
 * 第一页： 0-9        （1-1）*10  10
 * 第二页： 10-19       （2-1）*10  10
 * 第三页： 20-29
 *
 * firstPage 首页
 * PageSize 步长
 * totalCount 总记录数据
 * totalPage 总页数
 * cunPage 当前页
 *
 *
 */

public class PageModel implements Serializable {

    private Integer firstPage=1;
    private Integer PageSize=10;
    private Long totalCount;
    private Long totalPage;
    private Long cunPage;

    public PageModel(Integer pageSize) {
        PageSize = pageSize;
    }

    public PageModel() {
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public Long getCunPage() {
        return cunPage;
    }

    public void setCunPage(Long cunPage) {
        this.cunPage = cunPage;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "firstPage=" + firstPage +
                ", PageSize=" + PageSize +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", cunPage=" + cunPage +
                '}';
    }
}
