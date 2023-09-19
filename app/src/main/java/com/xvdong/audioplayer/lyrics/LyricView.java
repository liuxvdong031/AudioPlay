package com.xvdong.audioplayer.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.xvdong.audioplayer.R;

import java.util.List;


public class LyricView extends androidx.appcompat.widget.AppCompatTextView {
    private float width;
    private float height;
    private Paint currentPaint;             //用来描绘当前正在播放的那句歌词
    private Paint notCurrentPaint;          //用来描绘非当前歌词
    private float textHeight = 50;
    private float textSize = 35;
    private int index = 0;                  //当前歌词的索引


    /**
     *     观察歌词文件发现,每句话都对应着一个时间
     *     所以专门写一个类LyricContent.java
     *     后面马上介绍到,来存放时间和该时间对应的歌词
     *     然后再用一个List将很多这个类的实例包裹起来
     *     这样就能很好的将每句歌词和他们的时间对应起来
     */
    private List<LyricContent> myLyricList = null;        //每个LyricCOntent对应着一句话,这个List就是整个解析后的歌词文件


    public void setIndex(int index) {
        this.index = index;
    }


    public void setMyLyricList(List<LyricContent> lyricList) {
        this.myLyricList = lyricList;
    }


    public List<LyricContent> getMyLyricList() {
        return this.myLyricList;
    }


    public LyricView(Context context) {
        super(context);
        init();
    }


    public LyricView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }


    public LyricView(Context context, AttributeSet attributeSet, int defSytle) {
        super(context, attributeSet, defSytle);
        init();
    }


    private void init() {
        setFocusable(true);

        //初始化画笔
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);


        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);


    }

    /**
     *     onDraw()就是画歌词的主要方法了
     *     在PlayFragment中会不停地调用
     *     lyricView.invalidate();这个方法
     *     此方法写在了一个Runnable的run()函数中
     *     通过不断的给一个handler发送消息,不断的重新绘制歌词
     *     来达到歌词同步的效果
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        currentPaint.setColor(getResources().getColor(R.color.rainbow_red));
        notCurrentPaint.setColor(getResources().getColor(R.color.black));

        currentPaint.setTextSize(40);
        currentPaint.setTypeface(Typeface.DEFAULT_BOLD);

        notCurrentPaint.setTextSize(textSize);
        notCurrentPaint.setTypeface(Typeface.DEFAULT);
        try {
            setText("");
            //画出之前的句子
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                tempY -= textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(), width / 2, tempY, notCurrentPaint);
            }
            //画出当前的句子
            canvas.drawText(myLyricList.get(index).getLyricString(), width / 2, height / 2, currentPaint);
            //画出之后的句子
            tempY = height / 2;
            for (int i = index + 1; i < myLyricList.size(); i++) {
                tempY += textHeight;
                canvas.drawText(myLyricList.get(i).getLyricString(), width / 2, tempY, notCurrentPaint);
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
}