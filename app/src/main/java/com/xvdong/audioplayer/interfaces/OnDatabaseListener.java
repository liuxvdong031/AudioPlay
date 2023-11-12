package com.xvdong.audioplayer.interfaces;

import androidx.room.RoomDatabase;

/**
 * Created by xvDong on 2023/11/7.
 */

public interface OnDatabaseListener<T extends RoomDatabase> {
    void onDatabaseListener(T database);
}
