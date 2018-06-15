## 数据库升级使用教程

数据库升级流程

![数据库升级流程图](./capture/migration_flow.png)


### 需要手动调用处理的情况
原因:
    表修改了,但是自动生成代码的时候无法确定是哪个表进行了修改,只能手动指定新旧表名<br>
    表删除,即实体的@Entity注解被删掉,或者是整个类被删掉,但是自动生成代码的时候无法得知,所以需要手动指定删除的表

- 1.表名修改后使用的对应方法
> MigrationHelper_x.renameTable(database,"oldeTableName","newTableName");
- 2.删除表后使用的对应方法
> MigrationHelper_x.dropTableByName(database,"tableName",true);


### 注意事项:
1.数据库升级的方法依赖于Room的Schemae导出功能

1.1 设置schema导出路径,(路径不强制,但是要保证输出后的文件能够被正常识别,所以建议还是使用Room建议的默认配置)
```
defaultConfig {
 ...
 javaCompileOptions {
      annotationProcessorOptions {
          arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]
      }
  }
}
```

1.2@Database的exportSchema值必须为true(默认即是true)
```
@Database(entities = {...}, version = 1, exportSchema = true)
```

2.请勿删除schema下的json文件,把文件小心保管好.当成工程的一部分.

3.使用git或svn等协同工具,请提交json文件,否则会除本机外无法正常使用生成帮助类

4.升级帮助类内部没有对异常进行处理,请处理好传入的参数,以免发生出错

5.建议在正式使用之前,仔细查看流程图.

### x.json文件误删后的操作.
- 1.注释掉@Upgrade注解
- 2.AS -> Build -> Make Project
- 3.恢复@Upgrade
- 4.AS -> Build -> Make Project
- 5.查看帮助类是否正常生成
> 这种恢复操作只能恢复最新版的帮助类.所以请谨慎保管好x.json文件,否则容易导致数据库升级后内容丢失.

### 使用示例
使用场景
- 当前数据库版本为1.需要升级到2
- 在升级过程中,添加了@Entity注解的类(即希望创建新的表)
- 在升级过程中,对原有的实体进行了修改(即修改了表的列(新增,修改,删除))


#### 1.升级数据库(添加/删除字段,新增实体等),修改数据库版本
```
@Database(entities = {...}, version = 2, exportSchema = true)

```

#### 2.编译(build)在schema文件夹生成x.json文件.
AS -> Build -> Make Project

#### 3.创建一个类.添加@Upgrade注解,添加修改过字段的类
> upgradeEntities应该只包含表结构改变的实体类.如果是其他类型的修改,请参考顶部的流程图.查看需要使用的方法
**注意版本dataBaseVersion和@Database的version保持一致,这样才能正确获取到导出的json文件.**
```
@Upgrade(dataBaseVersion = 2, upgradeEntities = {...})
    public static class MIGRATE_1_2 {

}
```

#### 4.编译代码,自动生成数据库升级帮助类,生成的帮助类类名为MigrationHelper_x,其中x是dataBaseVersion的参数值
AS -> Build -> Make Project
> 编译前,请确保schema文件已经导出.否则会提示无法找到文件的错误.
例如
```
MigrationHelper_2
```

#### 5.创建Room升级的必要方法,调用升级帮助类的API.MigrationHelper_x.migrate(database);
```
 public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                MigrationHelper_2.getInstance().migrate(database);
            }
        };
```

#### 完整代码
```
public class UpgradeDataBase {

    @Upgrade(dataBaseVersion = 2, upgradeEntities = {UserEntity.class})
    public static class MIGRATE_1_2 {
        public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                MigrationHelper_2.getInstance().migrate(database);
            }
        };
    }

    public static Migration[] migrations() {
        return new Migration[]{MIGRATE_1_2.MIGRATION_1_2};
    }
}
```
//添加Room升级的addMigrations
```
@Database(entities = {UserEntity.class, PersonEntity.class}
        , version = 2, exportSchema = true)
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
```