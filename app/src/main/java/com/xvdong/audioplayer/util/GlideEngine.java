package com.xvdong.audioplayer.util;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xvdong.audioplayer.R;

/**
 * Glide4.x的加载图片引擎实现,单例模式
 * Glide4.x的缓存机制更加智能，已经达到无需配置的境界。如果使用Glide3.x，需要考虑缓存机制。
 * Created by huan on 2018/1/15.
 */

public class GlideEngine {
    //单例
    private static GlideEngine instance = null;

    private final int  defaultPlaceHolderRes = R.mipmap.ic_launcher;
    private final int defaultErrorRes = R.mipmap.ic_launcher;

    //单例模式，私有构造方法
    private GlideEngine() {
    }

    //获取单例
    public static GlideEngine getInstance() {
        if (null == instance) {
            synchronized (GlideEngine.class) {
                if (null == instance) {
                    instance = new GlideEngine();
                }
            }
        }
        return instance;
    }

    public void displayImage(Object url, ImageView imageView) {
        displayImage(url, defaultPlaceHolderRes, defaultErrorRes, imageView);
    }

    public void displayImage(Object url, int placeholderRes, int errorRes, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(placeholderRes == 0 ? defaultPlaceHolderRes : placeholderRes)
                .error(errorRes == 0 ? defaultErrorRes : errorRes)
                .override(imageView.getMeasuredWidth(),imageView.getMeasuredHeight())
                .into(imageView);
    }

    /**
     * 显示圆形图片
     */
    public void displayCircleImage(Object url, ImageView imageView) {
        displayCircleImage(url,defaultPlaceHolderRes,defaultErrorRes,imageView);
    }

    /**
     * 显示圆形图片
     */
    public void displayCircleImage(Object url, int placeholderRes, int errorRes, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(url)
                .transform(new CircleCrop())
                .placeholder(placeholderRes == 0 ? defaultPlaceHolderRes : placeholderRes)
                .error(errorRes == 0 ? defaultErrorRes : errorRes)
                .override(imageView.getMeasuredWidth(),imageView.getMeasuredHeight())
                .into(imageView);
    }

    /**
     * 显示圆角图片
     */
    public void displayCornerImage(Object url, ImageView imageView, int imgCorner) {
        displayCornerImage(url,defaultPlaceHolderRes,defaultErrorRes,imageView,imgCorner);
    }

    /**
     * 显示圆角图片
     */
    public void displayCornerImage(Object url, int placeholderRes, int errorRes, ImageView imageView, int imgCorner) {
        if (imgCorner != 0) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .transform(new CenterCrop(), new RoundedCorners(dip2px(imageView.getContext(),imgCorner)))
                    .placeholder(placeholderRes == 0 ? defaultPlaceHolderRes : placeholderRes)
                    .error(errorRes == 0 ? defaultErrorRes : errorRes)
                    .override(imageView.getMeasuredWidth(),imageView.getMeasuredHeight())
                    .into(imageView);
        } else {
            displayImage(url, placeholderRes, errorRes, imageView);
        }

    }

    public int dip2px(Context context ,int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
