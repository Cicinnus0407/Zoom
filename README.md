### Zoom  [ ![Download](https://api.bintray.com/packages/cicinnus0407/Zoom/compiler/images/download.svg?version=1.0.2) ](https://bintray.com/cicinnus0407/Zoom/compiler/1.0.2/link)
[English DOC](README_EN.md)

Zoom是基于Room的功能扩展库.

欢迎PR/commit issue.

觉得好的可以点一下star.

如果希望对库进行改造,可以点一下fork

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
> 如 UserDao -> UserDaoExtend

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

### 引入依赖

#### 1.引入Room数据库
```groovy
dependencies {
    api 'android.arch.persistence.room:runtime:1.1.0'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.0'
}
```

#### 2.添加Zoom的仓库地址.添加Zoom依赖(JCenter还未审核通过,所以需要手动指定仓库地址)
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

[简单使用示例](./wiki/simple_tutorial.md)

更多使用方法请查看sample

---

### 参考
[Mybatis通用Mapper](https://github.com/abel533/Mapper)

[Getting Class values from Annotations in an AnnotationProcessor](https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/)

[ANNOTATION PROCESSING 101](http://hannesdorfmann.com/annotation-processing/annotationprocessing101)


### License
```
Copyright [2018] [r09er zhong]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```