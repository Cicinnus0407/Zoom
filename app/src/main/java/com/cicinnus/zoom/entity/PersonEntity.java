package com.cicinnus.zoom.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 人类对象的表结构
 * <pre>
 * author cicinnus
 * date 2018/6/3
 * </pre>
 */
@Entity
public class PersonEntity {


    /**
     * String 主键
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String personId;


    /**
     * 有没ColumnInfo
     */
    private String name;


    /**
     * 有ColumnInfo
     */
    @ColumnInfo(name = "person_age")
    private int age;


    /**
     * 忽略字段
     */
    @Ignore
    private String ignoreProperty;


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getIgnoreProperty() {
        return ignoreProperty;
    }

    public void setIgnoreProperty(String ignoreProperty) {
        this.ignoreProperty = ignoreProperty;
    }

    @Override
    public String toString() {
        return "PersonEntity\n{" +"\n"+
                "personId='" + personId + '\'' +"\n"+
                "name='" + name + '\'' +"\n"+
                "age=" + age +"\n"+
                "ignoreProperty='" + ignoreProperty + '\'' +"\n"+
                '}';
    }
}
