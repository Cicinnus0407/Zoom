package com.cicinnus.zoom.extend.entity;

/**
 * 条件
 *
 * @author cicinnus
 * date 2018/6/1
 */
public class Criterion {

    /**
     * 条件
     * Condition
     */
    private String condition;

    /**
     * 搜索的值
     * Value For Search
     */
    private Object value;

    /**
     * and或者or的连接
     */
    private String andOr;

    /**
     * 没有连接符
     */
    private boolean noConnectSymbol;


    public Criterion(String condition, Object value, boolean isOr) {
        this.condition = condition;
        this.value = value;
        this.andOr = isOr ? " or " : " and ";
    }


    public Criterion(String condition) {
        this(condition, false);
    }

    public Criterion(String condition, boolean isOr) {
        this(condition, null, isOr);
    }


    public Criterion(String condition, Object value) {
        this(condition, value, false);
    }

    public String getCondition() {
        return condition;
    }

    public Object getValue() {
        return value == null ? "" : value;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

    public boolean isNoConnectSymbol() {
        return noConnectSymbol;
    }

    public void setNoConnectSymbol(boolean noConnectSymbol) {
        this.noConnectSymbol = noConnectSymbol;
    }
}
