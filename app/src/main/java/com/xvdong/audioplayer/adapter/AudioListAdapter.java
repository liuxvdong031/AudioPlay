package com.xvdong.audioplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.ui.AudioDetailActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xvDong on 2023/9/12.
 */

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private ArrayList<AudioBean> mDataList;
    private Context mContext;

    public AudioListAdapter(Context context, ArrayList<AudioBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioBean data = mDataList.get(position);
        holder.textView.setText(data.getDisplayName());
        holder.textView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AudioDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("bean", mDataList);
            bundle.putInt("position",position);
            intent.putExtra("bundle", bundle);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
       public TextView textView;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
        }
    }
}
