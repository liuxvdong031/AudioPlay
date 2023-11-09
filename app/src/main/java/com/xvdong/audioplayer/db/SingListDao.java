package com.xvdong.audioplayer.db;

import com.xvdong.audioplayer.model.SingListBean;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by xvDong on 2023/11/7.
 */
@Dao
public interface SingListDao {

    // 查询所有歌单
    @Query("SELECT * FROM SingList")
    Flowable<List<SingListBean>> getAllPlaylists();

    // 根据id查询歌单
    @Query("SELECT * FROM SingList WHERE id = :id")
    Single<SingListBean> getPlaylistById(long id);

    // 插入歌单
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSingList(SingListBean playlist);

    // 更新歌单
    @Update
    Completable updatePlaylist(SingListBean playlist);

    // 删除歌单
    @Delete
    Completable deletePlaylist(SingListBean playlist);

}
