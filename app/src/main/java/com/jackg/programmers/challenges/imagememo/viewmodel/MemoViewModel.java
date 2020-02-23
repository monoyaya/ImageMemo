package com.jackg.programmers.challenges.imagememo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.data.MemoRepo;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {

    private MemoRepo mRepo;
    private LiveData<List<MemoEntity>> mData;
    private LiveData<MemoEntity> mEntity;

    public MemoViewModel(@NonNull Application application) {
        super(application);
        mRepo = new MemoRepo(application);
        mData = mRepo.getItems();
    }

    public LiveData<List<MemoEntity>> getData() {
        return mData;
    }

    public LiveData<MemoEntity> getMemo(long memoId) {
        mEntity = mRepo.getItemOne(memoId);

        return mEntity;
    }

    public long insertItem(MemoEntity entity){
        return mRepo.insert(entity);
    }

    public void insertItems(List<MemoEntity> entities){
        mRepo.insertAll(entities);
    }

    public void updateItem(MemoEntity entity){
        mRepo.update(entity);
    }

    public void deleteItem(MemoEntity entity){
        mRepo.delete(entity);
    }
}
