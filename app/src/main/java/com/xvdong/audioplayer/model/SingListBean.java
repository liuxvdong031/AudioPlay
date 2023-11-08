package com.xvdong.audioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by xvDong on 2023/11/7.
 */
@Entity(tableName = "SingList")
public class SingListBean implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int singCount;  //歌曲数量
    private String description;
    private long createdAt;
    private String singIds;//所有的歌曲id,通过,分割

    // 构造函数
    public SingListBean(String name, long createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSingCount() {
        return singCount;
    }

    public void setSingCount(int singCount) {
        this.singCount = singCount;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getSingIds() {
        return singIds == null ? "" : singIds;
    }

    public void setSingIds(String singIds) {
        this.singIds = singIds;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.singCount);
        dest.writeString(this.description);
        dest.writeLong(this.createdAt);
        dest.writeString(this.singIds);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.name = source.readString();
        this.singCount = source.readInt();
        this.description = source.readString();
        this.createdAt = source.readLong();
        this.singIds = source.readString();
    }

    protected SingListBean(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.singCount = in.readInt();
        this.description = in.readString();
        this.createdAt = in.readLong();
        this.singIds = in.readString();
    }

    public static final Creator<SingListBean> CREATOR = new Creator<SingListBean>() {
        @Override
        public SingListBean createFromParcel(Parcel source) {
            return new SingListBean(source);
        }

        @Override
        public SingListBean[] newArray(int size) {
            return new SingListBean[size];
        }
    };
}
