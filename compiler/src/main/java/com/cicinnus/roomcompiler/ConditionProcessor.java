package com.cicinnus.roomcompiler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.cicinnus.roomextend.annototaion.DaoExtend;
import com.cicinnus.roomextend.entity.QueryCondition;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
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

    private List<FieldSpec> memberFieledList;

    /**
     * 表名
     */
    private String mTableName;
    /**
     * 主键名
     */
    private String mPrimaryKeyName;
    /**
     * 主键类型
     */
    private TypeName mPrimaryType;
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

            //获取entity属性,Class在编译期间无法获取准确对象,所以要使用getTypeMirror()


            methodSpecList = new ArrayList<>();
            memberFieledList = new ArrayList<>();

            try {
                currentAnnotation.entity();
            } catch (MirroredTypeException mte) {

                //处理获取到的类对象
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                handleClass(classTypeElement);
            }


            generateConstructor(element);
            //检查属性是否存在的方法
            generateCheckMethod();
            //创建sql拼接的方法
            generateSqlMethod();


            //创建一个类
            TypeSpec typeSpec = TypeSpec.classBuilder(entityName + "Condition")
                    //继承自QueryCondition
                    .superclass(ClassName.get(QueryCondition.class))
                    //成员变量
                    .addFields(memberFieledList)
                    //方法
                    .addMethods(methodSpecList)
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
        //表名
        mTableName = entity.tableName();
        //根据注解生成属性和字段名的匹配Map
        HashMap<String, String> params = new HashMap<>();

        for (Element element : typeElement.getEnclosedElements()) {
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
            codeBuilder.append("propertyToColumnMap")
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
                .addCode("generateProperty();")
                .build();

        methodSpecList.add(constructor);

    }


    private void generateSqlMethod() {
        String sql = " StringBuilder sqlBuilder = new StringBuilder(\"select * from " + mTableName + " where 1 = 1 \");\n" +
                "        //拼接匹配查询\n" +
                "        for (String key : equalsMap.keySet()) {\n" +
                "            sqlBuilder.append(\" and \")\n" +
                "                    .append(key)\n" +
                "                    .append(\" = \")\n" +
                "                    .append(\"'\")\n" +
                "                    .append(equalsMap.get(key))\n" +
                "                    .append(\"'\");\n" +
                "        }\n" +
                "        //模糊匹配\n" +
                "        for (String likeKey : likesMap.keySet()) {\n" +
                "            sqlBuilder\n" +
                "                    .append(\" and \")\n" +
                "                    .append(likeKey)\n" +
                "                    .append(\"%\")\n" +
                "                    .append(likesMap.get(likeKey))\n" +
                "                    .append(\"% \");\n" +
                "\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        return new SimpleSQLiteQuery(sqlBuilder.toString());";


        //创建一个RawQuery
        ClassName returnType = ClassName.get("android.arch.persistence.db", "SimpleSQLiteQuery");
        MethodSpec methodSpec = MethodSpec.methodBuilder("getQueryCondition")
                .addCode(sql)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .build();

        methodSpecList.add(methodSpec);

    }

    /**
     * 检查参数的方法
     */
    private void generateCheckMethod() {
        String codeBlock = "if (!propertyToColumnMap.containsKey(property)) {\n" +
                "            throw new IllegalArgumentException(String.format(\" %s 没有 %s 属性\", \"UserEntity\", property));\n" +
                "        }";

        MethodSpec checkMethodSpec = MethodSpec.methodBuilder("checkProperty")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(String.class, "property")
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
