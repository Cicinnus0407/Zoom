### Room扩展库



#### 使用说明

使用扩展之前必须先对Room的基础使用有了解.

基于Room数据库进行扩展,由于Room使用上的限制.所以特地为Room进行了基础扩展.

没有对Room原有功能进行修改.只是额外常用方法.

通过APT+自定义注解+JavaPoet.编译时生成代码.


---

主要功能:

- 添加了常用的基于对象的CRUD操作
  - insert(T t);
  - selectById(Object Id);
  - delectBydi(Object id);
  - update(T t);
  - ..etc
- 添加基础分页查询功能
  - selectByPageAndRows(int page,int rows);
- 获取表数据条数
  - count();
- 基于属性的对象查询(Coming Soon)

