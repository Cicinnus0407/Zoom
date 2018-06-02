package com.cicinnus.zoom;

import android.os.AsyncTask;

import java.util.List;

/**
 * author cicinnus
 * date 2018/6/1
 */
public class CustomExecutors {


    /**
     * IO操作插入一条数据
     *
     * @param entity
     * @param insertListener
     */
    public static void insert(UserEntity entity, AddTask.OnInsertListener insertListener) {
        AddTask addTask = new AddTask();
        addTask.setOnInsertListener(insertListener);
        addTask.execute(entity);
    }

    /**
     * 根据id获取一条数据
     *
     * @param id
     * @param selectOneListener
     */
    public static void selectOne(int id, SelectOneTask.OnSelectOneListener selectOneListener) {
        SelectOneTask selectOneTask = new SelectOneTask();
        selectOneTask.setOnSelectOneListener(selectOneListener);
        selectOneTask.execute(id);
    }

    /**
     * 查询所有数据
     *
     * @param listener
     */
    public static void selectAll(SelectAllTask.OnSelectAllListener listener) {
        SelectAllTask selectAllTask = new SelectAllTask();
        selectAllTask.setOnSelectAllListener(listener);
        selectAllTask.execute();
    }

    /**
     * 根据id删除一条数据
     *
     * @param id
     * @param listener
     */
    public static void deleteById(int id, DeleteOneTask.OnDeleteListener listener) {
        DeleteOneTask deleteOneTask = new DeleteOneTask();
        deleteOneTask.setOnDeleteListener(listener);
        deleteOneTask.execute(id);
    }


    public static void selectByPageRows(int page, int rows, SelectByPageRowsTask.OnSelectByPageRowsListener listener) {
        SelectByPageRowsTask selectByPageRowsTaskTask = new SelectByPageRowsTask();
        selectByPageRowsTaskTask.setOnSelectByPageRowsListener(listener);
        int[] params = new int[]{page, rows};
        selectByPageRowsTaskTask.execute(params);
    }

    /**
     * 插入操作的后台任务
     */
    public static class AddTask extends AsyncTask<UserEntity, Void, Long> {

        private OnInsertListener onInsertListener;

        @Override
        protected Long doInBackground(UserEntity... userEntities) {
            return AppDatabase.getDatabase(App.getInstance())
                    .userDao()
                    .insert(userEntities[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (onInsertListener != null) {
                onInsertListener.onFinish(aLong);
            }
        }

        public void setOnInsertListener(OnInsertListener onInsertListener) {
            this.onInsertListener = onInsertListener;
        }

        public interface OnInsertListener {
            void onFinish(Long rowId);
        }
    }


    /**
     * 查询一条数据的后台任务
     */
    public static class SelectOneTask extends AsyncTask<Integer, Void, UserEntity> {
        private OnSelectOneListener onSelectOneListener;

        @Override
        protected UserEntity doInBackground(Integer... integers) {
            return AppDatabase.getDatabase(App.getInstance())
                    .userDao()
                    .selectOneById(integers[0]);
        }

        @Override
        protected void onPostExecute(UserEntity entity) {
            super.onPostExecute(entity);
            if (onSelectOneListener != null) {
                onSelectOneListener.onFinish(entity);
            }
        }

        public void setOnSelectOneListener(OnSelectOneListener onSelectOneListener) {
            this.onSelectOneListener = onSelectOneListener;
        }

        public interface OnSelectOneListener {
            void onFinish(UserEntity userEntity);
        }

    }


    /**
     * 查询所有数据后台任务
     */
    public static class SelectAllTask extends AsyncTask<Void, Void, List<UserEntity>> {

        private OnSelectAllListener onSelectAllListener;


        @Override
        protected List<UserEntity> doInBackground(Void... voids) {


            UserEntityCondition condition = new UserEntityCondition();


            condition.createCriteria()
                    .andLike("firstName", "zh")
                    .andEqualTo("lastName", "rong");


            return AppDatabase
                    .getDatabase(App.getInstance())
                    .userDao()

                    .selectByCondition(condition.build());
        }

        @Override
        protected void onPostExecute(List<UserEntity> userEntities) {
            super.onPostExecute(userEntities);
            if (onSelectAllListener != null) {
                onSelectAllListener.onFinish(userEntities);
            }
        }

        public void setOnSelectAllListener(OnSelectAllListener onSelectAllListener) {
            this.onSelectAllListener = onSelectAllListener;
        }

        public interface OnSelectAllListener {
            void onFinish(List<UserEntity> list);
        }
    }

    /**
     * 根据id删除数据后台任务
     */
    public static class DeleteOneTask extends AsyncTask<Integer, Void, Integer> {

        private OnDeleteListener onDeleteListener;

        @Override
        protected Integer doInBackground(Integer... integers) {
            return AppDatabase
                    .getDatabase(App.getInstance())
                    .userDao()
                    .deleteById(integers[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (onDeleteListener != null) {
                onDeleteListener.onFinish(integer);
            }
        }

        public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
            this.onDeleteListener = onDeleteListener;
        }

        public interface OnDeleteListener {
            void onFinish(int rowId);
        }
    }

    /**
     * 分页查询数据
     */
    public static class SelectByPageRowsTask extends AsyncTask<int[], Void, List<UserEntity>> {

        private OnSelectByPageRowsListener onSelectByPageRowsListener;

        @Override
        protected List<UserEntity> doInBackground(int[]... integers) {
            return AppDatabase.getDatabase(App.getInstance())
                    .userDao()
                    .selectByPageRows(integers[0][0], integers[0][1]);
        }

        @Override
        protected void onPostExecute(List<UserEntity> list) {
            super.onPostExecute(list);
            if (onSelectByPageRowsListener != null) {
                onSelectByPageRowsListener.onFinish(list);
            }
        }

        public void setOnSelectByPageRowsListener(OnSelectByPageRowsListener onSelectByPageRowsListener) {
            this.onSelectByPageRowsListener = onSelectByPageRowsListener;
        }

        public interface OnSelectByPageRowsListener {
            void onFinish(List<UserEntity> userEntities);
        }
    }
}
