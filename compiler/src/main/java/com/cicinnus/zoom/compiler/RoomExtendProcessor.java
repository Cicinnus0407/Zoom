package com.cicinnus.zoom.compiler;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import com.cicinnus.zoom.extend.annototaion.DaoExtend;
import com.cicinnus.zoom.extend.base.ZoomBaseDao;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Room的扩展实现
 */
@AutoService(Processor.class)
public class RoomExtendProcessor extends AbstractProcessor {


    /**
     * 元素结合
     */
    private Elements elementUtils;

    /**
     * 需要生成的方法集合
     */
    private List<MethodSpec> methodSpecList;

    /**
     * 表名
     */
    private String mTableName;
    /**
     * 主键名称
     */
    private String mPrimaryKeyName;
    /**
     * 主键类型
     */
    private TypeName mPrimaryType;


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
            TypeMirror entityClass = null;

            try {
                currentAnnotation.entity();
            } catch (MirroredTypeException mte) {
                entityClass = mte.getTypeMirror();

                //处理获取到的类对象
                DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                handleClass(classTypeElement);
            }
            //实际的泛型对象
            TypeName realTypeName = ClassName.get(entityClass);
            //方法集合
            methodSpecList = new ArrayList<>();
            //创建selectAll方法
            generateSelectAll(realTypeName);
            //创建selectOne方法
            generateSelectOneById(realTypeName);
            //通过id删除
            generateDeleteById();
            //删除所有
            generateDeleteAll();
            //通过id集合获取实体列表
            generateSelectByIds(realTypeName);
            //分页
            generateSelectByPageAndRows(realTypeName);
            //手动sql
            generateCondition(realTypeName, element);
            //条数
            generateCount();


            //基类接口,拥有基础的insert,delete,update方法
            TypeName superClass = ParameterizedTypeName.get(ClassName.get(ZoomBaseDao.class), realTypeName);

