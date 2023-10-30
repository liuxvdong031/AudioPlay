package com.xvdong.audioplayer.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.xvdong.audioplayer.R;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;


public class LyricView extends androidx.appcompat.widget.AppCompatTextView {
    private float width;
    private float height;
    private float mLineOfLyricsHeight = 70;//一行歌词的高度
    private Paint mCurrentPaint;   //用来描绘当前正在播放的那句歌词
    private int mCurrentColor;      //当前正在播放的歌词的颜色
    private float mCurrentTextSize = 50;
    private Paint mOtherPaint;      //用来描绘非当前歌词
    private float mOtherTextSize = 40;
    private int mOtherColor;        //其他歌词的颜色
    private int index = 0;          //当前歌词的索引
    private List<LyricContent> myLyricList = null;        //每个LyricContent对应着一句话,这个List就是整个解析后的歌词文件


    public LyricView(Context context) {
        this(context,null);
    }

    public LyricView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet,0);
    }

    public LyricView(Context context, AttributeSet attributeSet, int defSytle) {
        super(context, attributeSet, defSytle);
        init();
    }

    private void init() {
        setFocusable(true);
        mCurrentColor = ContextCompat.getColor(this.getContext(), R.color.rainbow_red);
        mOtherColor = ContextCompat.getColor(this.getContext(), R.color.black);

        //初始化画笔
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentPaint.setColor(mCurrentColor);
        mCurrentPaint.setTextSize(mCurrentTextSize);
        mCurrentPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mOtherPaint = new Paint();
        mOtherPaint.setAntiAlias(true);
        mOtherPaint.setTextAlign(Paint.Align.CENTER);
        mOtherPaint.setColor(mOtherColor);
        mOtherPaint.setTextSize(mOtherTextSize);
        mOtherPaint.setTypeface(Typeface.DEFAULT);
    }

    /**
     * onDraw()就是画歌词的主要方法了
     * 在PlayFragment中会不停地调用
     * lyricView.invalidate();这个方法
     * 此方法写在了一个Runnable的run()函数中
     * 通过不断的给一个handler发送消息,不断的重新绘制歌词
     * 来达到歌词同步的效果
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        try {
            setText("");
            //画出之前的句子
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                tempY -= mLineOfLyricsHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(), width / 2, tempY, mOtherPaint);
            }
            //画出当前的句子
            canvas.drawText(myLyricList.get(index).getLyricString(), width / 2, height / 2, mCurrentPaint);
            //画出之后的句子
            tempY = height / 2;
            for (int i = index + 1; i < myLyricList.size(); i++) {
                tempY += mLineOfLyricsHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(), width / 2, tempY, mOtherPaint);
            }
        } catch (Exception e) {
            setText("一丁点儿歌词都没找到,下载后再来找我吧.......");
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        this.width = w;
        this.height = h;
    }

    public int getCurrentColor() {
        return mCurrentColor;
    }

    public void setCurrentColor(int currentColor) {
        mCurrentColor = currentColor;
    }

    public float getCurrentTextSize() {
        return mCurrentTextSize;
    }

    public void setCurrentTextSize(float currentTextSize) {
        mCurrentTextSize = currentTextSize;
    }

    public float getOtherTextSize() {
        return mOtherTextSize;
    }

    public void setOtherTextSize(float otherTextSize) {
        mOtherTextSize = otherTextSize;
    }

    public int getOtherColor() {
        return mOtherColor;
    }

    public void setOtherColor(int otherColor) {
        mOtherColor = otherColor;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<LyricContent> getMyLyricList() {
        if (myLyricList == null) {
            return new ArrayList<>();
        }
        return myLyricList;
    }

    public void setMyLyricList(List<LyricContent> myLyricList) {
        this.myLyricList = myLyricList;
    }

    public float getLineOfLyricsHeight() {
        return mLineOfLyricsHeight;
    }

    public void setLineOfLyricsHeight(float lineOfLyricsHeight) {
        mLineOfLyricsHeight = lineOfLyricsHeight;
    }
}