package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.databinding.ActivityPlaylistSongsBinding;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.SingListBean;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 歌单歌曲列表
 */
public class PlaylistSongsActivity extends AppCompatActivity {

    private ActivityPlaylistSongsBinding mBinding;
    private AudioDatabase mAudioDataBase;
    private SingListBean mSingListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_songs);
        initDatabase();
    }

    private void initDatabase() {
        DbUtils.getAudioDataBase(this, database -> {
            mAudioDataBase = database;
            initData();
        });
    }

    @SuppressLint("CheckResult")
    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mSingListBean = intent.getParcelableExtra(Constants.BEAN);
            mBinding.toolbar.setTitle(mSingListBean.getName());
            initListener(mSingListBean);
            refreshData();
        }
    }

    @SuppressLint("CheckResult")
    private void refreshData() {
        if (!TextUtils.isEmpty(mSingListBean.getSingIds())) {
            String singIds = mSingListBean.getSingIds();
            List<String> strings = Arrays.asList(singIds.split(","));
            Observable.fromIterable(strings)
                    .map(s -> {
                        return mAudioDataBase.mAudioDao()
                                .getAudioById(Long.parseLong(s));
                    })
                    .toList()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(audioBeans -> {
                        mBinding.rvSingList.setLayoutManager(new LinearLayoutManager(PlaylistSongsActivity.this));
                        AudioListAdapter adapter = new AudioListAdapter(PlaylistSongsActivity.this,
                                (ArrayList<AudioBean>) audioBeans,
                                mAudioDataBase,
                                false);
                        mBinding.rvSingList.setAdapter(adapter);
                    });
        }

    }

    private void initListener(SingListBean bean) {
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }

            @Override
            public void onRightClick(TitleBar titleBar) {
                Intent intent = new Intent(PlaylistSongsActivity.this, AudioSelectActivity.class);
                intent.putExtra(Constants.BEAN, bean);
                startActivityForResult(intent, 10010);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10010 && resultCode == 20010) {
            ArrayList<String> stringIds = data.getStringArrayListExtra(Constants.DATA);
            if (stringIds == null || stringIds.size() == 0) return;
            String join = String.join(",", stringIds);
            mSingListBean.setSingIds(join);
            mSingListBean.setSingCount(stringIds.size());
            DbUtils.getSingListDataBase(this, database -> {
                database.mSingListDao()
                        .insertSing(mSingListBean)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::refreshData);
            });
        }
    }
}