            //创建接口,生成的类的名字为原有名字加上Extend后缀
            TypeSpec typeSpec = TypeSpec.interfaceBuilder(typeElement.getSimpleName() + "Extend")
                    //添加一个@Dao的注解用于被Room发现并创建
                    .addAnnotation(Dao.class)
                    //修饰为public
                    .addModifiers(Modifier.PUBLIC)
                    //继承基础的增删改接口
                    .addSuperinterface(superClass)
                    //继承基类接口,自动实现所有功能
                    .addSuperinterface(ClassName.get(typeElement))
                    //添加需要创建的方法
                    .addMethods(methodSpecList)
                    .addJavadoc("这是APT自动生成的代码,实现了基础的对象实体增删改查功能")
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
        for (Element element : typeElement.getEnclosedElements()) {
            //被注解标记
            PrimaryKey primaryKey = element.getAnnotation(PrimaryKey.class);
            //默认是属性名
            if (primaryKey != null) {
                mPrimaryKeyName = element.toString();

                mPrimaryType = ClassName.get(element.asType());

                //如果同时也有ColumnInfo,则优先使用
                ColumnInfo columnInfo = element.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    mPrimaryKeyName = columnInfo.name();
                }
            }
        }

    }


    /**
     * 创建查询所有类集合的方法
     *
     * @param realTypeName Class对象的TypeName
     */
    private void generateSelectAll(TypeName realTypeName) {

        //创建查询所有实体对象
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"select * from %s\"", mTableName))
                .build();

        //返回泛型List
        ClassName list = ClassName.get("java.util", "List");
        //泛型参数
        TypeName resultList = ParameterizedTypeName.get(list, realTypeName);
        MethodSpec queryAll = MethodSpec.methodBuilder("selectAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(annotationSpec)
                .returns(resultList)
                .addJavadoc("查询所有实体集合")
                .build();
        methodSpecList.add(queryAll);


        //LiveData
        ClassName liveData = ClassName.get("android.arch.lifecycle", "LiveData");

        //泛型参数
        TypeName liveDataResult = ParameterizedTypeName.get(liveData, resultList);
        //泛型参数
        MethodSpec queryAllAsLiveData = MethodSpec.methodBuilder("selectAllAsLiveData")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(annotationSpec)
                .returns(liveDataResult)
                .addJavadoc("查询所有实体集合")
                .build();
        methodSpecList.add(queryAllAsLiveData);

    }

    /**
     * 创建查询单个实体的方法,通过id
     *
     * @param realTypeName
     */
    private void generateSelectOneById(TypeName realTypeName) {
        //创建查询注解
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"select * from %s where %s = (:id) limit 1\"", mTableName, mPrimaryKeyName))
                .build();
        //创建selectOne方法,参数类型为long
        MethodSpec selectOneByLongId = MethodSpec.methodBuilder("selectOneById")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(mPrimaryType, "id")
                .addAnnotation(annotationSpec)
                .returns(realTypeName)
                .addJavadoc("通过主键搜索单个实体")
                .build();
        methodSpecList.add(selectOneByLongId);

        ClassName liveData = ClassName.get("android.arch.lifecycle", "LiveData");
        //泛型参数
        TypeName liveDataResult = ParameterizedTypeName.get(liveData, realTypeName);

        //创建selectOne方法,参数类型为long
        MethodSpec selectOneByIdAsLiveData = MethodSpec.methodBuilder("selectOneByIdAsLiveData")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(mPrimaryType, "id")
                .addAnnotation(annotationSpec)
                .returns(liveDataResult)
                .addJavadoc("通过主键搜索单个实体")
                .build();
        methodSpecList.add(selectOneByIdAsLiveData);


    }

    /**
     * 创建根据id集合查询
     *
     * @param realTypeName
     */
    private void generateSelectByIds(TypeName realTypeName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"select * from %s where %s in (:ids)\"", mTableName, mPrimaryKeyName))
                .build();

        //返回泛型List
        ClassName list = ClassName.get("java.util", "List");
        //泛型参数
        TypeName resultList = ParameterizedTypeName.get(list, realTypeName);
        //id数组
        ArrayTypeName arrayTypeName = ArrayTypeName.of(mPrimaryType);

        MethodSpec selectOneByIds = MethodSpec.methodBuilder("selectOneByIds")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(arrayTypeName, "ids")
                .addAnnotation(annotationSpec)
                .returns(resultList)
                .addJavadoc("根据id集合,返回实体集合")
                .build();
        methodSpecList.add(selectOneByIds);


        ClassName liveData = ClassName.get("android.arch.lifecycle", "LiveData");
        //泛型参数
        TypeName liveDataResult = ParameterizedTypeName.get(liveData, resultList);

        MethodSpec selectOneByIdsAsLiveData = MethodSpec.methodBuilder("selectOneByIdsAsLiveData")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(arrayTypeName, "ids")
                .addAnnotation(annotationSpec)
                .returns(liveDataResult)
                .addJavadoc("根据id集合,返回实体集合")
                .build();
        methodSpecList.add(selectOneByIdsAsLiveData);


    }


    /**
     * 通过id删除
     */
    private void generateDeleteById() {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"delete from %s where %s = (:id)\"", mTableName, mPrimaryKeyName))
                .build();
        //deleteById,参数类型为long
        MethodSpec deleteById = MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(mPrimaryType, "id")
                .addAnnotation(annotationSpec)
                .returns(int.class)
                .addJavadoc("根据主键删除一条数据")
                .build();
        methodSpecList.add(deleteById);

    }

    /**
     * 删除所有数据
     */
    private void generateDeleteAll() {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"delete from %s \"", mTableName))
                .build();
        //deleteById,参数类型为long
        MethodSpec deleteAll = MethodSpec.methodBuilder("deleteAll")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(annotationSpec)
                .returns(int.class)
                .addJavadoc("删除所有表数据")
                .build();
        methodSpecList.add(deleteAll);
    }


    /**
     * 获取分页条数和页数
     *
     * @param realTypeName
     */
    public void generateSelectByPageAndRows(TypeName realTypeName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"select * from %s limit (:page * :rows ),:rows \"", mTableName))
                .build();

        //返回泛型List
        ClassName list = ClassName.get("java.util", "List");
        //泛型参数
        TypeName resultList = ParameterizedTypeName.get(list, realTypeName);

        MethodSpec selectByPageRows = MethodSpec.methodBuilder("selectByPageRows")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(int.class, "page")
                .addParameter(int.class, "rows")
                .addAnnotation(annotationSpec)
                .returns(resultList)
                .addJavadoc("分页查询,page从0开始,rows是分页的条数\n" +
                        "如果是查询0-5条,则是page=0,rows=5\n" +
                        "查询第6-10条,则是page=1,rows5.如此类推")
                .build();

        methodSpecList.add(selectByPageRows);

        ClassName liveData = ClassName.get("android.arch.lifecycle", "LiveData");
        //泛型参数
        TypeName liveDataResult = ParameterizedTypeName.get(liveData, resultList);

        MethodSpec selectByPageRowsAsLiveData = MethodSpec.methodBuilder("selectByPageRowsAsLiveData")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(int.class, "page")
                .addParameter(int.class, "rows")
                .addAnnotation(annotationSpec)
                .returns(liveDataResult)
                .addJavadoc("分页查询,page从0开始,rows是分页的条数\n" +
                        "如果是查询0-5条,则是page=0,rows=5\n" +
                        "查询第6-10条,则是page=1,rows5.如此类推")
                .build();

        methodSpecList.add(selectByPageRowsAsLiveData);

    }


    /**
     * 条件搜索
     *
     * @param realTypeName
     * @param element
     */
    private void generateCondition(TypeName realTypeName, Element element) {


        //element是被@DaoExtend注释的接口

        AnnotationSpec annotationSpec = AnnotationSpec.builder(RawQuery.class)
                .build();

        //返回泛型List
        ClassName list = ClassName.get("java.util", "List");
        //泛型参数
        TypeName resultList = ParameterizedTypeName.get(list, realTypeName);

        //创建一个RawQuery
        ClassName paramType = ClassName.get("android.arch.persistence.db", "SupportSQLiteQuery");

        MethodSpec selectByCondition = MethodSpec.methodBuilder("selectByCondition")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(paramType, "query")
                .addAnnotation(annotationSpec)
                .returns(resultList)
                .addJavadoc("条件搜索")
                .build();
        methodSpecList.add(selectByCondition);
    }


    /**
     * 获取条数
     */
    private void generateCount() {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Query.class)
                .addMember("value", String.format("\"select count(*) from %s \"", mTableName))
                .build();

        MethodSpec selectByCondition = MethodSpec.methodBuilder("count")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(annotationSpec)
                .returns(long.class)
                .addJavadoc("获取单表的数据总数")
                .build();
        methodSpecList.add(selectByCondition);
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
