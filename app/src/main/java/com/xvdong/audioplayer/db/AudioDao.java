package com.xvdong.audioplayer.db;

import com.xvdong.audioplayer.model.AudioBean;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by xvDong on 2023/9/28.
 */

@Dao
public interface AudioDao {

    /**
     * 查询id是否再数据库中存在
     * @param id 歌曲id与Android媒体类一致
     */
    @Query("SELECT EXISTS(SELECT 1 FROM audio WHERE id = :id LIMIT 1)")
    Single<Boolean> doesIdExist(long id);
    /**
     * room 插入数据 主键相同则替换
     * @param bean 实体类
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAudio(AudioBean bean);

    /**
     * 查询表中所有数据
     * @return
     */
    @Query("SELECT * FROM audio")
    Observable<List<AudioBean>> getAllAudio();

    /**
     * 查询所有的艺术家
     */
    @Query("SELECT DISTINCT artist FROM audio")
    Observable<List<String>>  getAllArtists();

    /**
     *  根据artist查询相同artist的所有歌曲
     * @param artistName 艺术家名称
     */
    @Query("SELECT * FROM audio WHERE artist = :artistName")
    Observable<List<AudioBean>> getAudiosByArtist(String artistName);

    @Query("SELECT * FROM audio WHERE isCollect = 1")
    Observable<List<AudioBean>> getAllCollectedMusic();

}
