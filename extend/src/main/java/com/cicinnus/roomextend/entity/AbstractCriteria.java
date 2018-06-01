package com.cicinnus.roomextend.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
     * 连接条件
     */
    private String andOr;

    /**
     * 属性名与表字段对应的HashMap
     */
    protected HashMap<String, String> propertyToColumnMap = new HashMap<>();


    public AbstractCriteria(boolean notNull) {
        this.notNull = notNull;
        criterionList = new ArrayList<>();
    }

    /**
     * 添加一个值匹配
     * add a value equals condition
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,待匹配的值
     * @return Condition
     */
    public AbstractCriteria andEqualTo(String property, Object value) {
        addCriterion(getColumn(property) + " = ", "'" + value + "'", property);
        return  this;
    }

    /**
     * 模糊搜索
     *
     * @param property entity property ,实体对象属性名
     * @param value    fuzzy search value ,待匹配的值
     * @return Condition
     */
    public AbstractCriteria andLike(String property, Object value) {
        addCriterion(getColumn(property) + " like ", "'%" + value + "%'", property);
        return (Criteria) this;
    }


    public AbstractCriteria andCondition(String condition) {
        addCriterion(condition);
        return this;
    }


    /**
     * 添加一个条件
     * add query condition
     *
     * @param condition
     */
    protected void addCriterion(String condition) {

        if (condition == null) {
            throw new IllegalArgumentException(" condition cannot be null");
        }
        if (condition.startsWith("null")) {
            return;
        }
        criterionList.add(new Criterion(condition));
    }

    protected void addCriterion(String condition, Object value, String property) {
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
    private String getColumn(String property) {
        checkProperty(property);
        return propertyToColumnMap.get(property);
    }


    public String getAndOr() {
        return andOr;
    }

    public void setAndOr(String andOr) {
        this.andOr = andOr;
    }

    public List<Criterion> getCriterianList() {
        return criterionList;
    }

    public Map<String, String> getPropertyColumn() {
        return propertyToColumnMap;
    }


}
