## 数据库升级使用教程

### 注意事项:
1.数据库升级的方法依赖于Room的Schemae导出功能

1.1 设置schema导出路径,路径不强制要求放在示例目录
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


### 使用教程
使用场景
- 当前数据库版本为1.需要升级到2
- 在升级过程中,添加了@Entity注解的类(即希望创建新的表)
- 在升级过程中,对原有的实体进行了修改(即修改了表的列(新增,修改,删除))

#### 1.创建一个类.添加@Upgrade注解
```
@Upgrade(dataBaseVersion = 2, upgradeEntities = {})
    public static class MIGRATE_1_2 {

}
```