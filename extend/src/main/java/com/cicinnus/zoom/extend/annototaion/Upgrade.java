package com.cicinnus.zoom.extend.annototaion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/12
 * </pre>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Upgrade {
    /**
     * 数据库版本,与@DataBase中的version一致
     *
     * @return
     */
    int dataBaseVersion();

    /**
     * 需要升级的类
     *
     * @return
     */
    Class[] upgradeEntities();


}
