package com.xvdong.audioplayer.ui;

import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioSelectAdapter;
import com.xvdong.audioplayer.databinding.ActivityAudioSelectMainBinding;
import com.xvdong.audioplayer.db.AppDataBase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.SingListBean;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 选择音乐加入歌单
 */
public class AudioSelectActivity extends AppCompatActivity {

    private ActivityAudioSelectMainBinding mBinding;
    private AppDataBase mAppDataBase;
    private AudioSelectAdapter mAudioSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_select_main);
        initDatabase();
        initListener();
    }

    private void initListener() {
        mBinding.btnSubmit.setOnClickListener(v -> {
            if (mAudioSelectAdapter != null) {
                Intent intent = new Intent();
                ArrayList<AudioBean> selectedAudios = (ArrayList<AudioBean>) mAudioSelectAdapter.getSelectedAudios();
                if (selectedAudios.size() > 0) {
                    intent.putExtra(Constants.DATA, selectedAudios);
                    setResult(20010, intent);
                    finish();
                } else {
                    ToastUtils.showShort("请选择音乐");
                }
            }
        });
    }

    private void initDatabase() {
        DbUtils.getAppDataBase(this, database -> {
            mAppDataBase = database;
            initData();
        });
    }

    private void initData() {
        DbUtils.getAllAudio(mAppDataBase, this::initRecycleView);
    }

    private void initRecycleView(List<AudioBean> allAudioBeans) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvAudioList.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        if (intent != null) {
            SingListBean singListBean = intent.getParcelableExtra(Constants.BEAN);
            DbUtils.getAudiosBySingList(mAppDataBase, singListBean.getId(), singListData -> {
                mAudioSelectAdapter = new AudioSelectAdapter(this, allAudioBeans, singListData);
                mBinding.rvAudioList.setAdapter(mAudioSelectAdapter);
            });
        }

    }
}