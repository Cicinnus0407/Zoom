package com.cicinnus.roomextend.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础的搜索功能.APT生成子类,实现具体的生成语句
 * author cicinnus
 * date 2018/6/1
 */
public abstract class BaseQueryCondition {


    /**
     * 值是否可以为null
     * whether value could be null
     */
    protected boolean notNull;


    /**
     * 属性和列名的映射集合
     * entity property with table column map
     */
    protected Map<String, String> mPropertyMap = new HashMap<>();

    /**
     * 搜索条件对象
     */
    private GenericCriteria mCriteria;


    public BaseQueryCondition(boolean notNull) {
        this.notNull = notNull;
    }

    /**
     * 创建一个搜索对象
     *
     * @return
     */
    public GenericCriteria createCriteria() {

        mCriteria = new GenericCriteria(notNull, mPropertyMap) {
            @Override
            protected String checkProperty(String property) {
                return checkPropertyByAPT(property);
            }
        };
        return mCriteria;
    }


    /**
     * 判断属性方法是否存在交由APT生成,因为编译期能拿到对应的类名
     * code implement by subclass that was generate by APT , cause during building could fetch the entity
     *
     * @param property
     * @return
     */
    protected abstract String checkPropertyByAPT(String property);

    /**
     * 生成sql的条件语句
     * assemble all the query condition
     *
     * @return
     */
    protected String generateConditionSQL() {

        StringBuilder sqlBuilder = new StringBuilder();

        for (Criterion criterion : mCriteria.getCriterianList()) {
            sqlBuilder.append(criterion.getAndOr())
                    .append(criterion.getCondition())
                    .append(criterion.getValue());
        }
        System.out.println("======" + sqlBuilder.toString() + "");
        return sqlBuilder.toString();

    }

}
