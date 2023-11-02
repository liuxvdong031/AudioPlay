package com.xvdong.audioplayer.ui;

import android.content.Intent;
import android.os.Bundle;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ActivitySettingBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        initData();
    }

    private void initData() {
        mBinding.toolbar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(TitleBar titleBar) {
                OnTitleBarListener.super.onLeftClick(titleBar);
                finish();
            }
        });

        mBinding.searchMusic.setOnClickListener(v -> {
            startActivity(new Intent(this, AudioListLocalActivity.class));
        });
        mBinding.searchNetMusic.setOnClickListener(v -> {
            startActivity(new Intent(this, AudioOnLineActivity.class));
        });
    }


}