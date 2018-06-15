package com.cicinnus.zoom.ext;

import com.squareup.javapoet.ClassName;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/14
 * </pre>
 */
public class ZoomNameType {

    /**
     * 数据库
     */
    public static final ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");

    /**
     * TableInfo
     */
    public static final ClassName tableInfo = ClassName.get("android.arch.persistence.room.util", "TableInfo");


}
