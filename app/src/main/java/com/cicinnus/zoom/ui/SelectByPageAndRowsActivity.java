package com.cicinnus.zoom.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cicinnus.zoom.AppDatabase;
import com.cicinnus.zoom.R;
import com.cicinnus.zoom.ToastUtil;
import com.cicinnus.zoom.base.BaseActivity;
import com.cicinnus.zoom.entity.UserEntity;
import com.cicinnus.zoom.util.CustomScheculers;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/3
 * </pre>
 */
public class SelectByPageAndRowsActivity extends BaseActivity {

    /**
     * 页数
     */
    EditText mEtPage;

    /**
     * 条数
     */
    EditText mEtRows;

    TextView mTvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_page_rows;
    }

    @Override
    protected void initViewAndData(Bundle savedInstanceState) {
        mEtPage = findViewById(R.id.et_page);
        mEtRows = findViewById(R.id.et_rows);
        mTvContent = findViewById(R.id.tv_content);
    }


    /**
     * 分页查询,page从0开始,rows是分页的条数
     * 如果是查询0-5条,则是page=0,rows=5
     * 查询第6-10条,则是page=1,rows5.如此类推
     */
    public void selectByPageAndRows(View view) {
        if (mEtPage.getText().length() == 0 || mEtRows.getText().length() == 0) {
            return;
        }

        final int page = Integer.parseInt(mEtPage.getText().toString());
        final int rows = Integer.parseInt(mEtRows.getText().toString());


        Observable.just(view)
                .map(new Function<View, List<UserEntity>>() {
                    @Override
                    public List<UserEntity> apply(View view) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .userDao()
                                .selectByPageRows(page, rows);
                    }
                })
                .compose(CustomScheculers.<List<UserEntity>>iO2Main())
                .subscribe(new DefaultObserver<List<UserEntity>>() {
                    @Override
                    public void onNext(List<UserEntity> userEntities) {
                        if (userEntities.size() != 0) {
                            setContent(userEntities);
                        } else {
                            ToastUtil.showShort("当前分页没有数据");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 显示所有数据
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

}
