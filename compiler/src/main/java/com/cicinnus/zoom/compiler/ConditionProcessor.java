package com.cicinnus.zoom.compiler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.cicinnus.zoom.extend.annototaion.DaoExtend;
import com.cicinnus.zoom.extend.entity.BaseQueryCondition;
import com.cicinnus.zoom.util.SQLUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

/**
 * 创建条件搜索的处理器
 * author cicinnus
 * date 2018/6/1
 */
@AutoService(Processor.class)
public class ConditionProcessor extends AbstractProcessor {

    private Elements elementUtils;


    /**
     * 方法集合
     */
    private List<MethodSpec> methodSpecList;
    /**
     * 表名
     */
    private String mTableName;
    /**
     * 对象名
     */
    private Name entityName;


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
        //获取被RoomExtend注解的元素
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(DaoExtend.class);


        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;

            /*
            注解对象
            */
            DaoExtend currentAnnotation = element.getAnnotation(DaoExtend.class);


            methodSpecList = new ArrayList<>();

            try {
                currentAnnotation.entity();
            } catch (MirroredTypeException mte) {
                //获取entity属性,Class在编译期间无法获取准确对象,所以要使用getTypeMirror()
                //处理获取到的类对象
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                handleClass(classTypeElement);
            }


            generateConstructor(element);
            //检查属性是否存在的方法
            generateCheckMethod(typeElement);
            //创建sql拼接的方法
            generateSqlMethod();


            //创建一个类
            TypeSpec typeSpec = TypeSpec.classBuilder(entityName + "Condition")
                    //继承自QueryCondition
                    .superclass(ClassName.get(BaseQueryCondition.class))
                    //方法
                    .addMethods(methodSpecList)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("APT生成的条件查询对象")
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

    /**
     * 获取实体类对象
     */
    private void handleClass(TypeElement typeElement) {
        //对象名
        entityName = typeElement.getSimpleName();
        //获取实体对象
        Entity entity = typeElement.getAnnotation(Entity.class);
        if (entity == null) {
            throw new IllegalArgumentException("@DaoExtend注解绑定的实体对象没有@Entity注解");
        }
        //表名
        mTableName = typeElement.getSimpleName().toString();
        if (!entity.tableName().equals("")) {
            mTableName = entity.tableName();
        }
        //根据注解生成属性和字段名的匹配Map
        HashMap<String, String> params = new HashMap<>();

        for (Element element : typeElement.getEnclosedElements()) {
            if (element == null) {
                continue;
            }
            //只有成员变量才进行判断
            if (element instanceof VariableElement) {

                //属性名
                String propertyName = element.getSimpleName().toString();
                //属性名-表列名
                params.put(propertyName, propertyName);
                //如果被注解了,使用注解的字段名
                ColumnInfo columnInfo = element.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    params.put(propertyName, columnInfo.name());
                }
                //判断是否被@Ignore注解,如果被@Ignore注解,则不放进Map
                Ignore ignore = element.getAnnotation(Ignore.class);
                if (ignore != null) {
                    params.remove(propertyName);
                }
            }

        }


        //构建一个Map
        StringBuilder codeBuilder = new StringBuilder();

        for (String property : params.keySet()) {
            codeBuilder.append("mPropertyMap")
                    .append(".put(\"")
                    .append(property)
                    .append("\",\"")
                    .append(params.get(property))
                    .append("\")")
                    .append(";")
                    .append("\n");
        }

        String codeBlock = codeBuilder.toString();

        MethodSpec methodSpec = MethodSpec.methodBuilder("generateProperty")
                .addModifiers(Modifier.PRIVATE)
                .addJavadoc("属性和表列名进行匹配")
                .addCode(codeBlock)
                .build();
        methodSpecList.add(methodSpec);


    }


    /**
     * 生成构造器代码
     *
     * @param typeElement
     */
    private void generateConstructor(Element typeElement) {

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode("this(true);\n")
                .addCode("generateProperty();\n")
                .build();

        methodSpecList.add(constructor);


        MethodSpec constructorWithParams = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(boolean.class, "notNull")
                .addCode("super(notNull);\n")
                .addCode("generateProperty();\n")
                .build();

        methodSpecList.add(constructorWithParams);

    }


    /**
     * 创建SQL语句
     */
    private void generateSqlMethod() {

        String preSql = String.format("\"select * from %s where 1=1 \"", mTableName);

        //创建一个RawQuery
        ClassName returnType = ClassName.get("android.arch.persistence.db", "SimpleSQLiteQuery");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("build")
                .addCode("String conditionSql = generateConditionSQL();\n");
        //输出SQL语句
        SQLUtil.showLog(builder, "\"Query SQL: \"+"+preSql + " + conditionSql");

        builder
                //添加代码块
                .addStatement("$T SQLiteQuery =  new $T($L + conditionSql)", returnType, returnType, preSql)
                .addModifiers(Modifier.PUBLIC)
                //返回类型
                .returns(returnType)
                .addStatement("return SQLiteQuery")
                .build();

        methodSpecList.add(builder.build());

    }

    /**
     * 检查参数的方法
     */
    private void generateCheckMethod(TypeElement element) {
        String codeBlock = "" +
                "if (mPropertyMap.containsKey(property)) {\n" +
                "   return mPropertyMap.get(property);\n" +
                "} else if (notNull) {\n" +
                "    throw new IllegalArgumentException(String.format(\" %s exclusive't %s property \", \"" + entityName + "\", property));\n" +
                "} else {\n" +
                "    return null;\n" +
                "}\n";

        MethodSpec checkMethodSpec = MethodSpec.methodBuilder("checkPropertyByAPT")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(String.class, "property")
                .returns(String.class)
                .addCode(codeBlock)
                .build();
        methodSpecList.add(checkMethodSpec);

    }


    /**
     * 需要注解的类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DaoExtend.class.getCanonicalName());
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
