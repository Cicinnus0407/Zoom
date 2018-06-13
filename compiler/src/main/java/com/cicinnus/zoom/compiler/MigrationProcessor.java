package com.cicinnus.zoom.compiler;

import android.arch.persistence.room.Entity;

import com.cicinnus.zoom.extend.annototaion.Upgrade;
import com.cicinnus.zoom.extend.helper.UpgradeInfoBean;
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
import javax.lang.model.element.Name;
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


    private Name entityName;

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
    private String mTableName;


    /**
     * 初始化
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Upgrade.class);


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
            //恢复数据
            restoreData();
            //创建和删除表
            generateCreateAndDropTable(typeElement, currentAnnotation.schemasLocation());
            //根据表名删除表
            generateDropTableByName();


            generateMemberField();


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
                .initializer("new $T();", thisClass)
                .addJavadoc("单例对象")
                .build();
        mFieldSpecList.add(0, fieldSpec);

        MethodSpec singleton = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(thisClass)
                .addCode("\nreturn helper;\n")
                .addJavadoc("\n//获取单例升级帮助类")
                .build();
        methodSpecList.add(0, singleton);

    }

    private void generateMemberField() {
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        ClassName tableInfo = ClassName.get("android.arch.persistence.room.util", "TableInfo");

        TypeName arrayListResult = ParameterizedTypeName.get(arrayList, tableInfo);

        FieldSpec fieldSpec = FieldSpec.builder(arrayListResult, "mTableInfoList", Modifier.PRIVATE)
                .initializer("new $T()", arrayListResult)
                .build();
        mFieldSpecList.add(fieldSpec);
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
                    mTableName = typeElement.getSimpleName().toString();
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
        ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");

        MethodSpec migrateFunc = MethodSpec.methodBuilder("migrate")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameter(database, "database")
                .addCode("//创建临时表")
                .addCode("\nfor(String name : getUpgradeList()){")
                .addCode("\n\tTableInfo tableInfo = TableInfo.read(database, name);")
                .addCode("\n\tmTableInfoList.add(tableInfo);")
                .addCode("\n\tif (tableInfo.columns.size() != 0) {")
                .addCode("\n\tcreateTempTables(database, tableInfo);")
                .addCode("\n\t}\n}")
                .addCode("\n//创建表")
                .addCode("\n\tcreateAllTables(database);")
                .addCode("\n//恢复数据\n")
                .addCode("for (TableInfo tableInfo : mTableInfoList) {\n")
                .addCode("\trestoreData(database,tableInfo);\n")
                .addCode("}\n")
                .addJavadoc("具体的操作方法")
                .build();
        methodSpecList.add(migrateFunc);

    }

    private void generateTempTable() {

        String codeBlock = "String divider = \"\";\n" +
                "String tableName = tableInfo.name;\n" +
                "String tempTableName = tableInfo.name.concat(\"_temp\");\n" +
                "//拼接创建临时表的SQL\n" +
                "createTableStringBuilder.append(\"create table \")\n" +
                "                        .append(tempTableName)\n" +
                "                        .append(\" ( \");";

        ClassName arrayList = ClassName.get("java.util", "ArrayList");

        //获取DB
        ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");
        ClassName tableInfo = ClassName.get("android.arch.persistence.room.util", "TableInfo");
        ClassName textUtils = ClassName.get("android.text", "TextUtils");

        //创建临时表
        MethodSpec.Builder createTempTable = MethodSpec.methodBuilder("createTempTables")
                .addParameter(database, "database")
                .addParameter(tableInfo, "tableInfo")
                .addStatement("StringBuilder createTableStringBuilder = new StringBuilder()")
                .addStatement("ArrayList<String> properties = new $T<>()", arrayList)
                .addCode(codeBlock)
                .beginControlFlow("\nfor(String key : tableInfo.columns.keySet())")
                .addStatement("TableInfo.Column column = tableInfo.columns.get(key)")
                .addStatement("properties.add(column.name)")
                .addStatement("String type = column.type")
                .addStatement("createTableStringBuilder.append(divider)\n.append(column.name)\n.append(\" \")\n.append(type)")
                .addStatement("\nif (column.isPrimaryKey()) {\ncreateTableStringBuilder.append(\" primary key\")")
                .addCode("}\n")
                .addStatement("divider = \",\"")
                .endControlFlow()
                .addStatement("createTableStringBuilder.append(\");\")")
                .addStatement("database.execSQL(" + "createTableStringBuilder.toString()" + ")", database);

        //SQL输出
        SQLUtil.showLog(createTempTable, "\"createTempTable:---\"" + "+createTableStringBuilder.toString()")
                .addStatement(" String insertTableString = \"insert into \" + tempTableName + \" ( \" +\n" +
                        "         $T.join(\",\", properties) +\n" +
                        "         \") select \" +\n" +
                        "         $T.join(\",\", properties) +\n" +
                        "         \" from \" + tableName + \";\"", textUtils, textUtils)
                .addStatement("database.execSQL(" + "insertTableString" + ")", database);
        //SQL输出
        SQLUtil.showLog(createTempTable, "\"insertTempTable:---\"" + "+insertTableString")
                .addCode("dropTableByName(database, tableName, true);\n")
                .addJavadoc("创建表");

        methodSpecList.add(createTempTable.build());

    }

    private void generateCreateAndDropTable(TypeElement typeElement, String schemaLocation) {
        entityName = typeElement.getSimpleName();

        try {
            File file = new File(schemaLocation);
            if (!file.exists()) {
                throw new IllegalArgumentException("请输入正确的Schema文件路径");
            }
            FileReader fileReader = new FileReader(file);
            Gson gson = new Gson();
            upgradeInfoBean = gson.fromJson(fileReader, UpgradeInfoBean.class);

            //获取DB
            ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");
            //创建方法
            MethodSpec.Builder createTable = MethodSpec.methodBuilder("createAllTables")
                    .addParameter(database, "database")
                    .addModifiers(Modifier.PRIVATE)
                    .addJavadoc("创建表");


            //删除所有表
            MethodSpec.Builder dropTable = MethodSpec.methodBuilder("dropAllTables")
                    .addParameter(database, "database")
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(boolean.class, "ifExists")
                    .addJavadoc("删除表");


            for (UpgradeInfoBean.DatabaseBean.EntitiesBean bean : upgradeInfoBean.getDatabase().getEntities()) {
                String sql = bean.getCreateSql();
                //创建
                String createSQL = "\"" + sql.replace("${TABLE_NAME}", bean.getTableName()) + "\"";
                createTable.addStatement("\t\tdatabase.execSQL(" + createSQL + ")");

                //删除
                String dropSQL = "\"DROP TABLE \"+(ifExists ? \"IF EXISTS\"  : \"\") +" + "\"" + bean.getTableName() + "\"";
                dropTable.addStatement("\t\tdatabase.execSQL(" + dropSQL + ")");

            }

            methodSpecList.add(createTable.build());
            methodSpecList.add(dropTable.build());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void generateDropTableByName() {
        ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");


        String dropSQL = "\"DROP TABLE \"+(ifExists ? \"IF EXISTS \"  : \"\") + tableName ";

        MethodSpec dropTableByName = MethodSpec.methodBuilder("dropTableByName")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(database, "database")
                .addParameter(String.class, "tableName")
                .addParameter(boolean.class, "ifExists")
                .addStatement("database.execSQL(" + dropSQL + ")")
                .addJavadoc("根据表名删除表")
                .build();
        methodSpecList.add(dropTableByName);
    }

    private void restoreData() {
        ClassName database = ClassName.get("android.arch.persistence.db", "SupportSQLiteDatabase");
        ClassName tableInfo = ClassName.get("android.arch.persistence.room.util", "TableInfo");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");


        MethodSpec.Builder restoreData = MethodSpec.methodBuilder("restoreData")
                .addParameter(database, "database")
                .addParameter(tableInfo, "tableInfo")
                .addCode("String tableName = tableInfo.name;\n" +
                        "String tempTableName = tableInfo.name.concat(\"_temp\");\n")
                .addStatement("ArrayList<String> properties = new $T<>()", arrayList)
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
