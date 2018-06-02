### Zoom

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
- 对于@Insert,@Update,@Delete 的方法,能够在基类接口中定义.但是@query不行.
- @RawQuery注解可以提供执行sql语句功能,但是每次简单的SQL都要手写.很枯燥并且没有太大没意义
- 数据库升级非常麻烦.需要手动写很多SQL语句

如何解决:
- 通过基类接口+APT生成表操作常用的CRUD方法
- 编译时扫描实体对象,生成属性名和表列名的映射关系,生成条件搜索的对象,根据语义化的Java代码进行SQL查询

   //示例
   ```
   //UserEntityCondition是编译期APT生成的条件搜索对象
   UserEntityCondition condition = new UserEntityCondition();
   //创建搜索条件
   condition.createCriteria()
            .andLike("firstName", "zh")
            .andEqualTo("lastName", "rong");
   //执行搜索.返回搜索集合
   List<UserEntity> userEntities = AppDatabase
                         .getDatabase(mContext)
                         .userDao()
                         .selectByCondition(condition.build());
   ```




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
- 基于属性的对象查询(Upgrading)
- 条件SQL查询
- 简化数据库升级功能(TODO)
### 使用说明


