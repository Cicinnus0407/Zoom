package com.cicinnus.roomextend.entity;

/**
 * 条件
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


    public Criterion(String condition, Object value, boolean isOr) {
        this.condition = condition;
        this.value = value;
        this.andOr = isOr ? " or " : " and ";
    }


    public Criterion(String condition) {
        this(condition, false);
    }


    public Criterion(String condition, Object value) {
        this(condition, value, false);
    }

    public String getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }

    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }
}
