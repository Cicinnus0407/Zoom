package com.cicinnus.zoom.util;

import com.cicinnus.zoom.extend.Zoom;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/13
 * </pre>
 */
public class SQLUtil {


    /**
     * 输出SQL语句
     *
     * @param builder
     * @param log
     */
    public static MethodSpec.Builder showLog(MethodSpec.Builder builder, String log) {
        ClassName androidLog = ClassName.get("android.util", "Log");
        ClassName zoom = ClassName.get(Zoom.class);

        builder.addStatement("if ($T.SHOW_SQL) {\n$T.d(\"Zoom \", " + log + ")", zoom, androidLog)
                //拼接SimpleSQLiteQuery
                .addCode("}\n");
        return builder;
    }
}
