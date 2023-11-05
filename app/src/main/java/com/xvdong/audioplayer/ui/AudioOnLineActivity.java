package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.databinding.ActivityAudioOnLineBinding;
import com.xvdong.audioplayer.http.ApiService;
import com.xvdong.audioplayer.http.RetrofitClient;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.WYAudio;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.internal.observers.BlockingBaseObserver;

public class AudioOnLineActivity extends AppCompatActivity {

    private ActivityAudioOnLineBinding mBinding;
    private ArrayList<AudioBean> mDatas;
    private AudioListAdapter mAudioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_on_line);
        initView();
    }

    private void initView() {
        mDatas = new ArrayList<>();
        mBinding.btnSearch.setOnClickListener(view -> {
            KeyboardUtils.hideSoftInput(view);
            String s = mBinding.etSearch.getText().toString();
            getMusicList(s);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.audioList.setLayoutManager(linearLayoutManager);
        mAudioListAdapter = new AudioListAdapter(this, mDatas, null, false);
        mBinding.audioList.setAdapter(mAudioListAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMusicList(String name) {
        Observable<WYAudio> musicId = RetrofitClient.getInstance()
                .create(ApiService.class)
                .getMusicId(name);
        RetrofitClient.execute(musicId, new BlockingBaseObserver<WYAudio>() {
            @Override
            public void onNext(WYAudio result) {
                try {
                    List<WYAudio.ResultBean.SongsBean> songs = result.getResult().getSongs();
                    mDatas.clear();
                    for (WYAudio.ResultBean.SongsBean song : songs) {
                        String artist = "";
                        if (song.getArtists().size() > 0) {
                            artist = song.getArtists().get(0).getName();
                        }
                        AudioBean audioBean = new AudioBean((long) song.getId(),
                                artist + " - " + song.getName(),
                                artist,
                                song.getAlbum().getName(),
                                "https://music.163.com/song/media/outer/url?id=" + song.getId() + ".mp3");
                        audioBean.setWYCloudID(song.getId());
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
}