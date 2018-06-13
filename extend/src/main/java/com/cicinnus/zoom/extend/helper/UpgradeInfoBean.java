package com.cicinnus.zoom.extend.helper;

import java.util.List;

/**
 * 数据库导出概要对应实体
 * <pre>
 * author cicinnus
 * date 2018/6/12
 * </pre>
 */
public class UpgradeInfoBean {

    /**
     * formatVersion : 1
     * database : {"version":3,"identityHash":"bb67c87cd644fd7b1ea700cb1eb77559","entities":[{"tableName":"t_user","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `first_name` TEXT, `last_name` TEXT)","fields":[{"fieldPath":"id","columnName":"id","affinity":"INTEGER","notNull":true},{"fieldPath":"firstName","columnName":"first_name","affinity":"TEXT","notNull":false},{"fieldPath":"lastName","columnName":"last_name","affinity":"TEXT","notNull":false}],"primaryKey":{"columnNames":["id"],"autoGenerate":true},"indices":[],"foreignKeys":[]},{"tableName":"PersonEntity","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` TEXT NOT NULL, `name` TEXT, `person_age` INTEGER NOT NULL, `height` REAL NOT NULL, PRIMARY KEY(`id`))","fields":[{"fieldPath":"personId","columnName":"id","affinity":"TEXT","notNull":true},{"fieldPath":"name","columnName":"name","affinity":"TEXT","notNull":false},{"fieldPath":"age","columnName":"person_age","affinity":"INTEGER","notNull":true},{"fieldPath":"height","columnName":"height","affinity":"REAL","notNull":true}],"primaryKey":{"columnNames":["id"],"autoGenerate":false},"indices":[],"foreignKeys":[]},{"tableName":"project_entity","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `project_name` TEXT, `user_id` INTEGER NOT NULL)","fields":[{"fieldPath":"id","columnName":"id","affinity":"INTEGER","notNull":true},{"fieldPath":"projectName","columnName":"project_name","affinity":"TEXT","notNull":false},{"fieldPath":"userId","columnName":"user_id","affinity":"INTEGER","notNull":true}],"primaryKey":{"columnNames":["id"],"autoGenerate":true},"indices":[],"foreignKeys":[]}],"setupQueries":["CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)","INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"bb67c87cd644fd7b1ea700cb1eb77559\")"]}
     */

