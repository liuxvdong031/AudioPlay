package com.xvdong.audioplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ItemSingListBinding;
import com.xvdong.audioplayer.model.SingListBean;
import com.xvdong.audioplayer.ui.PlaylistSongsActivity;
import com.xvdong.audioplayer.util.Constants;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xvDong on 2023/11/7.
 * 歌单列表的适配器
 */

public class SingListAdapter extends RecyclerView.Adapter<SingListAdapter.ViewHolder> {

    private List<SingListBean> mSingList;
    private Context mContext;

    public SingListAdapter(@NonNull Context context, List<SingListBean> singListBeans) {
        mSingList = singListBeans;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSingListBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_sing_list, parent, false);
        return new SingListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SingListBean singListBean = mSingList.get(position);
        holder.bind(singListBean);
        holder.mBinding.llRoot.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PlaylistSongsActivity.class);
            intent.putExtra(Constants.BEAN, singListBean);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mSingList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNewData(List<SingListBean> singListBeans) {
        mSingList = singListBeans;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSingListBinding mBinding;

        public ViewHolder(ItemSingListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(SingListBean singListBean) {
            mBinding.setBean(singListBean);
        }
    }
}
