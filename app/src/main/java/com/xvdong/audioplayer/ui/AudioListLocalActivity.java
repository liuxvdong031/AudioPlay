package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.bus.RxBus;
import com.xvdong.audioplayer.databinding.ActivityAudioListBinding;
import com.xvdong.audioplayer.db.AppDataBase;
import com.xvdong.audioplayer.db.AudioDao;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.event.AudioEvent;
import com.xvdong.audioplayer.util.LxdPermissionUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.schedulers.Schedulers;

/**
 * 扫描本地歌曲
 */
public class AudioListLocalActivity extends AppCompatActivity {

    private ActivityAudioListBinding mBinding;
    private AudioListAdapter mAudioListAdapter;
    private AppDataBase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_list);
        initDatabase();
        initView();
    }

    // 初始化数据库
    private void initDatabase() {
        DbUtils.getAppDataBase(this, database -> mDatabase = database);
    }

    private void initView() {
        ArrayList<AudioBean> audioBeans = new ArrayList<>();
        mBinding.btnLocal.setOnClickListener(view -> {
            LxdPermissionUtils.requestMediaAudioPermission(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    LogUtils.d("已经获取到了权限");
                    ArrayList<AudioBean> allAudioFiles = getAllAudioFiles();
                    insertToDbCheckId(allAudioFiles);
                    mBinding.btnLocal.setVisibility(View.GONE);
                    mAudioListAdapter.setNewData(allAudioFiles);
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever,
                                     List<String> permissionsDenied) {
                    ToastUtils.showShort("用户拒绝了该权限");
                }
            });
        });
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.audioList.setLayoutManager(linearLayoutManager);
        mAudioListAdapter = new AudioListAdapter(AudioListLocalActivity.this, audioBeans, mDatabase, false);
        mBinding.audioList.setAdapter(mAudioListAdapter);
    }

    public ArrayList<AudioBean> getAllAudioFiles() {
        ArrayList<AudioBean> audioFiles = new ArrayList<>();
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,// 音乐文件标题
                MediaStore.Audio.Media.ARTIST,// 音乐文件艺术家
                MediaStore.Audio.Media.ALBUM,//音乐文件的标题
                MediaStore.Audio.Media.DURATION, // 音乐文件时长
                MediaStore.Audio.Media.DATA};//音乐文件的路径
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                AudioBean audioBean = new AudioBean(id, displayName, artist, album, filePath);
                int columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                if (columnIndex >= 0) {
                    int duration = cursor.getInt(columnIndex);
                    audioBean.setDuration(duration);
                }
                audioFiles.add(audioBean);
            }
            cursor.close();
        }
        return audioFiles;
    }

    @SuppressLint("CheckResult")
    private void insertToDbCheckId(List<AudioBean> audioFiles) {
        if (mDatabase != null) {
            for (AudioBean audioFile : audioFiles) {
                AudioDao audioDao = mDatabase.mAudioDao();
                audioDao.doesIdExist(audioFile.getId())
                        .subscribeOn(Schedulers.computation())
                        .subscribe(exist -> {
                            if (!exist) {
                                DbUtils.insertAudio(mDatabase, audioFile);
                            }
                        });
            }
            RxBus.getDefault().postSticky(new AudioEvent());
        }
    }
}