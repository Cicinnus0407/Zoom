package com.cicinnus.zoom.util;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.cicinnus.zoom.entity.UserEntity;
import com.cicinnus.zoom.extend.annototaion.Upgrade;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/12
 * </pre>
 */
public class UpgradeDataBase {

    @Upgrade(schemasLocation = "app/schemas/com.cicinnus.zoom.AppDatabase/3.json", upgradeEntities = {})
    public static class MIGRATE_2_3 {
        public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                try {
                    MigrationHelper_3.getInstance().migrate(database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Upgrade(schemasLocation = "app/schemas/com.cicinnus.zoom.AppDatabase/4.json", upgradeEntities = {UserEntity.class})
    public static class MIGRATE_3_4 {
        public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                MigrationHelper_4.getInstance().migrate(database);
            }
        };
    }

    public static Migration[] migrations(){
        return new Migration[]{MIGRATE_2_3.MIGRATION_2_3,MIGRATE_3_4.MIGRATION_3_4};
    }
}
