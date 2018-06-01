package com.cicinnus.roomextend.entity;

import java.util.Map;

/**
 * author cicinnus
 * date 2018/6/1
 */
public abstract class BaseQueryCondition {


    protected boolean notNull;


    protected Map<String, String> propertyToColumnMap;
    protected Criteria criteria;


    public BaseQueryCondition(boolean notNull) {
        this.notNull = notNull;
        criteria = createCriteriaInternal();
    }


    public Criteria createCriteria() {
        if (criteria == null) {
            createCriteriaInternal();
        }
        return criteria;
    }


    protected Criteria createCriteriaInternal() {
        criteria = new Criteria(notNull) {
            @Override
            protected String checkProperty(String property) {
                return checkPropertyByAPT(property);
            }
        };
        propertyToColumnMap = criteria.getPropertyColumn();
        return criteria;
    }

    protected abstract String checkPropertyByAPT(String property);

    //
    protected String getSQL() {



        StringBuilder sqlBuilder = new StringBuilder();

        for (Criterion criterion : criteria.getCriterianList()) {
            sqlBuilder.append(criterion.getAndOr())
                    .append(criterion.getCondition())
                    .append(criterion.getValue());

        }
        System.out.println("======" + sqlBuilder.toString() + "");
        return sqlBuilder.toString();

    }

}
