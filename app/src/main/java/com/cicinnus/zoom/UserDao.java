package com.cicinnus.zoom;

import android.arch.persistence.room.Query;

import com.cicinnus.roomextend.annototaion.DaoExtend;

import java.util.List;

@DaoExtend(entity = UserEntity.class)
public interface UserDao {

    @Query("select * from t_user where first_name >5  ")
    List<UserEntity> list();

}
