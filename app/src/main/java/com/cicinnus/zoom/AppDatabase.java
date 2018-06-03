package com.cicinnus.zoom;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cicinnus.zoom.dao.PersonDaoExtend;
import com.cicinnus.zoom.dao.UserDaoExtend;
import com.cicinnus.zoom.entity.PersonEntity;
import com.cicinnus.zoom.entity.UserEntity;


/**
 * 数据库对象,一般情况下初始化为单例
 *
 * @author cicinnus
 * @date 2018/5/13
 */
@Database(entities = {UserEntity.class, PersonEntity.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;


    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, "db-test.db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return sInstance;
    }

    public static void onDestroy() {
        sInstance = null;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `PersonEntity` (`id` TEXT NOT NULL, `name` TEXT, `person_age` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        }
    };


    public abstract UserDaoExtend userDao();

    public abstract PersonDaoExtend personDao();
}
