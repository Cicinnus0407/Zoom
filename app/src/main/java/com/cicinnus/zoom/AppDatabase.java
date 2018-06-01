package com.cicinnus.zoom;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


/**
 * 数据库对象,一般情况下初始化为单例
 *
 * @author cicinnus
 * @date 2018/5/13
 */
@Database(entities = {UserEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;


    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class,
                            "db-test.db").build();
                }
            }
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }

    public abstract UserDaoExtend userDao();
}
