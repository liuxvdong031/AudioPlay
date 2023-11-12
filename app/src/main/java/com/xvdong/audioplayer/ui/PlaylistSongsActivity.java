package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.databinding.ActivityPlaylistSongsBinding;
import com.xvdong.audioplayer.db.AppDataBase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.Relationship;
import com.xvdong.audioplayer.model.SingListBean;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 歌单歌曲列表
 */
public class PlaylistSongsActivity extends AppCompatActivity {

    private ActivityPlaylistSongsBinding mBinding;
    private AppDataBase mAppDataBase;
    private SingListBean mSingListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_songs);
        initDatabase();
    }

    private void initDatabase() {
        DbUtils.getAppDataBase(this, database -> {
            mAppDataBase = database;
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
        if (mSingListBean != null) {
            DbUtils.getAudiosBySingList(mAppDataBase, mSingListBean.getId(), data -> {
                mBinding.rvSingList.setLayoutManager(new LinearLayoutManager(PlaylistSongsActivity.this));
                AudioListAdapter adapter = new AudioListAdapter(PlaylistSongsActivity.this,
                        (ArrayList<AudioBean>) data,
                        mAppDataBase,
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
            ArrayList<AudioBean> selectedAudios = data.getParcelableArrayListExtra(Constants.DATA);
            if (selectedAudios == null || selectedAudios.size() == 0) return;
            //创建歌单与歌手的关系
            for (AudioBean audioBean : selectedAudios) {
                Relationship relationship = new Relationship(audioBean.getId(), mSingListBean.getId());
                DbUtils.insertRelationship(mAppDataBase, relationship);
            }
            //更新歌单歌曲数量
            mSingListBean.setSingCount(selectedAudios.size());
            DbUtils.insertSingList(mAppDataBase,mSingListBean);
        }
    }
}