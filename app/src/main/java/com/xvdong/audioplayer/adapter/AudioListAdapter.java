package com.xvdong.audioplayer.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.ui.AudioDetailActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xvDong on 2023/9/12.
 */

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private ArrayList<AudioBean> mDataList;
    private Context mContext;
    private AudioDatabase mDatabase;
    private boolean mShowAction;

    public AudioListAdapter(Context context, ArrayList<AudioBean> dataList, AudioDatabase database, boolean showAction) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mDatabase = database;
        this.mShowAction = showAction;
    }


    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioBean data = mDataList.get(position);
        holder.textView.setText(data.getDisplayName());
        if (data.isCollect()) {
            holder.collect.setImageResource(R.mipmap.collected);
        } else {
            holder.collect.setImageResource(R.mipmap.collect);
        }
        holder.textView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AudioDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("bean", mDataList);
            bundle.putInt("position", position);
            intent.putExtra("bundle", bundle);
            mContext.startActivity(intent);
        });
        if (mShowAction) {
            holder.collect.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.collect.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityUtils.getTopActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);
            } else {
                //已经拥有权限
                new AlertDialog.Builder(mContext)
                        .setTitle("是否删除")
                        .setMessage("删除后,本地文件将会一同删除")
                        .setPositiveButton("确定", (dialog, which) -> {
                            //删除数据库
                            if (mDatabase != null) {
                                Observable.just(1)
                                        .map(integer -> {
                                            mDatabase.mAudioDao().delete(data);
                                            return true;
                                        }).subscribeOn(Schedulers.io())
                                        .subscribe();
                            }
                            //删除本地文件
                            boolean delete = FileUtils.delete(data.getPath());
                            //删除item
                            mDataList.remove(data);
                            //重新刷新数据
                            notifyItemRemoved(position);
                            int itemCount = mDataList.size() - position;
                            notifyItemRangeChanged(position, itemCount);
                        }).setNegativeButton("取消", (dialog, which) -> {

                        }).show();
            }

        });
        holder.collect.setOnClickListener(v -> {
            data.setCollect(!data.isCollect());
            if (data.isCollect()) {
                holder.collect.setImageResource(R.mipmap.collected);
            } else {
                holder.collect.setImageResource(R.mipmap.collect);
            }
            if (mDatabase != null) {
                mDatabase.mAudioDao()
                        .insertAudio(data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNewData(List<AudioBean> newData) {
        mDataList.clear();
        mDataList.addAll(newData);
        this.notifyDataSetChanged();
    }

    public void addData(AudioBean audioBean) {
        mDataList.add(audioBean);
        this.notifyItemChanged(mDataList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView collect;
        public ImageView delete;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
            delete = itemView.findViewById(R.id.delete);
            collect = itemView.findViewById(R.id.collect);
        }
    }

}
