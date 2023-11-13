package com.xvdong.audioplayer.db;

import android.annotation.SuppressLint;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.xvdong.audioplayer.interfaces.OnDataListener;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.Relationship;
import com.xvdong.audioplayer.model.SingListBean;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/11/7.
 */

public class DbUtils {

    @SuppressLint("CheckResult")
    public static void getAppDataBase(Context context, @NonNull OnDatabaseListener<AppDataBase> listener) {
        AppDataBase.getInstance(context)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDatabaseListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    public static void insertAudio(AppDataBase database,AudioBean audioBean){
        database.mAudioDao()
                .insertAudio(audioBean)
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public static void getAllAudio(AppDataBase database, @NonNull OnDataListener<List<AudioBean>> listener) {
        database.mAudioDao()
                .getAllAudio()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    public static void getAllArtists(AppDataBase database, @NonNull OnDataListener<List<String>> listener) {
        database.mAudioDao()
                .getAllArtists()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    public static void getAudiosByArtist(AppDataBase database,String artistName ,@NonNull OnDataListener<List<AudioBean>> listener) {
        database.mAudioDao()
                .getAudiosByArtist(artistName)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener, throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    public static void getAllCollectedMusic(AppDataBase database,@NonNull OnDataListener<List<AudioBean>> listener) {
        database.mAudioDao()
                .getAllCollectedMusic()
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
    public static void insertSingList(AppDataBase database, SingListBean bean) {
        database.mSingListDao()
                .insertSingList(bean)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public static void getAllPlaylists(AppDataBase database, @NonNull OnDataListener<List<SingListBean>> listener) {
        database.mSingListDao()
                .getAllPlaylists()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener,throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }


    /**
     * --------------------------------歌单与歌手的关系数据库------------------------------------------
     */

    /**
     * 插入一条歌单与歌手关系数据
     * @param database          DB
     * @param relationship      关系
     */
    public static void insertRelationship(AppDataBase database,Relationship relationship){
        database.mRelationshipDao()
                .insertRelationship(relationship)
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    /**
     * 根据歌单ID获取所有的歌曲
     * @param database      DB
     * @param singListId    歌单ID
     * @param listener      回调
     */
    @SuppressLint("CheckResult")
    public static void getAudiosBySingList(AppDataBase database,Long singListId, @NonNull OnDataListener<List<AudioBean>> listener) {
        database.mRelationshipDao()
                .getAudiosBySingList(singListId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener,throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

    /**
     * 根据歌曲ID获取所有的包含该ID的歌单
     * @param database  DB
     * @param audioId   歌曲ID
     * @param listener  回调
     */
    @SuppressLint("CheckResult")
    public static void getSingListByAudio(AppDataBase database,Long audioId, @NonNull OnDataListener<List<SingListBean>> listener) {
        database.mRelationshipDao()
                .getSingListByAudio(audioId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onDataListener,throwable -> {
                    LogUtils.e("数据库访问异常: " + throwable.getMessage());
                });
    }

}
