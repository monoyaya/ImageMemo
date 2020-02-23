package com.jackg.programmers.challenges.imagememo.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MemoRepo {

    private MemoDAO mDao;
    private LiveData<List<MemoEntity>> mItems;
    private LiveData<MemoEntity> mItem;

    public MemoRepo(Context context) {
        MemoDataBase mDB = MemoDataBase.getDatabase(context);
        mDao = mDB.memoDAO();
        mItems = mDao.selectAll();
    }

    public LiveData<List<MemoEntity>> getItems() {
        return mItems;
    }

    public LiveData<MemoEntity> getItemOne(long memoId) {
        mItem = mDao.selectOne(memoId);

        return mItem;
    }

    public long insert(MemoEntity entity) {
        Future<Long> future = MemoDataBase.databaseExecutor.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDao.insert(entity);
            }
        });

        try {
            return  future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void insertAll(List<MemoEntity> entities) {
        MemoDataBase.databaseExecutor.execute(() -> {
            mDao.insertAll(entities);
        });
    }

    public void update(MemoEntity entity) {
        MemoDataBase.databaseExecutor.execute(() -> {
            mDao.update(entity);
        });
    }

    public void delete(MemoEntity entity) {
        MemoDataBase.databaseExecutor.execute(() -> {
            mDao.delete(entity);
        });
    }
}
