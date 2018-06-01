package com.cicinnus.roomextend.entity;

import java.util.HashMap;
import java.util.List;

/**
 * author cicinnus
 * date 2018/6/1
 */
public abstract class QueryCondition extends GeneratedCriteria {


    /**
     * 属性名与表字段对应的HashMap
     */
    protected HashMap<String, String> propertyToColumnMap = new HashMap<>();


    /**
     * 需要匹配查询的搜索map
     */
    protected HashMap<String, Object> equalsMap = new HashMap<>();


    /**
     * 模糊查询的搜索map
     */
    protected HashMap<String, Object> likesMap = new HashMap<>();


    /**
     * 大于的搜索map
     */
    protected HashMap<String, Object> gtMap = new HashMap<>();
    /**
     * 小于的搜索map
     */
    protected HashMap<String, Object> ltMap = new HashMap<>();

    /**
     * 值等于
     *
     * @param property
     * @param value
     * @return
     */
    public QueryCondition andEqualTo(String property, Object value) {
        checkProperty(property);
        String tableColumn = propertyToColumnMap.get(property);
        equalsMap.put(tableColumn, value);
        return this;
    }


    /**
     * 模糊匹配
     *
     * @param property
     * @param value
     * @return
     */
    public QueryCondition andLikeTo(String property, Object value) {
        checkProperty(property);
        String tableColumn = propertyToColumnMap.get(property);
        likesMap.put(tableColumn, value);
        return this;
    }

    /**
     * 大于匹配
     *
     * @param property
     * @param value
     * @return
     */
    public QueryCondition andGtTo(String property, Object value) {
        checkProperty(property);
        String tableColumn = propertyToColumnMap.get(property);
        gtMap.put(tableColumn, value);
        return this;
    }


    /**
     * 小于匹配
     *
     * @param property
     * @param value
     * @return
     */
    public QueryCondition andLtTo(String property, Object value) {
        checkProperty(property);
        String tableColumn = propertyToColumnMap.get(property);
        ltMap.put(tableColumn, value);
        return this;
    }


    protected abstract void checkProperty(String property);


    protected static class GeneratedCriteria {


    }

    protected static class Criterion {


    }
}
