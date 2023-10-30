package com.xvdong.audioplayer.util;

import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import androidx.core.app.ActivityCompat;

/**
 * Created by xvDong on 2023/9/13.
 */

public class LxdPermissionUtils {
    public static void requestMediaAudioPermission(PermissionUtils.FullCallback callback) {
        String permissionString = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionString = android.Manifest.permission.READ_MEDIA_AUDIO;
        } else {
            permissionString = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        String finalPermissionString = permissionString;
        PermissionUtils.permission(finalPermissionString)
                .rationale((activity, shouldRequest) -> {
                    LogUtils.d("没有权限,去请求权限");
                    ActivityCompat.requestPermissions(activity, new String[]{
                            finalPermissionString
                    }, 10086);
                })
                .callback(callback)
                .request();

    }
}
