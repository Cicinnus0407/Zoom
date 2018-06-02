### Zoom  [ ![Download](https://api.bintray.com/packages/cicinnus0407/Zoom/compiler/images/download.svg?version=1.0.2) ](https://bintray.com/cicinnus0407/Zoom/compiler/1.0.2/link)
[English DOC](README_EN.md)

基于Room的功能扩展库.

#### 关于Zoom
使用扩展之前必须先对Room的基础使用有了解.

在Room原有的基础上扩展了一些基础功能方便使用
类似于Retrofit和OkHttp的关系(水平有限.代码当然没有Retrofit那么优雅)


通过APT+JavaPoet.编译时生成代码.

#### Room存在的问题以及Zoom出现的原因

存在的问题:
- 虽然Room是一个ORM类型的数据库,但是对于数据库的操作基本都要通过原生SQL实现.

- 最基础的CRUD操作都需要定义一个Room提供的注解方式.

- 虽然对于@Insert,@Update,@Delete 的方法,能够在基类接口中定义.但是@query 需要具体的SQL语句.所以无法在基类仲定义

- @RawQuery注解可以提供执行sql语句功能,但是每次简单的SQL都要手写.很枯燥并且没有太大没意义

- 数据库升级非常麻烦.需要手动写很多SQL语句

如何解决:
- 通过基类接口+APT生成表操作常用的CRUD方法

- 编译时扫描实体对象,生成属性名和表列名的映射关系,生成条件搜索的对象,根据语义化的Java代码进行SQL查询


**Zoom出现就是为了解决Room使用不便的问题**


#### Zoom对象生成Java类名规则
- 被@DaoExtend注解的接口编译后会生成类名+Extend的对象.
> 如 UserDao -> UserDaoExtrend

- 数据库实体编译后会生成 类名+Condition对象.
> 如 UserEntity -> UserEntityCondition

---

### 主要功能:

- 添加了常用的基于对象的CRUD操作
  > 主键的Java类型会在编译期自动识别并生成对应参数类型
  - insert(T t);
  - selectById(Object Id);
  - delectBydi(Object id);
  - update(T t);
  - ..etc
- 基础分页查询功能
  - selectByPageAndRows(int page,int rows);
- 获取表数据条数
  - count();
- 基于属性的对象查询
  - andEqulaTo(property,value);
  - andLike(property,value);
  - andIsNotNull(property);
  - etc...
- 条件SQL查询
  - andCondition(SQL statement);
- 简化数据库升级功能(TODO)

### 基本使用步骤

#### 1.引入Room数据库
```groovy
dependencies {
    api 'android.arch.persistence.room:runtime:1.1.0'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.0'
}
```

#### 2.添加Zoom的仓库地址.添加Zoom依赖
**latest-version** 是库的最新版本,请看标题的版本提示.
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/cicinnus0407/Zoom"
    }
}
```
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
        .andEqualTo("lastName", "rong");

List<UserEntity> userEntities = AppDatabase
                    .getDatabase(App.getInstance())
                    .userDao()
                    .selectByCondition(condition.build());
//etc.请查看相关代码和Sample
```


