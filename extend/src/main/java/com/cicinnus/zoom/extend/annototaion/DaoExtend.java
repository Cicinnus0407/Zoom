package com.cicinnus.zoom.extend.annototaion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解,增强Room
 * <pre>
 *
 * author cicinnus
 * date 2018/5/13
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DaoExtend {


    /**
     * 实体类
     *
     * @return 实体类Class
     */
    Class entity();
}
