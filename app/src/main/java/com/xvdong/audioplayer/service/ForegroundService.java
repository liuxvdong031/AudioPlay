package com.xvdong.audioplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.ui.AudioDetailActivity;

import androidx.annotation.Nullable;

/**
 * Created by xvDong on 2023/10/20.
 */

public class ForegroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        Intent nfIntent = new Intent(this, AudioDetailActivity.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, nfIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            //注意在API 26 Android要求创建Notification必须要有channelId 如果没有会抛出RemoteException异常
            //1 获取channelId
            NotificationChannel channel = null;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            channel = new NotificationChannel("foreground", "foregroundName", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            String id = channel.getId();
            //2 创建Notification 对象.
            builder = new Notification.Builder(this.getApplicationContext(), id);
        } else {
            //3 API 26之前没有要求channel对象 创建Notification 对象.
            builder = new Notification.Builder(this.getApplicationContext());
        }
        Bitmap largeBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo);
        builder.setContentIntent(activity)
                .setLargeIcon(largeBitmap)
                .setContentTitle("ContentTitle")
                .setSmallIcon(R.mipmap.logo)
                .setContentText("ContentText")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        startForeground(100, notification);
    }

    @Override
    public void onDestroy() {
        // 停止前台服务--参数：表示是否移除之前的通知
        stopForeground(true);
        super.onDestroy();
    }

}
