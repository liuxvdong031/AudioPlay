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
     * room 插入数据 主键相同则替换
     * @param bean 实体类
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAudio(AudioBean bean);

    @Query("SELECT * FROM audio")
    Observable<List<AudioBean>> getCollectAudio();

    @Query("SELECT EXISTS(SELECT 1 FROM audio WHERE id = :id LIMIT 1)")
    Single<Boolean> doesIdExist(long id);
}
