package com.xvdong.audioplayer.db;

import android.content.Context;

import com.xvdong.audioplayer.model.AudioBean;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/9/28.
 */

@Database(entities = {AudioBean.class}, version = 1)
public abstract class AudioDatabase extends RoomDatabase {
    public abstract AudioDao mAudioDao();

    private static AudioDatabase INSTANCE;
    private static final Object sLock = new Object();

    public static Single<AudioDatabase> getInstance(final Context context) {
        return Single.fromCallable(() -> {
            synchronized (sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AudioDatabase.class, "audio").build();
                }
                return INSTANCE;
            }
        }).subscribeOn(Schedulers.io());
    }
}
