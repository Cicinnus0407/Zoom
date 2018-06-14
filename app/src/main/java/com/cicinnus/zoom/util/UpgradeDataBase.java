package com.cicinnus.zoom.util;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.cicinnus.zoom.entity.UserEntity;
import com.cicinnus.zoom.extend.annototaion.Upgrade;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/12
 * </pre>
 */
public class UpgradeDataBase {

    @Upgrade(dataBaseVersion = 2, upgradeEntities = {UserEntity.class})
    public static class MIGRATE_1_2 {
        public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                try {
//                    MigrationHelper_2.getInstance().migrate(database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

//    @Upgrade(schemasLocation = "app/schemas/com.cicinnus.zoom.db.AppDatabase/4.json", upgradeEntities = {UserEntity.class})
//    public static class MIGRATE_3_4 {
//        public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
//            @Override
//            public void migrate(@NonNull SupportSQLiteDatabase database) {
//                MigrationHelper_4.getInstance().migrate(database);
//            }
//        };
//    }

    public static Migration[] migrations() {
        return new Migration[]{MIGRATE_1_2.MIGRATION_1_2};
    }
}
