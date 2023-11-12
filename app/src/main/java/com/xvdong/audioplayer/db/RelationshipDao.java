package com.xvdong.audioplayer.db;

import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.Relationship;
import com.xvdong.audioplayer.model.SingListBean;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Created by xvDong on 2023/11/12.
 */
@Dao
public interface RelationshipDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRelationship(Relationship relationship);

    // 根据歌单 id 获取其所有歌曲
    @Transaction
    @Query("SELECT * FROM audio INNER JOIN Relationship ON audio.id = Relationship.audioId WHERE Relationship.singListId = :singListId")
    Flowable<List<AudioBean>> getAudiosBySingList(Long singListId);

    // 根据歌曲 id 获取其所在的所有歌单
    @Transaction
    @Query("SELECT * FROM singlist INNER JOIN Relationship ON singlist.id = Relationship.singListId WHERE Relationship.audioId = :audioId")
    Flowable<List<SingListBean>> getSingListByAudio(Long audioId);
}
