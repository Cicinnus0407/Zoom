package com.cicinnus.zoom.dao;

import android.arch.persistence.room.Query;

import com.cicinnus.zoom.entity.PersonEntity;
import com.cicinnus.zoom.extend.annototaion.DaoExtend;

import java.util.List;

/**
 * Dao接口
 * <pre>
 * author cicinnus
 * date 2018/6/3
 * </pre>
 */
@DaoExtend(entity = PersonEntity.class)
public interface PersonDao {
}
