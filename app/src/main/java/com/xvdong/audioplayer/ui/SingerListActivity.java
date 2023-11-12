package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.adapter.SingerAdapter;
import com.xvdong.audioplayer.databinding.ActivitySingerListBinding;
import com.xvdong.audioplayer.db.AppDataBase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SingerListActivity extends AppCompatActivity {

    private ActivitySingerListBinding mBinding;
    private AppDataBase mDatabase;
    private List<String> mSingers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_singer_list);
        initDatabase();
    }

    //初始化视图
    private void initView() {
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                finish();
            }
        });
    }

    //初始化数据库
    @SuppressLint("CheckResult")
    private void initDatabase() {
        DbUtils.getAppDataBase(this, database -> {
            mDatabase = database;
            initData();
        });
    }

    //获取所有的歌手名称
    @SuppressLint("CheckResult")
    private void initData() {
        DbUtils.getAllArtists(mDatabase,data -> {
            mSingers = data;
            initSingerAdapter();
        });
    }

    //初始化歌手的适配器
    private void initSingerAdapter() {
        initView();
        mBinding.toolbar.setTitle("歌手列表");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        mBinding.rvSinger.setLayoutManager(layoutManager);
        SingerAdapter adapter = new SingerAdapter(mDatabase, mSingers, singer -> {
            getSingerMusics(singer);
        });
        mBinding.rvSinger.setAdapter(adapter);
    }

    //获取歌手所有的歌曲
    @SuppressLint("CheckResult")
    private void getSingerMusics (String singer){
        DbUtils.getAudiosByArtist(mDatabase,singer,data -> {
            updateAdapter(singer,data);
        });
    }

    //更新为歌曲的适配器
    private void updateAdapter(String singer, List<AudioBean> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        mBinding.rvSinger.setLayoutManager(layoutManager);
        AudioListAdapter audioListAdapter = new AudioListAdapter(this, (ArrayList<AudioBean>) list,mDatabase,false);
        mBinding.rvSinger.setAdapter(audioListAdapter);
        mBinding.toolbar.setTitle(singer);
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                initSingerAdapter();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mBinding.toolbar.getTitle().equals("歌手列表")){
            super.onBackPressed();
        }else {
            initSingerAdapter();
        }
    }
}