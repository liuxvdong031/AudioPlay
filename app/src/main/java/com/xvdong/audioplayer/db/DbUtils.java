package com.xvdong.audioplayer.db;

import android.annotation.SuppressLint;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.xvdong.audioplayer.model.AudioBean;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/11/7.
 */

public class DbUtils {

    @SuppressLint("CheckResult")
    public static void getAudioDataBase(Context context, @NonNull OnDatabaseListener<AudioDatabase> listener) {
        AudioDatabase.getInstance(context)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDatabaseListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    public static void insertAudio(AudioDatabase database,AudioBean audioBean){
        database.mAudioDao()
                .insertAudio(audioBean)
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public static void getAllAudio(AudioDatabase database, @NonNull OnDataListener<List<AudioBean>> listener) {
        database.mAudioDao()
                .getAllAudio()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    /**
     * -----------------------------------分割线,下边为歌单数据库--------------------------------------
     */
    @SuppressLint("CheckResult")
    public static void getSingListDataBase(Context context, OnDatabaseListener<SingListDatabase> listener) {
        SingListDatabase.getInstance(context)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(audioDatabase -> {
                    if (listener != null) {
                        listener.onDatabaseListener(audioDatabase);
                    }
                }, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }


}
