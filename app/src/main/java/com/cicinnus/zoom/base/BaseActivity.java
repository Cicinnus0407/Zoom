package com.cicinnus.zoom.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 基础Activity
 * author cicinnus
 * date 2018/6/2
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected AppCompatActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        initViewAndData(savedInstanceState);
    }


    /**
     * Activity跳转
     *
     * @param targetClass
     */
    protected void openActivity(Class<?> targetClass) {
        Intent intent = new Intent(mContext, targetClass);
        startActivity(intent);
    }

    /**
     * 抽象layoutId方法
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 抽象view和数据处理方法
     *
     * @param savedInstanceState
     */
    protected abstract void initViewAndData(Bundle savedInstanceState);

}
