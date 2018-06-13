package com.cicinnus.zoom;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cicinnus.zoom.dao.PersonDaoExtend;
import com.cicinnus.zoom.dao.UserDaoExtend;
import com.cicinnus.zoom.entity.PersonEntity;
import com.cicinnus.zoom.entity.TestEntity;
import com.cicinnus.zoom.entity.UserEntity;
import com.cicinnus.zoom.util.UpgradeDataBase;


/**
 * 数据库对象,一般情况下初始化为单例
 *
 * @author cicinnus
 * @date 2018/5/13
 */
@Database(entities = {UserEntity.class, PersonEntity.class, TestEntity.class}
        , version = 4, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;


    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, "db-test.db")
                            .addMigrations(UpgradeDataBase.migrations())
                            .build();
                }
            }
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }

    public abstract UserDaoExtend userDao();

    public abstract PersonDaoExtend personDao();
}
