package com.xvdong.audioplayer.adapter;

import com.xvdong.audioplayer.model.AudioBean;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by xvDong on 2023/11/10.
 */

public class AudioDiffCallback extends DiffUtil.Callback{

    private List<AudioBean> oldList;
    private List<AudioBean> newList;

    public AudioDiffCallback(List<AudioBean> oldList, List<AudioBean> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        AudioBean oldMusic = oldList.get(oldItemPosition);
        AudioBean newMusic = newList.get(newItemPosition);
        return oldMusic.equals(newMusic);
    }
}
