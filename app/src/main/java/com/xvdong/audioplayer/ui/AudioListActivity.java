package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.db.AudioDao;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.http.ApiService;
import com.xvdong.audioplayer.http.RetrofitClient;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.WYAudio;
import com.xvdong.audioplayer.util.Constants;
import com.xvdong.audioplayer.util.LxdPermissionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;

public class AudioListActivity extends AppCompatActivity {

    private ArrayList<AudioBean> mDatas;

    private com.xvdong.audioplayer.databinding.ActivityAudioListBinding mBinding;
    private AudioListAdapter mAudioListAdapter;
    private AudioDatabase mDatabase;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_list);
        initView();
        // 初始化数据库
        AudioDatabase.getInstance(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(audioDatabase -> {
                    mDatabase = audioDatabase;
                });
    }

    @SuppressLint({"NotifyDataSetChanged", "CheckResult"})
    private void initView() {
        mDatas = new ArrayList<>();
        mBinding.btnSearch.setOnClickListener(view -> {
            String s = mBinding.etSearch.getText().toString();
            getMusicList(s);
        });

        mBinding.btnLocal.setOnClickListener(view -> {
            LxdPermissionUtils.requestMediaAudioPermission(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    LogUtils.d("已经获取到了权限");
                    ArrayList<AudioBean> allAudioFiles = getAllAudioFiles();
                    mAudioListAdapter.setNewData(allAudioFiles);

                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever,
                                     List<String> permissionsDenied) {
                    ToastUtils.showShort("用户拒绝了该权限");
                }
            });
        });

        mBinding.btnCollect.setOnClickListener(view -> {
            AudioDao audioDao = mDatabase.mAudioDao();
            audioDao.getAllAudio()
                    .subscribeOn(Schedulers.io()) // 在 IO 线程执行查询操作
                    .observeOn(AndroidSchedulers.mainThread()) // 在主线程更新 UI
                    .subscribe(audioList -> {
                        // 处理查询结果并更新 UI
                        mAudioListAdapter.setNewData(audioList);
                    }, throwable -> {
                        // 处理错误情况
                    });
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.audioList.setLayoutManager(linearLayoutManager);
        mAudioListAdapter = new AudioListAdapter(AudioListActivity.this, mDatas, mDatabase, false);
        mBinding.audioList.setAdapter(mAudioListAdapter);
    }

    private void getMusicList(String name) {
        Observable<WYAudio> musicId = RetrofitClient.getInstance()
                .create(ApiService.class)
                .getMusicId(name);
        RetrofitClient.execute(musicId, new BlockingBaseObserver<WYAudio>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onNext(WYAudio result) {
                try {
                    List<WYAudio.ResultBean.SongsBean> songs = result.getResult().getSongs();
                    mDatas.clear();
                    for (WYAudio.ResultBean.SongsBean song : songs) {
                        AudioBean audioBean = new AudioBean((long) song.getId(), song.getName(), "", "", song.getMp3Url());
                        Set<String> exceptionList = SPUtils.getInstance().getStringSet(Constants.EXCEPTION_LIST, new HashSet<>());
                        if (!exceptionList.contains(String.valueOf(audioBean.getId()))) {
                            mDatas.add(audioBean);
                        }
                    }
                    mAudioListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    ToastUtils.showLong("result 解析异常: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(e.getMessage());
            }
        });
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
                try {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    AudioBean audioBean = new AudioBean(id, displayName, artist, album, filePath);
                    int columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    if (columnIndex >= 0){
                        int duration = cursor.getInt(columnIndex);
                        audioBean.setDuration(duration);
                    }
                    audioFiles.add(audioBean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            cursor.close();
            for (AudioBean audioFile : audioFiles) {
                AudioDao audioDao = mDatabase.mAudioDao();
                audioDao.insertAudio(audioFile)
                        .subscribeOn(Schedulers.computation())
                        .subscribe();
            }
        }
        return audioFiles;
    }
}