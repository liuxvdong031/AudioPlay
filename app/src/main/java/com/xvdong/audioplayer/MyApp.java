package com.xvdong.audioplayer;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.xvdong.audioplayer.util.Constants;

/**
 * Created by xvDong on 2023/9/28.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LogUtils.Config config = LogUtils.getConfig();
        config.setGlobalTag(Constants.LXD_AUDIO_PLAY);
    }
}
