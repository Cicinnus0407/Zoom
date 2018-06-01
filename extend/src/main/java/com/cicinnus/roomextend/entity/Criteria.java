package com.cicinnus.roomextend.entity;

/**
 * author cicinnus
 * date 2018/6/1
 */
public abstract class Criteria extends AbstractCriteria {


    public Criteria(boolean notNull) {
        super(notNull);
    }

    @Override
    protected abstract String checkProperty(String property);

}
