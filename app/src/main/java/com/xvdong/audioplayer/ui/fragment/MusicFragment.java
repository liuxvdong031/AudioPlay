package com.xvdong.audioplayer.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.adapter.AudioListAdapter;
import com.xvdong.audioplayer.databinding.FragmentMusicBinding;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.ui.AudioListLocalActivity;
import com.xvdong.audioplayer.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/10/27.
 */

public class MusicFragment extends Fragment {

    private AudioDatabase mDatabase;
    private FragmentMusicBinding mBinding;
    private MainActivity mActivity;
    private AudioListAdapter mAudioListAdapter;

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        // 初始化数据库
        AudioDatabase.getInstance(getActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(audioDatabase -> {
                    mDatabase = audioDatabase;
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initListener();
    }


    @SuppressLint("CheckResult")
    private void initData() {
        if (mDatabase != null) {
            mDatabase.mAudioDao()
                    .getAllAudio()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::changeUI);
        }else {
            mBinding.rlEmpty.postDelayed(() -> initData(),500);
        }
    }

    private void changeUI(List<AudioBean> list) {
        if (list == null || list.size() == 0) {
            mBinding.audioList.setVisibility(View.GONE);
            mBinding.rlEmpty.setVisibility(View.VISIBLE);
        } else {
            mBinding.audioList.setVisibility(View.VISIBLE);
            mBinding.rlEmpty.setVisibility(View.GONE);
            mAudioListAdapter = new AudioListAdapter(mActivity, (ArrayList<AudioBean>) list,mDatabase, true);
            mBinding.audioList.setLayoutManager(new LinearLayoutManager(mActivity));
            mBinding.audioList.setAdapter(mAudioListAdapter);
        }
    }


    private void initListener() {
        mBinding.rlEmpty.setOnClickListener(v -> {
            startActivity(new Intent(mActivity, AudioListLocalActivity.class));
        });
    }
}
