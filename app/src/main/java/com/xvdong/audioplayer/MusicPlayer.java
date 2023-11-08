package com.xvdong.audioplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.xvdong.audioplayer.db.AudioDatabase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by xvDong on 2023/9/13.
 */

public class MusicPlayer {
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private ArrayList<AudioBean> mAudioList;
    private int mCurrentPosition;
    private int mCurrentModel = 2;

    public static final int MODEL_SINGLE = 1;
    public static final int MODEL_LOOP = 2;
    public static final int MODEL_RANDOM = 3;
    public boolean mPlayException = false;
    private AudioBean mAudioBean;

    public MusicPlayer(Context context, ArrayList<AudioBean> audioList, int currentPosition) {
        if (audioList == null) throw new RuntimeException("音乐数据集不可未空!");
        this.mContext = context;
        this.mAudioList = audioList;
        this.mCurrentPosition = currentPosition;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(mp -> {
            if (!mPlayException) {
                autoPlayNext();
            }
        });
    }

    /**
     * 设置播放模式
     *
     * @param model 三种
     */
    public void setPlayMode(int model) {
        mCurrentModel = model;
    }

    public int getCurrentModel() {
        return mCurrentModel;
    }

    /**
     * 自动播放下一首
     */
    private void autoPlayNext() {
        switch (mCurrentModel) {
            case MODEL_SINGLE:
                playOther(mAudioList.get(mCurrentPosition));
                break;
            case MODEL_LOOP:
                loopModelNext();
                break;
            case MODEL_RANDOM:
                randomModel();
                break;
            default:
                break;
        }
    }


    /**
     * 手动点击播放上一首
     */
    public void pre() {
        switch (mCurrentModel) {
            case MODEL_SINGLE:
            case MODEL_LOOP:
                loopModelPre();
                break;
            case MODEL_RANDOM:
                randomModel();
                break;
            default:
                break;
        }
    }

    /**
     * 列表循环的播放上一首
     */
    private void loopModelPre() {
        if (mCurrentPosition == 0) {
            mCurrentPosition = mAudioList.size() - 1;
        } else {
            mCurrentPosition--;
        }
        playOther(mAudioList.get(mCurrentPosition));
    }

    /**
     * 随机播放
     */
    private void randomModel() {
        mCurrentPosition = new Random().nextInt(mAudioList.size() - 1);
        playOther(mAudioList.get(mCurrentPosition));
    }

    /**
     * 手动点击下一曲
     */
    public void next() {
        switch (mCurrentModel) {
            case MODEL_SINGLE:
            case MODEL_LOOP:
                loopModelNext();
                break;
            case MODEL_RANDOM:
                randomModel();
                break;
            default:
                break;
        }
    }

    private void loopModelNext() {
        if (mCurrentPosition == mAudioList.size() - 1) {
            mCurrentPosition = 0;
        } else {
            mCurrentPosition++;
        }
        playOther(mAudioList.get(mCurrentPosition));
    }

    private void playOther(AudioBean audioBean) {
        mMediaPlayer.reset();
        play(audioBean);
    }

    public void play(AudioBean audioBean) {
        mAudioBean = audioBean;
        String dataSource = "";
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }
            if (lyricsListener != null) {
                lyricsListener.onNewMusicPlay(audioBean);
            }
            if (TextUtils.isEmpty(audioBean.getPath())) {
                dataSource = "https://music.163.com/song/media/outer/url?id=" + audioBean.getId() + ".mp3";
                audioBean.setPath(dataSource);
                mMediaPlayer.setDataSource(dataSource);
            } else {
                mMediaPlayer.setDataSource(mContext, Uri.parse(audioBean.getPath()));
            }
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            if (mOnPlayListener != null) {
                mOnPlayListener.onPlay(mAudioBean);
            }
        } catch (Exception e) {
            new AlertDialog.Builder(mContext)
                    .setMessage("抱歉,该音乐没有找到,换一首吧!")
                    .setNegativeButton("下一曲", (dialogInterface, i) -> {
                        mPlayException = false;
                        autoPlayNext();
                    })
                    .setPositiveButton("退出", (dialogInterface, i) -> {
                        if (mOnPlayListener != null) {
                            mOnPlayListener.onPlayException();
                        }
                    })
                    .create()
                    .show();
            Set<String> exceptionList = SPUtils.getInstance().getStringSet(Constants.EXCEPTION_LIST, new HashSet<>());
            exceptionList.add(String.valueOf(audioBean.getId()));
            SPUtils.getInstance().put(Constants.EXCEPTION_LIST, exceptionList);
            mPlayException = true;
            e.printStackTrace();
            LogUtils.d(dataSource);
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    //回收MediaPlayer
    public void releaseMediaPlayer() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public String getCurrentSongName() {
        return mAudioList.get(mCurrentPosition).getMusicName();
    }

    public void seekTo(int newPosition) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(newPosition);
        }
    }

    private LyricsListener lyricsListener;

    public void setOnLyricsListener(LyricsListener lyricsListener) {
        this.lyricsListener = lyricsListener;
    }

    public void collectCurrentAudio(AudioDatabase database) {
        if (mAudioBean != null && database != null) {
            DbUtils.insertAudio(database,mAudioBean);
        }
    }

    public AudioBean getAudioBean() {
        return mAudioBean;
    }

    public void setAudioBean(AudioBean audioBean) {
        mAudioBean = audioBean;
    }

    public interface LyricsListener {
        void onNewMusicPlay(AudioBean audioBean);
    }

    private setOnPlayListener mOnPlayListener;

    public interface setOnPlayListener {
        void onPlay(AudioBean audioBean);
        void onPlayException();
    }

    public void setOnPlayListener(setOnPlayListener listener) {
        this.mOnPlayListener = listener;
    }

}