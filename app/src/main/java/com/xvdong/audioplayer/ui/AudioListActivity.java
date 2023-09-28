package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.utl.Constants;
import com.xvdong.audioplayer.utl.LxdPermissionUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.http.ApiService;
import com.xvdong.audioplayer.http.RetrofitClient;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.WYAudio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.internal.observers.BlockingBaseObserver;

public class AudioListActivity extends AppCompatActivity {

    private ArrayList<AudioBean> mDatas;

    private com.xvdong.audioplayer.databinding.ActivityAudioListBinding mBinding;
    private AudioListAdapter mAudioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_list);
        initView();
    }

    private void initView() {
        mDatas = new ArrayList<>();

        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mBinding.etSearch.getText().toString();
                getMusicList(s);
            }
        });

        mBinding.btnLocalSearch.setOnClickListener(view -> {
            LxdPermissionUtils.requestMediaAudioPermission();
            ArrayList<AudioBean> allAudioFiles = getAllAudioFiles();
            if (allAudioFiles.size() > 0) {
                view.setVisibility(View.GONE);
            }
            mDatas.addAll(allAudioFiles);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.audioList.setLayoutManager(linearLayoutManager);
        mAudioListAdapter = new AudioListAdapter(AudioListActivity.this, mDatas);
        mBinding.audioList.setAdapter(mAudioListAdapter);
    }

    private void getMusicList(String name){
        Observable<WYAudio> musicId = RetrofitClient.getInstance()
                .create(ApiService.class)
                .getMusicId(name);
        RetrofitClient.execute(musicId, new BlockingBaseObserver<WYAudio>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onNext(WYAudio result) {
                try {
                    List<WYAudio.ResultBean.SongsBean> songs = result.getResult().getSongs();
                    for (WYAudio.ResultBean.SongsBean song : songs) {
                        AudioBean audioBean = new AudioBean((long) song.getId(), song.getName(), "", "", song.getMp3Url());
                        Set<String> exceptionList = SPUtils.getInstance().getStringSet(Constants.EXCEPTION_LIST, new HashSet<>());
                        if (!exceptionList.contains(String.valueOf(audioBean.getId()))){
                            mDatas.add(audioBean);
                        }
                    }
                    mAudioListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    ToastUtils.showLong("result 解析异常: "+ e.getMessage() );
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
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DATA};
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
                        audioFiles.add(new AudioBean(id, displayName, artist, album, filePath));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                cursor.close();
            }
        return audioFiles;
    }
}