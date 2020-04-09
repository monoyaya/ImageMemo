package com.jackg.programmers.challenges.imagememo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@Dao
public interface MemoDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(MemoEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<MemoEntity> entities);

    @Update
    void update(MemoEntity entity);

    @Delete
    void delete(MemoEntity entity);

    @Query("SELECT * FROM memo ORDER BY memoId DESC")
    LiveData<List<MemoEntity>> selectAll();

    @Query("SELECT * FROM memo WHERE memoId = :memoId")
    LiveData<MemoEntity> selectOne(long memoId);

    class Converters {

        @TypeConverter
        public static List<String> fromString(String src) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            return new Gson().fromJson(src, listType);
        }

        @TypeConverter
        public static String fromList(List<String> list) {
            return (new Gson()).toJson(list);
        }
    }
}
