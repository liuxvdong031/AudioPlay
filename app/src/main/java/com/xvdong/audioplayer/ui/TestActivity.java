package com.xvdong.audioplayer.ui;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.xvdong.audioplayer.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by xvDong on 2023/12/5.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_test);
    }
}
