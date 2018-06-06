## 简单教程


#### 1.引入Room数据库
```groovy
dependencies {
    api 'android.arch.persistence.room:runtime:1.1.0'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.0'
}
```

#### 2.添加Zoom的仓库地址.添加Zoom依赖
**latest-version** 是库的最新版本,请看标题的版本提示.

```
dependencies {
    api 'com.cicinnus.zoom:extend:latest-version'
    annotationProcessor 'com.cicinnus.zoom:compiler:latest-version'
}
```
#### 3.定义实体和Dao接口
定义实体
```java

@Entity(tableName = "t_user")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;
    //省略get set
}
```
**定义Dao接口,注意这里使用的是@DaoExtend注解**
```java

@DaoExtend(entity = UserEntity.class)
public interface UserDao {
//你没看错,除了@DaoExtend的定义,其他什么都不需要写

}

```
#### 4. Build -> make project .编译项目,自动生成代码

#### 5. 配置数据库类
```java

@Database(entities = {UserEntity.class}, version = 1)
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
    //注意这里的UserDaoExtend对象是编译后生成的接口对象.
    public abstract UserDaoExtend userDao();
```

#### 6.使用.注意数据库的操作必须在子线程.
> UserEntityCondition是编译后生成的对象.
```java
//插入
AppDatabase.getDatabase(context)
                    .userDao()
                    .insert(UserEntity);

//查询所有
AppDatabase
           .getDatabase(context)
           .userDao()
           .selectAll()

//对象查询
UserEntityCondition condition = new UserEntityCondition();

condition.createCriteria()
        .andLike("firstName", "zh")
        .andEqualTo("lastName", "rong")
        .limit(0,10);

List<UserEntity> userEntities = AppDatabase
                    .getDatabase(App.getInstance())
                    .userDao()
                    .selectByCondition(condition.build());
//etc.请查看相关代码和Sample
```


