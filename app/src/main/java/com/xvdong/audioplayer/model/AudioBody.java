package com.xvdong.audioplayer.model;

/**
 * Created by xvDong on 2023/9/13.
 */
//s：歌曲名
//offset：偏移量
//limit：获取歌曲数
//type：类型(歌曲：1、专辑：10、歌手：100、歌单：1000、用户：1002、mv：1004)
public class AudioBody {
        private String s;
        private int offset;
        private int limit;
        private int type;

    public String getS() {
        return s == null ? "" : s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