    private int formatVersion;
    private DatabaseBean database;

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public DatabaseBean getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseBean database) {
        this.database = database;
    }

    public static class DatabaseBean {
        /**
         * version : 3
         * identityHash : bb67c87cd644fd7b1ea700cb1eb77559
         * entities : [{"tableName":"t_user","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `first_name` TEXT, `last_name` TEXT)","fields":[{"fieldPath":"id","columnName":"id","affinity":"INTEGER","notNull":true},{"fieldPath":"firstName","columnName":"first_name","affinity":"TEXT","notNull":false},{"fieldPath":"lastName","columnName":"last_name","affinity":"TEXT","notNull":false}],"primaryKey":{"columnNames":["id"],"autoGenerate":true},"indices":[],"foreignKeys":[]},{"tableName":"PersonEntity","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` TEXT NOT NULL, `name` TEXT, `person_age` INTEGER NOT NULL, `height` REAL NOT NULL, PRIMARY KEY(`id`))","fields":[{"fieldPath":"personId","columnName":"id","affinity":"TEXT","notNull":true},{"fieldPath":"name","columnName":"name","affinity":"TEXT","notNull":false},{"fieldPath":"age","columnName":"person_age","affinity":"INTEGER","notNull":true},{"fieldPath":"height","columnName":"height","affinity":"REAL","notNull":true}],"primaryKey":{"columnNames":["id"],"autoGenerate":false},"indices":[],"foreignKeys":[]},{"tableName":"project_entity","createSql":"CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `project_name` TEXT, `user_id` INTEGER NOT NULL)","fields":[{"fieldPath":"id","columnName":"id","affinity":"INTEGER","notNull":true},{"fieldPath":"projectName","columnName":"project_name","affinity":"TEXT","notNull":false},{"fieldPath":"userId","columnName":"user_id","affinity":"INTEGER","notNull":true}],"primaryKey":{"columnNames":["id"],"autoGenerate":true},"indices":[],"foreignKeys":[]}]
         * setupQueries : ["CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)","INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"bb67c87cd644fd7b1ea700cb1eb77559\")"]
         */

        private int version;
        private String identityHash;
        private List<EntitiesBean> entities;
        private List<String> setupQueries;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getIdentityHash() {
            return identityHash;
        }

        public void setIdentityHash(String identityHash) {
            this.identityHash = identityHash;
        }

        public List<EntitiesBean> getEntities() {
            return entities;
        }

        public void setEntities(List<EntitiesBean> entities) {
            this.entities = entities;
        }

        public List<String> getSetupQueries() {
            return setupQueries;
        }

        public void setSetupQueries(List<String> setupQueries) {
            this.setupQueries = setupQueries;
        }

        public static class EntitiesBean {
            /**
             * tableName : t_user
             * createSql : CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `first_name` TEXT, `last_name` TEXT)
             * fields : [{"fieldPath":"id","columnName":"id","affinity":"INTEGER","notNull":true},{"fieldPath":"firstName","columnName":"first_name","affinity":"TEXT","notNull":false},{"fieldPath":"lastName","columnName":"last_name","affinity":"TEXT","notNull":false}]
             * primaryKey : {"columnNames":["id"],"autoGenerate":true}
             * indices : []
             * foreignKeys : []
             */

            private String tableName;
            private String createSql;
            private PrimaryKeyBean primaryKey;
            private List<FieldsBean> fields;
            private List<?> indices;
            private List<?> foreignKeys;

            public String getTableName() {
                return tableName;
            }

            public void setTableName(String tableName) {
                this.tableName = tableName;
            }

            public String getCreateSql() {
                return createSql;
            }

            public void setCreateSql(String createSql) {
                this.createSql = createSql;
            }

            public PrimaryKeyBean getPrimaryKey() {
                return primaryKey;
            }

            public void setPrimaryKey(PrimaryKeyBean primaryKey) {
                this.primaryKey = primaryKey;
            }

            public List<FieldsBean> getFields() {
                return fields;
            }

            public void setFields(List<FieldsBean> fields) {
                this.fields = fields;
            }

            public List<?> getIndices() {
                return indices;
            }

            public void setIndices(List<?> indices) {
                this.indices = indices;
            }

            public List<?> getForeignKeys() {
                return foreignKeys;
            }

            public void setForeignKeys(List<?> foreignKeys) {
                this.foreignKeys = foreignKeys;
            }

            public static class PrimaryKeyBean {
                /**
                 * columnNames : ["id"]
                 * autoGenerate : true
                 */

                private boolean autoGenerate;
                private List<String> columnNames;

                public boolean isAutoGenerate() {
                    return autoGenerate;
                }

                public void setAutoGenerate(boolean autoGenerate) {
                    this.autoGenerate = autoGenerate;
                }

                public List<String> getColumnNames() {
                    return columnNames;
                }

                public void setColumnNames(List<String> columnNames) {
                    this.columnNames = columnNames;
                }
            }

            public static class FieldsBean {
                /**
                 * fieldPath : id
                 * columnName : id
                 * affinity : INTEGER
                 * notNull : true
                 */

                private String fieldPath;
                private String columnName;
                private String affinity;
                private boolean notNull;

                public String getFieldPath() {
                    return fieldPath;
                }

                public void setFieldPath(String fieldPath) {
                    this.fieldPath = fieldPath;
                }

                public String getColumnName() {
                    return columnName;
                }

                public void setColumnName(String columnName) {
                    this.columnName = columnName;
                }

                public String getAffinity() {
                    return affinity;
                }

                public void setAffinity(String affinity) {
                    this.affinity = affinity;
                }

                public boolean isNotNull() {
                    return notNull;
                }

                public void setNotNull(boolean notNull) {
                    this.notNull = notNull;
                }
            }
        }
    }
}
