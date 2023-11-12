package com.xvdong.audioplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ItemAudioSelectBinding;
import com.xvdong.audioplayer.model.AudioBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xvDong on 2023/11/7.
 */

public class AudioSelectAdapter extends RecyclerView.Adapter<AudioSelectAdapter.ViewHolder> {
    private Context mContext;
    private List<AudioBean> mAudioBeans;
    private List<AudioBean> mSelectedAudios;

    public AudioSelectAdapter(Context context, List<AudioBean> list, List<AudioBean> selectedId) {
        mContext = context;
        mAudioBeans = list;
        mSelectedAudios = selectedId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAudioSelectBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_audio_select, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(mAudioBeans.get(position));
    }

    @Override
    public int getItemCount() {
        return mAudioBeans.size();
    }

    public List<AudioBean> getSelectedAudios() {
        if (mSelectedAudios == null) {
            return new ArrayList<>();
        }
        return mSelectedAudios;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAudioSelectBinding mBinding;
        private boolean selected = false;

        public ViewHolder(@NonNull ItemAudioSelectBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(AudioBean audioBean) {
            mBinding.setBean(audioBean);
            if (mSelectedAudios.contains(audioBean)) {
                selected = true;
                mBinding.ivSelectState.setImageResource(R.mipmap.select_selected);
            }
            mBinding.btnAudioName.setOnClickListener(v -> {
                if (selected) {
                    selected = false;
                    mBinding.ivSelectState.setImageResource(R.mipmap.select_normal);
                    mSelectedAudios.remove(audioBean.getId());
                } else {
                    selected = true;
                    mBinding.ivSelectState.setImageResource(R.mipmap.select_selected);
                    mSelectedAudios.add(audioBean);
                }
            });
        }
    }
}
