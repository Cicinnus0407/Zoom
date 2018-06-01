package com.cicinnus.zoom;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;

import com.cicinnus.roomextend.entity.QueryCondition;

/**
 * author cicinnus
 * date 2018/6/1
 */
public class EntityCondition extends QueryCondition {


    public EntityCondition() {
    }

    @Override
    protected void checkProperty(String property) {

    }

    /**
     * 获取sql查询语句
     *
     * @return
     */
    public SupportSQLiteQuery getQuery() {
        StringBuilder sqlBuilder = new StringBuilder("select * from t_user where 1 = 1 ");
        //拼接匹配查询
        for (String key : equalsMap.keySet()) {
            sqlBuilder.append(" and ")
                    .append(key)
                    .append(" = ")
                    .append("'")
                    .append(equalsMap.get(key))
                    .append("'");
        }
        //模糊匹配
        for (String likeKey : likesMap.keySet()) {
            sqlBuilder
                    .append(" and ")
                    .append(likeKey)
                    .append("%")
                    .append(likesMap.get(likeKey))
                    .append("% ");

        }

        //拼接模糊查询 select * from t_user where  first_name like %%

        return new SimpleSQLiteQuery(sqlBuilder.toString());
    }
}
