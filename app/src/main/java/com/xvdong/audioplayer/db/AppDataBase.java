package com.xvdong.audioplayer.db;

import android.content.Context;

import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.Relationship;
import com.xvdong.audioplayer.model.SingListBean;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/11/12.
 */
@Database(entities = {AudioBean.class, SingListBean.class,Relationship.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract RelationshipDao mRelationshipDao();
    public abstract AudioDao mAudioDao();
    public abstract SingListDao mSingListDao();
    private static AppDataBase INSTANCE;
    private static final Object sLock = new Object();

    public static Single<AppDataBase> getInstance(final Context context) {
        return Single.fromCallable(() -> {
            synchronized (sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "lxdAudioPlay").build();
                }
                return INSTANCE;
            }
        }).subscribeOn(Schedulers.io());
    }

}
