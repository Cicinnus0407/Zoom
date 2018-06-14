package com.cicinnus.zoom.compiler;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;

import com.cicinnus.zoom.ext.ZoomNameType;
import com.cicinnus.zoom.extend.annototaion.Upgrade;
import com.cicinnus.zoom.extend.helper.UpgradeInfoBean;
import com.cicinnus.zoom.util.ProperitesUtil;
import com.cicinnus.zoom.util.SQLUtil;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

/**
 * 升级帮助类生成器
 * <pre>
 * author cicinnus
 * date 2018/6/12
 * </pre>
 */
@AutoService(Processor.class)
public class MigrationProcessor extends AbstractProcessor {

    /**
     * 方法集合
     */
    private ArrayList<MethodSpec> methodSpecList;

    /**
     * 变量集合
     */
    private ArrayList<FieldSpec> mFieldSpecList;


    private Elements elementUtils;
    private UpgradeInfoBean upgradeInfoBean;
    private String location;
    private String packageName;


    /**
     * 初始化
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        Map<String, String> options = processingEnv.getOptions();
        location = options.get("room.schemaLocation");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Upgrade.class);
        Set<? extends Element> database = roundEnvironment.getElementsAnnotatedWith(Database.class);

        for (Element element : database) {
            packageName = getPackageName((TypeElement) element);
            packageName = packageName + "." + element.getSimpleName();
        }

        for (Element element : elements) {

            methodSpecList = new ArrayList<>();
            mFieldSpecList = new ArrayList<>();
            TypeElement typeElement = (TypeElement) element;


            //注解对象
            Upgrade currentAnnotation = element.getAnnotation(Upgrade.class);
            AnnotationMirror annotationMirror = MoreElements.getAnnotationMirror(element, Upgrade.class).get();
            handleUpgradeClass(annotationMirror);


            //
            migrateFunc();
            //创建临时表
            generateTempTable();
            //创建和删除表
            generateTableOperate(typeElement, currentAnnotation.dataBaseVersion());
            //根据表名删除表
            generateDropTableByName();
            //表重命名
            generateRenameTable();
            //获取生成的类名
            String className = "MigrationHelper_" + upgradeInfoBean.getDatabase().getVersion();
            //单例
            createSingleton(typeElement, className);
            //创建一个类
            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    //方法
                    .addMethods(methodSpecList)
                    .addFields(mFieldSpecList)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("APT生成的升级类")
                    .build();


            //生成Java文件
            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec)
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return false;
    }

    private void createSingleton(TypeElement element, String className) {
        ClassName thisClass = ClassName.get(getPackageName(element), className);
        FieldSpec fieldSpec = FieldSpec.builder(thisClass, "helper", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", thisClass)
                .addJavadoc("单例对象")
                .build();
        mFieldSpecList.add(0, fieldSpec);

        MethodSpec singleton = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(thisClass)
                .addCode("\nreturn helper;\n")
                .addJavadoc("\n获取单例升级帮助类")
                .build();
        methodSpecList.add(0, singleton);

    }

    /**
     * 处理注解标记的Class[]
     *
     * @param annotationMirror
     */
    @SuppressWarnings("unchecked")
    private void handleUpgradeClass(AnnotationMirror annotationMirror) {
        Set<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> entries = annotationMirror.getElementValues().entrySet();

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : entries) {
            //只有参数名为upgradeEntities时获取值
            if ("upgradeEntities".equals(entry.getKey().getSimpleName().toString())) {

                List<AnnotationValue> values = (List<AnnotationValue>) entry.getValue().getValue();
                //创建方法
                MethodSpec.Builder setUpgradeTableList = MethodSpec.methodBuilder("getUpgradeList")
                        .addModifiers(Modifier.PRIVATE);

                ClassName arrayList = ClassName.get("java.util", "ArrayList");

                TypeName arrayListResult = ParameterizedTypeName.get(arrayList, ClassName.get(String.class));


                setUpgradeTableList
                        .addStatement("ArrayList<String> tableList = new $T<>()", arrayList);
                for (AnnotationValue annotationValue : values) {
                    DeclaredType classTypeMirror = (DeclaredType) annotationValue.getValue();
                    TypeElement typeElement = (TypeElement) classTypeMirror.asElement();
                    Entity entity = typeElement.getAnnotation(Entity.class);
                    if (entity == null) {
                        throw new IllegalArgumentException(typeElement.getSimpleName() + "没有@Entity注解");
                    }

                    //表名
                    String mTableName = typeElement.getSimpleName().toString();
                    if (!entity.tableName().equals("")) {
                        mTableName = entity.tableName();
                    }
                    setUpgradeTableList.addStatement("tableList.add(\"" + mTableName + "\")");

                }

                setUpgradeTableList
                        .addStatement("return tableList")
                        .returns(arrayListResult);

                methodSpecList.add(setUpgradeTableList.build());
            }
        }

    }

    private void migrateFunc() {

        MethodSpec migrateFunc = MethodSpec.methodBuilder("migrate")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameter(ZoomNameType.database, "database")
                .addCode("//创建临时表")
                .addCode("\nfor(String name : getUpgradeList()){")
                .addCode("\n\tTableInfo tableInfo = TableInfo.read(database, name);")
                .addCode("\n\tif (tableInfo.columns.size() != 0) {")
                .addCode("\n\tprocessTempTables(database, tableInfo);")
                .addCode("\n\t}\n}")
                .addCode("\n//创建表")
                .addCode("\n\tcreateAllTables(database);")
                .addJavadoc("具体的操作方法")
                .build();
        methodSpecList.add(migrateFunc);

    }

    private void generateTempTable() {

        String codeBlock = "String tableName = tableInfo.name;\n" +
                "String tempTableName = tableInfo.name.concat(\"_TEMP\");\n";

        //创建临时表
        MethodSpec.Builder createTempTable = MethodSpec.methodBuilder("processTempTables")
                .addParameter(ZoomNameType.database, "database")
                .addParameter(ZoomNameType.tableInfo, "tableInfo")
                .addModifiers(Modifier.PRIVATE)
                .addCode(codeBlock)
                .addCode("//创建临时表\n")
                .addCode("createTempTableByTableName(database,tableName);\n")
                .addCode("//向临时表插入数据\n")
                .addCode("insertIntoTempTable(database,tempTableName);\n")
                .addCode("//删除原表\n")
                .addCode("dropTableByName(database, tableName, true);\n")
                .addCode("//临时表重命名\n")
                .addCode("renameTable(database,tempTableName,tableName);\n")
                .addJavadoc("处理临时表");

        methodSpecList.add(createTempTable.build());

    }

    private void generateTableOperate(TypeElement typeElement, int version) {

        try {
            String filePath = location + File.separator + packageName + File.separator + version + ".json";
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("没有找到schema的json文件" + filePath + ",请检查配置项");
            }

            FileReader fileReader = new FileReader(file);
            Gson gson = new Gson();
            upgradeInfoBean = gson.fromJson(fileReader, UpgradeInfoBean.class);


            //创建方法
            MethodSpec.Builder createTable = MethodSpec.methodBuilder("createAllTables")
                    .addParameter(ZoomNameType.database, "database")
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc("创建表");


            //删除所有表
            MethodSpec.Builder dropTable = MethodSpec.methodBuilder("dropAllTables")
                    .addParameter(ZoomNameType.database, "database")
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(boolean.class, "ifExists")
                    .addJavadoc("删除表");

            //创建临时表
            MethodSpec.Builder tempTable = MethodSpec.methodBuilder("createTempTableByTableName")
                    .addJavadoc("临时表")
                    .addParameter(ZoomNameType.database, "database")
                    .addParameter(String.class, "tableName")
                    .addModifiers(Modifier.PRIVATE)
                    .addCode("String sql=\"\";\n")
                    .addCode("switch(tableName){");

            //插入数据到临时表
            MethodSpec.Builder insertTempTable = MethodSpec.methodBuilder("insertIntoTempTable")
                    .addJavadoc("将原数据插入临时表")
                    .addParameter(ZoomNameType.database, "database")
                    .addParameter(String.class, "tempTableName")
                    .addModifiers(Modifier.PRIVATE)
                    .addCode("String sql=\"\";\n")
                    .addCode("switch(tempTableName){");


            for (UpgradeInfoBean.DatabaseBean.EntitiesBean bean : upgradeInfoBean.getDatabase().getEntities()) {
                String sql = bean.getCreateSql();
                //创建
                String createSQL = "\"" + sql.replace("${TABLE_NAME}", bean.getTableName()) + "\"";
                createTable.addStatement("\t\tdatabase.execSQL(" + createSQL + ")");

                //删除
                String dropSQL = "\"DROP TABLE \"+(ifExists ? \"IF EXISTS\"  : \"\") +" + "\"" + bean.getTableName() + "\"";
                dropTable.addStatement("\t\tdatabase.execSQL(" + dropSQL + ")");

                String tempSQL = "\"" + sql.replace("${TABLE_NAME}", bean.getTableName() + "_TEMP") + "\"";
                //临时表
                tempTable
                        .addCode("\n\t\tcase \"" + bean.getTableName() + "\":\n")
                        .addStatement("\t\t\tsql=" + tempSQL + "")
                        .addCode("\t\tbreak;");

                List<String> list = new ArrayList<>();
                for (UpgradeInfoBean.DatabaseBean.EntitiesBean.FieldsBean fieldsBean : bean.getFields()) {
                    list.add(fieldsBean.getColumnName());
                }
                String properties = ProperitesUtil.join(",", list);


                //插入临时表
                insertTempTable
                        .addCode("\n\t\tcase \"" + bean.getTableName().concat("_TEMP") + "\":\n")
                        .addStatement("\t\t\tsql = \"insert into \" + tempTableName + \" ( \" +\n" +
                                "                 $S +\n" +
                                "                 \") select \" +\n" +
                                "                 $S +\n" +
                                "                 \" from " + bean.getTableName() + " ;\"", properties, properties)
                        .addCode("\t\tbreak;");

            }
            //临时表结束
            tempTable
                    .addCode("\n\t\tdefault:break;\n")
                    .addCode("\n}")
                    .addCode("\ndatabase.execSQL(sql);\n");
            SQLUtil.showLog(tempTable, "\"createTempTable:---\"" + "+sql");

            insertTempTable
                    .addCode("\n\t\tdefault:break;\n")
                    .addCode("\n}")
                    .addCode("database.execSQL(sql);\n");
            SQLUtil.showLog(insertTempTable, "\"insertIntoTempTable:---\"" + "+sql");

            methodSpecList.add(createTable.build());
//            methodSpecList.add(dropTable.build());
            methodSpecList.add(tempTable.build());
            methodSpecList.add(insertTempTable.build());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据表名删除
     */
    private void generateDropTableByName() {

        String dropSQL = "\"DROP TABLE \"+(ifExists ? \"IF EXISTS \"  : \"\") + tableName ";

        MethodSpec dropTableByName = MethodSpec.methodBuilder("dropTableByName")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ZoomNameType.database, "database")
                .addParameter(String.class, "tableName")
                .addParameter(boolean.class, "ifExists")
                .addStatement("database.execSQL(" + dropSQL + ")")
                .addJavadoc("根据表名删除表")
                .build();
        methodSpecList.add(dropTableByName);
    }

    private void generateRenameTable() {

        String renameSql = "database.execSQL(\"ALTER TABLE \" + tableName + \" RENAME TO \" + newTableName);\n";

        MethodSpec.Builder renameTable = MethodSpec.methodBuilder("renameTable")
                .addParameter(ZoomNameType.database, "database")
                .addParameter(String.class, "tableName")
                .addParameter(String.class, "newTableName")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("表重命名")
                .addCode(renameSql);
        methodSpecList.add(renameTable.build());
    }

    private void restoreData() {


        MethodSpec.Builder restoreData = MethodSpec.methodBuilder("restoreData")
                .addParameter(ZoomNameType.database, "database")
                .addParameter(ZoomNameType.tableInfo, "tableInfo")
                .addCode("String tableName = tableInfo.name;\n" +
                        "String tempTableName = tableInfo.name.concat(\"_TEMP\");\n")
                .beginControlFlow("for (String key : tableInfo.columns.keySet())")
                .addStatement("\tTableInfo.Column column = tableInfo.columns.get(key)")
                .addStatement("\tproperties.add(column.name)")
                .addCode("\n}\n")
                .addCode("String insertTableStringBuilder = \"INSERT INTO \" + tableName + \" (\" +\n" +
                        "                TextUtils.join(\",\", properties) +\n" +
                        "                \") SELECT \" +\n" +
                        "                TextUtils.join(\",\", properties) +\n" +
                        "                \" FROM \" + tempTableName + \";\";\n")

                .addCode("database.execSQL(insertTableStringBuilder);\n");

        //SQL输出
        SQLUtil.showLog(restoreData, "\"restoreData:---\"" + "+insertTableStringBuilder");
        restoreData.addCode("dropTableByName(database, tempTableName, true);\n")
                .addJavadoc("将临时表数据插入改动后的表,然后将临时表删除")
                .build();

        methodSpecList.add(restoreData.build());

    }

    /**
     * 需要注解的类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Upgrade.class.getCanonicalName());
    }

    /**
     * 源码版本为Java8
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    /**
     * 获取包名
     *
     * @param type
     * @return
     */
    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
}
