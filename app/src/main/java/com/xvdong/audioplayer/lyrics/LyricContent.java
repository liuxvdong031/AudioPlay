package com.xvdong.audioplayer.lyrics;

/**
 * Created by xvDong on 2023/9/13.
 */

public class LyricContent {
    private String lyricString;            //歌词的内容
    private int lyricTime;                 //歌词当前的时间

    public LyricContent(String lyricString, int lyricTime) {
        this.lyricString = lyricString;
        this.lyricTime = lyricTime;
    }

    public String getLyricString() {
        return this.lyricString;
    }

    public void setLyricString(String str) {
        this.lyricString = str;
    }

    public int getLyricTime() {
        return this.lyricTime;
    }

    public void setLyricTime(int time) {
        this.lyricTime = time;
    }
}