package com.xvdong.audioplayer.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ItemSingerBinding;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.db.DbUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xvDong on 2023/11/5.
 */

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {

    private List<String> mSingers;
    private AudioDatabase mDatabase;
    private  onItemClickListener mItemClickListener;

    public SingerAdapter(AudioDatabase database, List<String> singers ,onItemClickListener listener) {
        mDatabase = database;
        mSingers = singers;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingerBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_singer, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.btnSingerName.setOnClickListener(v -> {
            mItemClickListener.onItemClick(mSingers.get(position));
        });
        holder.bind(mDatabase, mSingers.get(position));
    }

    @Override
    public int getItemCount() {
        return mSingers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSingerBinding mBinding;

        public ViewHolder(@NonNull ItemSingerBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @SuppressLint("CheckResult")
        public void bind(AudioDatabase database, String singer) {
            mBinding.btnSingerName.setText(singer);
            DbUtils.getAudiosByArtist(database,singer,data -> {
                mBinding.tvMusicCount.setText(data.size() + "首歌曲");
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(String singer);
    }
}
