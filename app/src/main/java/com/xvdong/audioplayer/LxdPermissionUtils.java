package com.xvdong.audioplayer;

import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import androidx.core.app.ActivityCompat;

/**
 * Created by xvDong on 2023/9/13.
 */

public class LxdPermissionUtils {
    public static void requestMediaAudioPermission() {
        String permissionString = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissionString = android.Manifest.permission.READ_MEDIA_AUDIO;
        }else {
            permissionString = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        String finalPermissionString = permissionString;
        PermissionUtils.permission(finalPermissionString)
                .rationale((activity, shouldRequest) -> {
                    ToastUtils.showShort( "没有权限,去请求权限");
                    ActivityCompat.requestPermissions( activity,new String[]{
                            finalPermissionString
                    },10086);
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        LogUtils.d(permissionsGranted);
                        ToastUtils.showShort( "已经获取到了权限");
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        LogUtils.d(permissionsDeniedForever, permissionsDenied);
                        if (permissionsDeniedForever.contains(finalPermissionString)) {
                            ToastUtils.showShort( "永久拒绝了该权限");
                        } else if (!permissionsDenied.contains(finalPermissionString)){
                            ToastUtils.showShort( "用户拒绝了该权限");
                        }else {
                            ToastUtils.showShort( "用户授予了该权限");
                        }
                    }
                })
                .request();

    }
}
