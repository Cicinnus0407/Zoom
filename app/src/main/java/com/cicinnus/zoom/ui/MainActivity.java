package com.cicinnus.zoom.ui;

import android.os.Bundle;
import android.view.View;

import com.cicinnus.zoom.R;
import com.cicinnus.zoom.base.BaseActivity;

/**
 * 主入口Activity
 *
 * @author cicinnus
 * @date 2018/6/2
 */
public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewAndData(Bundle savedInstanceState) {

    }

    public void BasicCRUDActivity(View view) {
        openActivity(BasicCRUDActivity.class);
    }


    public void SelectByPageAndRows(View view) {
        openActivity(SelectByPageAndRowsActivity.class);
    }

    public void conditionSearch(View view) {
        openActivity(SelectByConditionActivity.class);
    }

    public void livedataSearch(View view) {
        openActivity(LiveDataSampleActivity.class);
    }
}
