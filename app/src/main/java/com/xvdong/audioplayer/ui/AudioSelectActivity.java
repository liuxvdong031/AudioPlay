package com.xvdong.audioplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioSelectAdapter;
import com.xvdong.audioplayer.databinding.ActivityAudioSelectMainBinding;
import com.xvdong.audioplayer.db.AudioDatabase;
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
    private AudioDatabase mAudioDatabase;
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
            if (mAudioSelectAdapter != null){
                Intent intent = new Intent();
                ArrayList<Long> selectedId = (ArrayList<Long>) mAudioSelectAdapter.getSelectedId();
                ArrayList<String> stringIds = new ArrayList<>();
                if (selectedId.size() > 0){
                    for (Long aLong : selectedId) {
                        stringIds.add(String.valueOf(aLong));
                    }
                    intent.putExtra(Constants.DATA,stringIds);
                    setResult(20010,intent);
                    finish();
                }else {
                    ToastUtils.showShort("请选择音乐");
                }
            }
        });
    }

    private void initDatabase() {
        DbUtils.getAudioDataBase(this,database -> {
            mAudioDatabase = database;
            initData();
        });
    }

    private void initData() {
        DbUtils.getAllAudio(mAudioDatabase, this::initRecycleView);
    }

    private void initRecycleView(List<AudioBean> data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvAudioList.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        if (intent != null){
            SingListBean singListBean =  intent.getParcelableExtra(Constants.BEAN);
            String singIds = singListBean.getSingIds();
            ArrayList<Long> list = new ArrayList<>();
            if (!TextUtils.isEmpty(singIds)){
                String[] split = singIds.split(",");
                for (String s : split) {
                    long id = Long.parseLong(s);
                    list.add(id);
                }
            }
            mAudioSelectAdapter = new AudioSelectAdapter(this, data, list);
            mBinding.rvAudioList.setAdapter(mAudioSelectAdapter);
        }

    }
}