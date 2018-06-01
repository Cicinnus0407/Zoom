package com.cicinnus.zoom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


/**
 * Room扩展库测试
 * author cicinnus
 */
public class RoomTestActivity extends AppCompatActivity {


    private static final String TAG = RoomTestActivity.class.getSimpleName();

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
     * 页数
     */
    EditText etPage;


    /**
     * 条数
     */
    EditText etRows;

    /**
     * 输出内容
     */
    TextView mTvContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etId = findViewById(R.id.et_id);
        etPage = findViewById(R.id.et_page);
        etRows = findViewById(R.id.et_rows);
        mTvContext = findViewById(R.id.tv_content);
        etFirstName.setText("first");
        etLastName.setText("last");


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

        CustomExecutors.insert(userEntity, new CustomExecutors.AddTask.OnInsertListener() {
            @Override
            public void onFinish(Long rowId) {
                ToastUtil.showShort("插入成功" + rowId);
            }
        });
    }


    /**
     * 根据id查询单个
     *
     * @param view
     */
    public void queryOne(View view) {
        String id = etId.getText().toString();
        CustomExecutors.selectOne(Integer.parseInt(id), new CustomExecutors.SelectOneTask.OnSelectOneListener() {
            @Override
            public void onFinish(UserEntity userEntity) {
                //查询数据有可能为空
                if (userEntity != null) {
                    mTvContext.setText(userEntity.toString());
                }
            }
        });

    }

    /**
     * 查询所有
     *
     * @param view
     */
    public void loadAll(View view) {
        CustomExecutors.selectAll(new CustomExecutors.SelectAllTask.OnSelectAllListener() {
            @Override
            public void onFinish(List<UserEntity> list) {
                setContent(list);
            }
        });
    }

    /**
     * 删除一条
     *
     * @param view
     */
    public void delete(View view) {
        int id = Integer.parseInt(etId.getText().toString());
        CustomExecutors.deleteById(id, new CustomExecutors.DeleteOneTask.OnDeleteListener() {
            @Override
            public void onFinish(int rowId) {
                ToastUtil.showShort("删除任务结束:" + rowId);
            }
        });
    }

    /**
     * 分页查询
     *
     * @param view
     */
    public void selectByPageAndRows(View view) {

        final int page = Integer.parseInt(etPage.getText().toString());
        final int rows = Integer.parseInt(etRows.getText().toString());
        CustomExecutors.selectByPageRows(page, rows, new CustomExecutors.SelectByPageRowsTask.OnSelectByPageRowsListener() {
            @Override
            public void onFinish(List<UserEntity> userEntities) {
                ToastUtil.showShort(userEntities.size() + "");
                setContent(userEntities);
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
        mTvContext.setText(builder.toString());
    }


}
