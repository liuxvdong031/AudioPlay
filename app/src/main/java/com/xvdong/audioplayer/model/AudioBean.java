package com.xvdong.audioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by xvDong on 2023/9/12.
 */

@Entity(tableName = "audio")
public class AudioBean implements Parcelable {
    @PrimaryKey
    private Long id;
    @ColumnInfo(name = "WYCloudID")
    private Integer WYCloudID = 0; //网易云音乐ID
    private String displayName; //显示名称
    private String artist;      //艺术家
    private String album;       //专辑
    private String path;        //歌曲路径
    private String lyric;  //歌词
    private int duration;      //时长
    private boolean isCollect;  //是否收藏

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AudioBean)) return false;
        AudioBean audioBean = (AudioBean) o;
        return Objects.equals(getId(), audioBean.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public AudioBean(Long id, String displayName, String artist, String album, String path) {
        this.id = id;
        this.displayName = displayName;
        this.artist = artist;
        this.album = album;
        this.path = path;
    }

    public Integer getWYCloudID() {
        return WYCloudID;
    }

    public void setWYCloudID(Integer WYCloudID) {
        this.WYCloudID = WYCloudID;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public String getMusicName() {
        try {
            String[] parts = displayName.split(" - ");
            if (parts.length >= 2) {
                if (parts[1].contains(".")) {
                    return parts[1].substring(0, parts[1].lastIndexOf("."));
                } else {
                    return parts[1];
                }
            } else {
                // 如果没有正确的分隔符，则处理异常情况
                return displayName;
            }
        } catch (Exception e) {
            return displayName;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getArtist() {
        return artist == null ? "" : artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album == null ? "" : album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyric() {
        return lyric == null ? "" : lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.WYCloudID);
        dest.writeString(this.displayName);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeString(this.path);
        dest.writeString(this.lyric);
        dest.writeInt(this.duration);
        dest.writeByte(this.isCollect ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = (Long) source.readValue(Long.class.getClassLoader());
        this.WYCloudID = (Integer) source.readValue(Integer.class.getClassLoader());
        this.displayName = source.readString();
        this.artist = source.readString();
        this.album = source.readString();
        this.path = source.readString();
        this.lyric = source.readString();
        this.duration = source.readInt();
        this.isCollect = source.readByte() != 0;
    }

    protected AudioBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.WYCloudID = (Integer) in.readValue(Integer.class.getClassLoader());
        this.displayName = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.path = in.readString();
        this.lyric = in.readString();
        this.duration = in.readInt();
        this.isCollect = in.readByte() != 0;
    }

    public static final Creator<AudioBean> CREATOR = new Creator<AudioBean>() {
        @Override
        public AudioBean createFromParcel(Parcel source) {
            return new AudioBean(source);
        }

        @Override
        public AudioBean[] newArray(int size) {
            return new AudioBean[size];
        }
    };
}
