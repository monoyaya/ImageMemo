package com.jackg.programmers.challenges.imagememo.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MemoEntity.class}, version = 1, exportSchema = false)
@TypeConverters({MemoDAO.Converters.class})
public abstract class MemoDataBase extends RoomDatabase {

    static final String DB_NAME = "memo_db";
    abstract MemoDAO memoDAO();

    private static volatile MemoDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MemoDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (MemoDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                    MemoDataBase.class,
                                                    DB_NAME)
                                    .build();
                }
            }
        }

        return INSTANCE;
    }
}
