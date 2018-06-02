package com.cicinnus.roomextend.base;


import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;


/**
 * Room的基类操作接口
 * <pre>
 * param <T> 泛型参数
 * author cicinnus
 * date 2018/5/13
 * </pre>
 */
public interface ZoomBaseDao<T> {


    /**
     * 插入实体
     *
     * @param t
     * @return
     */
    @Insert
    long insert(T t);

    /**
     * 插入一个集合
     *
     * @param list
     * @return
     */
    @Insert
    List<Long> insertList(List<T> list);

    /**
     * 删除实体
     *
     * @param t
     * @return rowId
     */
    @Delete
    int delete(T t);

    /**
     * 更新实体
     *
     * @param t
     * @return
     */
    @Update
    int update(T t);

    /**
     * 更新实体集合
     *
     * @param t
     * @return
     */
    @Update
    int updates(List<T> t);


}
