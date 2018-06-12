package com.cicinnus.zoom.ui;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.cicinnus.zoom.AppDatabase;
import com.cicinnus.zoom.R;
import com.cicinnus.zoom.base.BaseActivity;
import com.cicinnus.zoom.entity.UserEntity;

import java.util.List;

/**
 * LiveData示例
 * <pre>
 * author cicinnus
 * date 2018/6/6
 * </pre>
 */
public class LiveDataSampleActivity extends BaseActivity {

    private TextView mTvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_data;
    }

    @Override
    protected void initViewAndData(Bundle savedInstanceState) {
        mTvContent = findViewById(R.id.tv_content);


    }

    /**
     * LiveData数据加载监听
     */
    private void initLiveData() {
        AppDatabase
                .getDatabase(mContext)
                .userDao()
                .selectAllAsLiveData()
                .observe(this, new Observer<List<UserEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<UserEntity> personEntities) {
                        if (personEntities != null) {
                            setContent(personEntities);
                        }
                    }
                });
    }

    /**
     * 设置数据内容
     *
     * @param userEntities
     */
    private void setContent(List<UserEntity> userEntities) {
        StringBuilder builder = new StringBuilder();

        for (UserEntity userEntity : userEntities) {
            builder.append(userEntity.toString())
                    .append("\n");
        }
        mTvContent.setText(builder.toString());
    }

    public void selectAsLiveData(View view) {
        initLiveData();

    }
}
