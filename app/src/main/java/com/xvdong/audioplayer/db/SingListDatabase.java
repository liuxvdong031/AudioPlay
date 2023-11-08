package com.xvdong.audioplayer.db;

import android.content.Context;

import com.xvdong.audioplayer.model.SingListBean;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/9/28.
 */

@Database(entities = {SingListBean.class}, version = 1, exportSchema = false)
public abstract class SingListDatabase extends RoomDatabase {
    public abstract SingListDao mSingListDao();

    private static SingListDatabase INSTANCE;
    private static final Object sLock = new Object();

    public static Single<SingListDatabase> getInstance(final Context context) {
        return Single.fromCallable(() -> {
            synchronized (sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SingListDatabase.class, "SingList").build();
                }
                return INSTANCE;
            }
        }).subscribeOn(Schedulers.io());
    }
}
