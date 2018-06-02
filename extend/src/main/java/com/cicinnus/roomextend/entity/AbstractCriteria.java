package com.cicinnus.roomextend.entity;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽象的查询类,提供基础的语义化查询方法
 * abstract criteria ,provide the semantic query method
 * author cicinnus
 * date 2018/6/1
 */
public abstract class AbstractCriteria {

    private List<Criterion> criterionList;
    /**
     * 值是否不能为空
     */
    private boolean notNull;

    /**
     * 属性名与表字段对应的HashMap
     */
    protected Map<String, String> mPropertyMap;


    public AbstractCriteria(boolean notNull, Map<String, String> propertyMap) {
        this.notNull = notNull;
        this.mPropertyMap = propertyMap;
        criterionList = new ArrayList<>();
    }

    /**
     * 添加一个值匹配
     * add a value equals condition
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,待匹配的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andEqualTo(@NonNull String property, Object value) {
        addCriterion(getColumn(property) + " = ", "'" + value + "'", property);
        return this;
    }

    /**
     * 模糊搜索
     *
     * @param property entity property ,实体对象属性名
     * @param value    fuzzy search value ,待匹配的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andLike(@NonNull String property, Object value) {
        addCriterion(getColumn(property) + " like ", "'%" + value + "%'", property);
        return this;
    }


    /**
     * 小于
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than value
     * @return AbstractCriteria
     */
    public AbstractCriteria andLessThan(@NonNull String property, Object value) {
        addCriterion(getColumn(property) + " < ", "'" + value + "'", property);
        return this;
    }

    /**
     * 小于等于
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than or equal to value
     * @return AbstractCriteria
     */
    public AbstractCriteria andLessThanOrEqualTo(@NonNull String property, Object value) {
        addCriterion(getColumn(property) + " <= ", "'" + value + "'", property);
        return this;
    }


    /**
     * 直接通过sql进行搜索
     *
     * @param condition sql query condition ,like 'and last_name = roger',条件搜索语句
     * @return AbstractCriteria
     */
    public AbstractCriteria andCondition(String condition) {
        addCriterion(condition);
        return this;
    }


    /**
     * 添加一个条件
     * add query condition with raw condition
     *
     * @param condition
     */
    protected void addCriterion(@NonNull String condition) {
        if (condition.startsWith("null")) {
            return;
        }
        criterionList.add(new Criterion(condition));
    }

    /**
     * 添加搜索条件
     * add query condition with property
     *
     * @param condition query condition,搜索条件
     * @param value     query value ,匹配值
     * @param property  entity property ,对象属性
     */
    protected void addCriterion(@NonNull String condition, Object value, String property) {
        if (value == null) {
            if (notNull) {
                throw new IllegalArgumentException("Value for " + property + " cannot be null");
            } else {
                return;
            }
        }
        criterionList.add(new Criterion(condition, value));
    }


    /**
     * 检查是否property存在
     * Check the Entity property whether notNull
     *
     * @param property
     */
    protected abstract String checkProperty(String property);

    /**
     * 获取属性名对应的列名
     * get the Table column name match with entity property
     *
     * @param property entity property ,实体对象名称
     * @return column name,数据库列名
     */
    private String getColumn(@NonNull String property) {
        checkProperty(property);
        return mPropertyMap.get(property);
    }

    public List<Criterion> getCriterianList() {
        return criterionList;
    }
}
