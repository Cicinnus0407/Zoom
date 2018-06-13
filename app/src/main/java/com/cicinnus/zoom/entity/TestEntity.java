package com.cicinnus.zoom.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/13
 * </pre>
 */
@Entity
public class TestEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;


    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
