package com.xvdong.audioplayer.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.FragmentProfileBinding;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.ui.SettingActivity;
import com.xvdong.audioplayer.ui.SingerListActivity;
import com.xvdong.audioplayer.util.GlideEngine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * Created by xvDong on 2023/10/27.
 */

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private AudioDatabase mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GlideEngine.getInstance().displayCircleImage(R.mipmap.scarecrow, mBinding.ivAvatar);
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mBinding.ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingActivity.class));
        });
        mBinding.btnArtist.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SingerListActivity.class));
        });
    }

}
