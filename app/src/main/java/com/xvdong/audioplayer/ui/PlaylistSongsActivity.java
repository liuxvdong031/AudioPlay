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
    private void refreshData(){
        if (!TextUtils.isEmpty(mSingListBean.getSingIds())) {
            String singIds = mSingListBean.getSingIds();
            String[] split = singIds.split(",");
            List<String> strings = Arrays.asList(split);
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
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> stringIds = data.getStringArrayListExtra(Constants.DATA);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < stringIds.size(); i++) {
            if (i != stringIds.size() - 1) {
                stringBuffer.append(stringIds.get(i));
                stringBuffer.append(",");
            } else {
                stringBuffer.append(stringIds.get(i));
            }
        }
        String s = stringBuffer.toString();
        mSingListBean.setSingIds(s);
        mSingListBean.setSingCount(stringIds.size());
        DbUtils.getSingListDataBase(this, database -> {
            database.mSingListDao()
                    .insertSing(mSingListBean)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        refreshData();
                    });
        });
    }
}