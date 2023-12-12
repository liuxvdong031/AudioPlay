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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.ui.AudioDetailActivity;
import com.xvdong.audioplayer.util.Constants;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Created by xvDong on 2023/10/20.
 */

public class ForegroundService extends Service {

    private final String CHANNEL_ID = "lxdVideoPlayChannel";
    private final int NOTIFICATION_ID = 100;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            createNotification(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification(Intent intent) {
        String musicName = intent.getStringExtra(Constants.MUSIC_NAME);
        String musicArtist = intent.getStringExtra(Constants.MUSIC_ARTIST);
        Intent nfIntent = new Intent(this, AudioDetailActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 1001, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 1001, nfIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            //注意在API 26 Android要求创建Notification必须要有channelId 如果没有会抛出RemoteException异常
            //1 获取channelId
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "音乐播放", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            //2 创建Notification 对象.
            builder = new NotificationCompat.Builder(getApplicationContext(), channel.getId());
        } else {
            //3 API 26之前没有要求channel对象 创建Notification 对象.
            builder = new NotificationCompat.Builder(this.getApplicationContext());
        }
        Bitmap largeBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo);
        builder.setContentIntent(pendingIntent)
                .setLargeIcon(largeBitmap)
                .setContentTitle(musicName)
                .setSmallIcon(R.mipmap.logo)
                .setContentText(musicArtist)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        // 停止前台服务--参数：表示是否移除之前的通知
        stopForeground(true);
        super.onDestroy();
    }

    public class MusicBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

}
