package com.cicinnus.zoom.extend.entity;

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
     * 值匹配
     * add a value equals condition
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,待匹配的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andEqualTo(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " = ", "'" + value + "'", property);
        return this;
    }


    /**
     * 值不等于
     * value not equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,不等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andNotEqualTo(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " <> ", "'" + value + "'", property);
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
        addAndCriterion(getColumn(property) + " like ", "'%" + value + "%'", property);
        return this;
    }


    /**
     * 小于
     * less than
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than or equal value ,小于值
     * @return AbstractCriteria
     */
    public AbstractCriteria andLessThan(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " < ", "'" + value + "'", property);
        return this;
    }

    /**
     * 小于等于
     * less than or equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than or equal to value,小于等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andLessThanOrEqualTo(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " <= ", "'" + value + "'", property);
        return this;
    }

    /**
     * 大于
     * greater than
     *
     * @param property entity property ,实体对象属性名
     * @param value    greater than value,大于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andGreaterThan(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " > ", "'" + value + "'", property);
        return this;
    }

    /**
     * 大于等于
     * greater than or equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    greater than or equal to  value,大于等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria andGreaterThanOrEqualTo(@NonNull String property, Object value) {
        addAndCriterion(getColumn(property) + " >= ", "'" + value + "'", property);
        return this;
    }

    /**
     * 数据在一个集合范围内
     * value in an Iterator
     *
     * @param property entity property ,实体对象属性名
     * @param values   Iterable values
     * @return AbstractCriteria
     */
    public AbstractCriteria andIn(@NonNull String property, Iterable values) {
        // in ('a','b','c')
        //composite all values
        StringBuilder realValueBuilder = new StringBuilder();
        for (Object value : values) {
            realValueBuilder.append("'")
                    .append(value)
                    .append("'")
                    .append(",");
        }
        String valueString = realValueBuilder.toString();
        String realValue = valueString.substring(0, valueString.length() - 1);

        addAndCriterion(getColumn(property) + " in ", "(" + realValue + ")", property);
        return this;
    }


    /**
     * 数据不在一个集合范围内
     * value not in an Iterator
     *
     * @param property entity property ,实体对象属性名
     * @param values   Iterable values
     * @return AbstractCriteria
     */
    public AbstractCriteria andNotIn(@NonNull String property, Iterable values) {
        // in ('a','b','c')
        //composite all values
        StringBuilder realValueBuilder = new StringBuilder();
        for (Object value : values) {
            realValueBuilder.append("'")
                    .append(value)
                    .append("'")
                    .append(",");
        }
        String valueString = realValueBuilder.toString();
        String realValue = valueString.substring(0, valueString.length() - 1);

        addAndCriterion(getColumn(property) + " not in ", "(" + realValue + ")", property);
        return this;
    }


    /**
     * 等于空
     * property is null
     *
     * @param property entity property ,实体对象属性名
     * @return AbstractCriteria
     */
    public AbstractCriteria andIsNull(@NonNull String property) {
        addAndCriterion(getColumn(property) + " is null");
        return this;
    }


    /**
     * 不等于空
     * property is not null
     *
     * @param property entity property ,实体对象属性名
     * @return AbstractCriteria
     */
    public AbstractCriteria andIsNotNull(@NonNull String property) {
        addAndCriterion(getColumn(property) + " is not null");
        return this;
    }


    /**
     * 两个值之间
     * between value boundary
     *
     * @param property entity property ,实体对象属性名
     * @param value1   left boundary
     * @param value2   right boundary
     * @return AbstractCriteria
     */
    public AbstractCriteria andBetween(@NonNull String property, Object value1, Object value2) {
        addAndCriterion(getColumn(property) + " between " + "'" + value1 + "'" + " and " + "'" + value2 + "'");
        return this;
    }


    /**
     * 不在两个值之间
     * not between value boundary
     *
     * @param property entity property ,实体对象属性名
     * @param value1   left boundary
     * @param value2   right boundary
     * @return AbstractCriteria
     */
    public AbstractCriteria andNotBetween(@NonNull String property, Object value1, Object value2) {
        addAndCriterion(getColumn(property) + " not between " + "'" + value1 + "'" + " and " + "'" + value2 + "'");
        return this;
    }


    /**
     * 直接通过sql进行搜索
     *
     * @param condition sql query condition ,like 'and last_name = roger',条件搜索语句
     * @return AbstractCriteria
     */
    public AbstractCriteria andCondition(String condition) {
        addAndCriterion(condition);
        return this;
    }


    /**
     * 或者等于
     * or equal
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,待匹配的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orEqualTo(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " = ", "'" + value + "'", property);
        return this;
    }


    /**
     * 或者值不等于
     * or value not equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    matching value ,不等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orNotEqualTo(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " <> ", "'" + value + "'", property);
        return this;
    }


    /**
     * 或者模糊匹配
     *
     * @param property entity property ,实体对象属性名
     * @param value    fuzzy search value ,待匹配的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orLike(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " like ", "'%" + value + "%'", property);
        return this;
    }


    /**
     * 或者小于
     * or less than
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than or equal value ,小于值
     * @return AbstractCriteria
     */
    public AbstractCriteria orLessThan(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " < ", "'" + value + "'", property);
        return this;
    }

    /**
     * 或者小于等于
     * or less than or equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    less than or equal to value,小于等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orLessThanOrEqualTo(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " <= ", "'" + value + "'", property);
        return this;
    }

    /**
     * 或者大于
     * or greater than
     *
     * @param property entity property ,实体对象属性名
     * @param value    greater than value,大于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orGreaterThan(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " > ", "'" + value + "'", property);
        return this;
    }

    /**
     * 或大于等于
     * or greater than or equal to
     *
     * @param property entity property ,实体对象属性名
     * @param value    greater than or equal to  value,大于等于的值
     * @return AbstractCriteria
     */
    public AbstractCriteria orGreaterThanOrEqualTo(@NonNull String property, Object value) {
        addOrCriterion(getColumn(property) + " >= ", "'" + value + "'", property);
        return this;
    }

    /**
     * 或数据在一个集合范围内
     * or value in an Iterator
     *
     * @param property entity property ,实体对象属性名
     * @param values   Iterable values
     * @return AbstractCriteria
     */
    public AbstractCriteria orIn(@NonNull String property, Iterable values) {
        // in ('a','b','c')
        //composite all values
        StringBuilder realValueBuilder = new StringBuilder();
        for (Object value : values) {
            realValueBuilder.append("'")
                    .append(value)
                    .append("'")
                    .append(",");
        }
        String valueString = realValueBuilder.toString();
        String realValue = valueString.substring(0, valueString.length() - 1);

        addOrCriterion(getColumn(property) + " in ", "(" + realValue + ")", property);
        return this;
    }


    /**
     * 或数据不在一个集合范围内
     * or value not in an Iterator
     *
     * @param property entity property ,实体对象属性名
     * @param values   Iterable values
     * @return AbstractCriteria
     */
    public AbstractCriteria orNotIn(@NonNull String property, Iterable values) {
        // in ('a','b','c')
        //composite all values
        StringBuilder realValueBuilder = new StringBuilder();
        for (Object value : values) {
            realValueBuilder.append("'")
                    .append(value)
                    .append("'")
                    .append(",");
        }
        String valueString = realValueBuilder.toString();
        String realValue = valueString.substring(0, valueString.length() - 1);

        addOrCriterion(getColumn(property) + " not in ", "(" + realValue + ")", property);
        return this;
    }


    /**
     * 或等于空
     * or property is null
     *
     * @param property entity property ,实体对象属性名
     * @return AbstractCriteria
     */
    public AbstractCriteria orIsNull(@NonNull String property) {
        addOrCriterion(getColumn(property) + " is null");
        return this;
    }


    /**
     * 不等于空
     * property is not null
     *
     * @param property entity property ,实体对象属性名
     * @return AbstractCriteria
     */
    public AbstractCriteria orIsNotNull(@NonNull String property) {
        addOrCriterion(getColumn(property) + " is not null");
        return this;
    }


    /**
     * 两个值之间
     * between value boundary
     *
     * @param property entity property ,实体对象属性名
     * @param value1   left boundary
     * @param value2   right boundary
     * @return AbstractCriteria
     */
    public AbstractCriteria orBetween(@NonNull String property, Object value1, Object value2) {
        addOrCriterion(getColumn(property) + " between " + "'" + value1 + "'" + " and " + "'" + value2 + "'");
        return this;
    }


    /**
     * 不在两个值之间
     * not between value boundary
     *
     * @param property entity property ,实体对象属性名
     * @param value1   left boundary
     * @param value2   right boundary
     * @return AbstractCriteria
     */
    public AbstractCriteria orNotBetween(@NonNull String property, Object value1, Object value2) {
        addOrCriterion(getColumn(property) + " not between " + "'" + value1 + "'" + " and " + "'" + value2 + "'");
        return this;
    }


    /**
     * 或直接通过sql进行搜索
     *
     * @param condition sql query condition ,like 'or last_name = roger',条件搜索语句
     * @return AbstractCriteria
     */
    public AbstractCriteria orCondition(String condition) {
        addOrCriterion(condition);
        return this;
    }


    /**
     * 分页.后续不能继续拼接
     * paging list . should be the last condition.
     *
     * @param page paging page num
     * @param rows paging rows num
     */
    public void limit(int page, int rows) {
        String limitSql = " limit " + page * rows + "," + rows;
        addCriterionWithoutConnetSymbol(limitSql);
    }

    /**
     * 根据字段倒序
     * order by properties desc,
     *
     * @param properties
     */
    public AbstractCriteria orderByDesc(@NonNull String... properties) {
        StringBuilder orderBuilder = new StringBuilder();
        for (String property : properties) {
            orderBuilder.append("'")
                    .append(checkProperty(property))
                    .append("'")
                    .append(",");
        }
        String orderToString = orderBuilder.toString();
        String orderParams = orderToString.substring(0, orderBuilder.length() - 1);
        String orderSql = " order by " + orderParams + " desc";
        addCriterionWithoutConnetSymbol(orderSql);
        return this;
    }

    /**
     * 根据字段倒序
     * order by properties asc,
     *
     * @param properties
     */
    public AbstractCriteria orderByAsc(@NonNull String... properties) {
        StringBuilder orderBuilder = new StringBuilder();
        for (String property : properties) {
            orderBuilder.append("'")
                    .append(checkProperty(property))
                    .append("'")
                    .append(",");
        }
        String orderToString = orderBuilder.toString();
        String orderParams = orderToString.substring(0, orderBuilder.length() - 1);
        String orderSql = " order by " + orderParams + "asc";
        addCriterionWithoutConnetSymbol(orderSql);
        return this;
    }


    /**
     * 添加一个条件
     * add an 'and' query condition with raw condition
     *
     * @param condition
     */
    protected void addAndCriterion(@NonNull String condition) {
        if (condition.startsWith("null")) {
            return;
        }
        criterionList.add(new Criterion(condition));
    }


    /**
     * 添加一个或条件
     * add an 'and' query condition with raw condition
     *
     * @param condition
     */
    protected void addOrCriterion(@NonNull String condition) {
        if (condition.startsWith("null")) {
            return;
        }
        criterionList.add(new Criterion(condition, true));
    }

    /**
     * 添加搜索条件
     * add query condition with property
     *
     * @param condition query condition,搜索条件
     * @param value     query value ,匹配值
     * @param property  entity property ,对象属性
     */
    protected void addAndCriterion(@NonNull String condition, Object value, String property) {
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
     * 添加搜索条件
     * add query condition with property
     *
     * @param condition query condition,搜索条件
     * @param value     query value ,匹配值
     * @param property  entity property ,对象属性
     */
    protected void addOrCriterion(@NonNull String condition, Object value, String property) {
        if (value == null) {
            if (notNull) {
                throw new IllegalArgumentException("Value for " + property + " cannot be null");
            } else {
                return;
            }
        }
        criterionList.add(new Criterion(condition, value, true));
    }

    /**
     * 添加一个没有连接符的语句
     * add query condition without connect symbol.
     *
     * @param condition
     */
    protected void addCriterionWithoutConnetSymbol(@NonNull String condition) {

        Criterion criterion = new Criterion(condition);
        criterion.setNoConnectSymbol(true);
        criterionList.add(criterion);
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

    /**
     * 查询语句集合
     * criterion List
     *
     * @return
     */
    public List<Criterion> getCriterionList() {
        return criterionList;
    }
}
