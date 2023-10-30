package com.xvdong.audioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by xvDong on 2023/9/12.
 */

@Entity(tableName = "audio")
public class AudioBean implements Parcelable {
    @PrimaryKey
    private Long id;
    private String displayName; //显示名称
    private String artist;      //艺术家
    private String album;       //专辑
    private String path;        //歌曲路径
    private String lyricsPath;  //歌词
    private int duration;      //时长
    private boolean isCollect;  //是否收藏

    public AudioBean(Long id, String displayName, String artist, String album, String path) {
        this.id = id;
        this.displayName = displayName;
        this.artist = artist;
        this.album = album;
        this.path = path;
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
                return parts[1].substring(0, parts[1].lastIndexOf("."));
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

    public String getLyricsPath() {
        return lyricsPath == null ? "" : lyricsPath;
    }

    public void setLyricsPath(String lyricsPath) {
        this.lyricsPath = lyricsPath;
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
        dest.writeString(this.displayName);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeString(this.path);
        dest.writeString(this.lyricsPath);
        dest.writeInt(this.duration);
        dest.writeByte(this.isCollect ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = (Long) source.readValue(Long.class.getClassLoader());
        this.displayName = source.readString();
        this.artist = source.readString();
        this.album = source.readString();
        this.path = source.readString();
        this.lyricsPath = source.readString();
        this.duration = source.readInt();
        this.isCollect = source.readByte() != 0;
    }

    protected AudioBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.displayName = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.path = in.readString();
        this.lyricsPath = in.readString();
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
