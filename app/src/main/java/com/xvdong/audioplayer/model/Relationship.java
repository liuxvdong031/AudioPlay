package com.xvdong.audioplayer.model;

/**
 * Created by xvDong on 2023/11/12.
 * 歌单与歌手的关系
 */

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Relationship", primaryKeys = {"audioId", "singListId"})
public class Relationship {
    @ColumnInfo(name = "audioId")
    private @NonNull Long audioId;
    @ColumnInfo(name = "singListId")
    private @NonNull Long singListId;

    public Relationship(@NonNull Long audioId, @NonNull Long singListId) {
        this.audioId = audioId;
        this.singListId = singListId;
    }

    public Long getAudioId() {
        return audioId;
    }

    public void setAudioId(Long audioId) {
        this.audioId = audioId;
    }

    public Long getSingListId() {
        return singListId;
    }

    public void setSingListId(Long singListId) {
        this.singListId = singListId;
    }
}
