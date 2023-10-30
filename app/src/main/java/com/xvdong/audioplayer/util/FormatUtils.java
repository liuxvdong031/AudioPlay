package com.xvdong.audioplayer.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xvDong on 2023/10/24.
 */

public class FormatUtils {
    public static String formatTime(int duration) {
        return new SimpleDateFormat("mm:ss").format(new Date(duration));
    }
}
