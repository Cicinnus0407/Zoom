package com.cicinnus.zoom.ui;

import android.os.Bundle;
import android.util.Log;
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
 * CRUD 操作Demo
 * author cicinnus
 * date 2018/6/12
 */
public class BasicCRUDActivity extends BaseActivity {


    private static final String TAG = BasicCRUDActivity.class.getSimpleName();

    /**
     * 姓
     */
    EditText etFirstName;


    /**
     * 名
     */
    EditText etLastName;


    /**
     * id
     */
    EditText etId;


    /**
     * 输出内容
     */
    TextView mTvContent;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_basic_crud;
    }

    @Override
    protected void initViewAndData(Bundle savedInstanceState) {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etId = findViewById(R.id.et_id);
        mTvContent = findViewById(R.id.tv_content);
    }

    /**
     * 插入一条数据
     *
     * @param view
     */
    public void insert(View view) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(etFirstName.getText().toString());
        userEntity.setLastName(etLastName.getText().toString());
        Observable.just(userEntity)
                .map(new Function<UserEntity, Long>() {
                    @Override
                    public Long apply(UserEntity userEntity) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .userDao()
                                .insert(userEntity);
                    }
                })
                .compose(CustomScheculers.<Long>iO2Main())
                .subscribe(new DefaultObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        ToastUtil.showShort("插入成功,当前rowId:" + aLong);
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
     * 根据id查询单个
     *
     * @param view
     */
    public void queryOne(View view) {
        int id = Integer.parseInt(etId.getText().toString());
        Observable.just(id)
                .map(new Function<Integer, UserEntity>() {
                    @Override
                    public UserEntity apply(Integer id) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .userDao()
                                .selectOneById(id);
                    }
                })
                .compose(CustomScheculers.<UserEntity>iO2Main())
                .subscribe(new DefaultObserver<UserEntity>() {
                    @Override
                    public void onNext(UserEntity userEntity) {
                        if (userEntity != null) {
                            mTvContent.setText(userEntity.toString());
                        } else {
                            ToastUtil.showShort("不存在当前id的数据");
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
     * 查询所有
     *
     * @param view
     */
    public void loadAll(View view) {
        Observable
                .just(view)
                .map(new Function<Object, List<UserEntity>>() {
                    @Override
                    public List<UserEntity> apply(Object o) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .userDao()
                                .selectAll();
                    }
                })
                .compose(CustomScheculers.<List<UserEntity>>iO2Main())
                .subscribe(new DefaultObserver<List<UserEntity>>() {
                    @Override
                    public void onNext(List<UserEntity> userEntities) {
                        ToastUtil.showShort(String.format("总共有%s条数据", userEntities.size()));
                        setContent(userEntities);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 删除一条
     *
     * @param view
     */
    public void delete(View view) {
        final int id = Integer.parseInt(etId.getText().toString());
        Observable.just(id)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .userDao()
                                .deleteById(id);
                    }
                })
                .compose(CustomScheculers.<Integer>iO2Main())
                .subscribe(new DefaultObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        ToastUtil.showShort("删除成功" + integer);
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
