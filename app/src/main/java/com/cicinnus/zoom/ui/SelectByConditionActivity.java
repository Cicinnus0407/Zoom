package com.cicinnus.zoom.ui;

import android.arch.lifecycle.ComputableLiveData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cicinnus.zoom.AppDatabase;
import com.cicinnus.zoom.R;
import com.cicinnus.zoom.ToastUtil;
import com.cicinnus.zoom.base.BaseActivity;
import com.cicinnus.zoom.dao.PersonEntityCondition;
import com.cicinnus.zoom.entity.PersonEntity;
import com.cicinnus.zoom.entity.UserEntity;
import com.cicinnus.zoom.util.CustomScheculers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/3
 * </pre>
 */
public class SelectByConditionActivity extends BaseActivity {

    private static final String TAG = SelectByConditionActivity.class.getSimpleName();
    private TextView mTvContent;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_by_condition;
    }

    @Override
    protected void initViewAndData(Bundle savedInstanceState) {
        mTvContent = findViewById(R.id.tv_content);
    }

    public void insertFakeData(View view) {
        List<PersonEntity> personEntityList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(30); i++) {
            PersonEntity personEntity = new PersonEntity();
            personEntity.setPersonId(UUID.randomUUID().toString());
            personEntity.setAge(random.nextInt(15) + i);
            personEntity.setName("PersonName" + i);
            personEntity.setIgnoreProperty("ignore---" + i);
            personEntityList.add(personEntity);
        }
        Observable.just(personEntityList)
                .map(new Function<List<PersonEntity>, List<Long>>() {
                    @Override
                    public List<Long> apply(List<PersonEntity> personEntities) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .personDao()
                                .insertList(personEntities);
                    }
                })
                .compose(CustomScheculers.<List<Long>>iO2Main())
                .subscribe(new Observer<List<Long>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Long> longs) {
                        ToastUtil.showShort("插入成功,插入的条数为:" + longs.size());
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
     * 条件搜索
     *
     * @param view
     */
    public void selectByCondition(View view) {
        PersonEntityCondition condition = new PersonEntityCondition();
        //(age >=10 && age<-20 )||age <5
        condition.createCriteria()
                //第一个参数为Entity的属性名,并不是数据库表字段
                .andBetween("age", 10, 20)
                .orLessThan("age", 5);
        //排序必须在分页前面或者在最后
//                .orderByDesc("personId");
        //分页必须在最后一个条件
//                .limit(0, 10);


        Observable.just(condition)
                .map(new Function<PersonEntityCondition, List<PersonEntity>>() {
                    @Override
                    public List<PersonEntity> apply(PersonEntityCondition personEntityCondition) throws Exception {
                        return AppDatabase.getDatabase(mContext)
                                .personDao()
                                .selectByCondition(personEntityCondition.build());
                    }
                })
                .compose(CustomScheculers.<List<PersonEntity>>iO2Main())
                .subscribe(new Observer<List<PersonEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<PersonEntity> personEntities) {
                        ToastUtil.showShort("搜索结果集合长度为:" + personEntities.size());
                        if (personEntities.size() != 0) {
                            setContent(personEntities);
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
     * 删除所有
     */
    public void deleteAll(View view) {
        Observable.just(view)
                .map(new Function<View, Integer>() {
                    @Override
                    public Integer apply(View view) throws Exception {
                        return AppDatabase
                                .getDatabase(mContext)
                                .personDao()
                                .deleteAll();
                    }
                })
                .compose(CustomScheculers.<Integer>iO2Main())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        ToastUtil.showShort("清空数据成功:" + integer);
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
     * 显示数据
     *
     * @param personEntities
     */
    private void setContent(List<PersonEntity> personEntities) {
        StringBuilder builder = new StringBuilder();
        for (PersonEntity personEntity : personEntities) {
            builder.append(personEntity.toString())
                    .append("\n")
                    .append("=============\n");
        }
        mTvContent.setText(builder.toString());
    }

}
