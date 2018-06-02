package com.cicinnus.roomextend.entity;

import java.util.Map;

/**
 * 继承自抽象的条件搜索类.
 * extend from abstract criteria class
 * author cicinnus
 * date 2018/6/1
 */
public abstract class GenericCriteria extends AbstractCriteria {


    public GenericCriteria(boolean notNull, Map<String, String> propertyMap) {
        super(notNull, propertyMap);
    }

}
