package com.xvdong.audioplayer.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.MusicPlayer;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ActivityAudioDetailBinding;
import com.xvdong.audioplayer.db.AppDataBase;
import com.xvdong.audioplayer.db.DbUtils;
import com.xvdong.audioplayer.http.ApiService;
import com.xvdong.audioplayer.http.RetrofitClient;
import com.xvdong.audioplayer.impl.DefaultOnSeekBarChangeListener;
import com.xvdong.audioplayer.lyrics.LyricContent;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.LyricsBean;
import com.xvdong.audioplayer.model.WYAudio;
import com.xvdong.audioplayer.service.ForegroundService;
import com.xvdong.audioplayer.util.Constants;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.reactivex.Observable;
import io.reactivex.internal.observers.BlockingBaseObserver;

/**
 * 音乐播放界面
 */
public class AudioDetailActivity extends AppCompatActivity {

    private ActivityAudioDetailBinding mBinding;
    private MusicPlayer mMusicPlayer;
    private ArrayList<LyricContent> lyricContents;
    private AppDataBase mDatabase;
    private Intent mServiceIntent;
    private int currentTime;
    private int duration;
    private int lyricIndex;
    private Menu mMenu;
    private final int WHAT_UPDATE_SEEK_BAR = 1;//handler 更新进度条
    private final int WHAT_UPDATE_LYRIC = 2;//handler 更新歌词
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_UPDATE_SEEK_BAR:
                    updateSeekBar();
                    handler.sendEmptyMessageDelayed(WHAT_UPDATE_SEEK_BAR, 200);
                    break;
                case WHAT_UPDATE_LYRIC:
                    mBinding.lrcShowView.setIndex(lyricIndex());
                    mBinding.lrcShowView.invalidate();
                    handler.sendEmptyMessageDelayed(WHAT_UPDATE_LYRIC, 300);
                    break;
            }
        }
    };


    //获取歌词当前播放的行数
    public int lyricIndex() {
        int size = lyricContents.size();
        if (mMusicPlayer.isPlaying()) {
            currentTime = mMusicPlayer.getCurrentPosition();
            duration = mMusicPlayer.getDuration();
        }
        if (currentTime < duration) {
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    if (currentTime < lyricContents.get(i).getLyricTime() && i == 0) {
                        lyricIndex = i;
                        break;
                    }
                    if (currentTime > lyricContents.get(i).getLyricTime()
                            && currentTime < lyricContents.get(i + 1).getLyricTime()) {
                        lyricIndex = i;
                        break;
                    }
                }
                if (i == size - 1
                        && currentTime > lyricContents.get(i).getLyricTime()) {
                    lyricIndex = i;
                    break;
                }
            }
        }
        return lyricIndex;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_play_mode, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理菜单项点击事件
        int id = item.getItemId();
        if (id == R.id.action_single_cycle) {//单曲循环
            ToastUtils.showShort("单曲循环");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_SINGLE);
            mBinding.playMode.setImageResource(R.mipmap.single_cycle);
            return true;
        } else if (id == R.id.action_list_loop) {//列表循环
            ToastUtils.showShort("列表循环");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_LOOP);
            mBinding.playMode.setImageResource(R.mipmap.list_loop);
            return true;
        } else if (id == R.id.action_shuffle_playback) {//随机播放
            ToastUtils.showShort("随机播放");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_RANDOM);
            mBinding.playMode.setImageResource(R.mipmap.shuffle_playback);
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        } else if (id == R.id.action_collect) {
            AudioBean audioBean = mMusicPlayer.getAudioBean();
            audioBean.setCollect(!audioBean.isCollect());
            setCollectState(audioBean);
            mMusicPlayer.collectCurrentAudio(mDatabase);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_detail);
        initDatabase();
        initForegroundService();
        initParams();
        initView();
        initReceiver();
    }

    private void initDatabase() {
        DbUtils.getAppDataBase(this, database -> mDatabase = database);
    }

    private void initForegroundService() {
        mServiceIntent = new Intent(this, ForegroundService.class);
        mServiceIntent.putExtra(Constants.MUSIC_NAME, "音乐播放器");
        mServiceIntent.putExtra(Constants.MUSIC_ARTIST, "前台服务");
        startForegroundService(mServiceIntent);
    }


    private void initReceiver() {
        //耳机插拔的监听
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        //电话的监听
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mHeadsetReceiver, filter);
        //注册媒体的监听
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(audioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }


    //监听耳机插拔的广播
    private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                if (state == 0) {// 耳机已拨出
                    if (mMusicPlayer.isPlaying()) {
                        mMusicPlayer.pause();
                    }
                    mBinding.play.setImageResource(R.mipmap.play);
                } else if (state == 1) {// 耳机已插入
                    mMusicPlayer.resume();
                    mBinding.play.setImageResource(R.mipmap.pause);
                }
            }

            if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                // 电话响铃，暂停音乐播放操作
                if (phoneState != null && phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (mMusicPlayer.isPlaying()) {
                        mMusicPlayer.pause();
                    }
                    mBinding.play.setImageResource(R.mipmap.play);
                    // 电话挂断，恢复音乐播放操作
                } else if (phoneState != null && phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    mMusicPlayer.resume();
                    mBinding.play.setImageResource(R.mipmap.pause);
                }
            }
        }
    };

    private final AudioManager.OnAudioFocusChangeListener audioListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                // 获得音频焦点，可以恢复音乐播放
                case AudioManager.AUDIOFOCUS_GAIN:
                    mMusicPlayer.resume();
                    mBinding.play.setImageResource(R.mipmap.pause);
                    break;
                // 失去音频焦点，暂停音乐播放
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mMusicPlayer.isPlaying()) {
                        mMusicPlayer.pause();
                    }
                    mBinding.play.setImageResource(R.mipmap.play);
                    break;
                // 暂时失去音频焦点（短暂的），暂停音乐播放
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mMusicPlayer.isPlaying()) {
                        mMusicPlayer.pause();
                    }
                    mBinding.play.setImageResource(R.mipmap.play);
                    break;
                // 暂时失去音频焦点，但可以继续播放，降低音量即可
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };


    private void initParams() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra(Constants.BUNDLE);
            if (bundle == null) return;
            ArrayList<AudioBean> audioList = bundle.getParcelableArrayList(Constants.BEAN);
            int position = bundle.getInt(Constants.POSITION);
            mMusicPlayer = new MusicPlayer(this, audioList, position);
            mMusicPlayer.setOnLyricsListener(this::displayLyricsSimultaneously);
            mMusicPlayer.setOnPlayListener(new MusicPlayer.setOnPlayListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onPlay(AudioBean audioBean) {
                    setCollectState(audioBean);
                }

                @Override
                public void onPlayException() {
                    AudioDetailActivity.this.finish();
                }
            });
            mMusicPlayer.play(audioList.get(position));
            handler.sendEmptyMessage(WHAT_UPDATE_SEEK_BAR);
        }
    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.pre.setOnClickListener(view -> {
            mMusicPlayer.previous();
        });

        mBinding.play.setOnClickListener(view -> {
            if (mMusicPlayer.isPlaying()) {
                mMusicPlayer.pause();
                mBinding.play.setImageResource(R.mipmap.play);
            } else {
                mMusicPlayer.resume();
                mBinding.play.setImageResource(R.mipmap.pause);
            }
        });

        mBinding.next.setOnClickListener(view -> {
            mMusicPlayer.next();
        });

        mBinding.seekBar.setOnSeekBarChangeListener(new DefaultOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // 当用户拖动 SeekBar 时
                    int newPosition = (mMusicPlayer.getDuration() * progress) / 100;
                    mMusicPlayer.seekTo(newPosition);
                }
            }
        });

        mBinding.playMode.setOnClickListener(v -> {
            if (mMusicPlayer != null) {
                switch (mMusicPlayer.getCurrentModel()) {
                    case MusicPlayer.MODEL_SINGLE:
                        mMusicPlayer.setPlayMode(MusicPlayer.MODEL_LOOP);
                        mBinding.playMode.setImageResource(R.mipmap.list_loop);
                        break;
                    case MusicPlayer.MODEL_LOOP:
                        mMusicPlayer.setPlayMode(MusicPlayer.MODEL_RANDOM);
                        mBinding.playMode.setImageResource(R.mipmap.shuffle_playback);
                        break;
                    case MusicPlayer.MODEL_RANDOM:
                        mMusicPlayer.setPlayMode(MusicPlayer.MODEL_SINGLE);
                        mBinding.playMode.setImageResource(R.mipmap.single_cycle);
                        break;
                }
            }
        });

    }


    @NonNull
    private void setCollectState(AudioBean audioBean) {
        if (mMenu != null) {
            MenuItem item = mMenu.getItem(0);
            if (audioBean.isCollect()) {
                item.setIcon(R.mipmap.collected);
            } else {
                item.setIcon(R.mipmap.collect);
            }
        }
    }

    private void displayLyricsSimultaneously(AudioBean audioBean) {
        String lyric = audioBean.getLyric();
        if (TextUtils.isEmpty(lyric)) {
            if (audioBean.getWYCloudID() == 0L) {
                getWYCloudIDOnMusicName(audioBean);
            } else {
                getLyricsById(audioBean);
            }
        } else {
            parseLyrics(lyric);
        }
    }

    private void getWYCloudIDOnMusicName(AudioBean audioBean) {
        Observable<WYAudio> musicId = RetrofitClient.getInstance()
                .create(ApiService.class)
                .getMusicId(audioBean.getMusicName());
        RetrofitClient.execute(musicId, new BlockingBaseObserver<WYAudio>() {
            @Override
            public void onNext(WYAudio result) {
                try {
                    //弹窗列表 展示所有的歌曲 让用户选择
                    int id = result.getResult().getSongs().get(0).getId();
                    audioBean.setWYCloudID(id);
                    getLyricsById(audioBean);
                } catch (Exception e) {
                    ToastUtils.showLong("result 解析异常: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(e.getMessage());
            }
        });
    }

    private void getLyricsById(AudioBean audioBean) {
        RetrofitClient.execute(RetrofitClient
                        .getInstance()
                        .create(ApiService.class)
                        .getLyricsById(audioBean.getWYCloudID()),
                new BlockingBaseObserver<LyricsBean>() {
                    @Override
                    public void onNext(LyricsBean lyricsBean) {
                        String lyric = lyricsBean.getLrc().getLyric();
                        audioBean.setLyric(lyric);
                        insertAudio(audioBean);
                        parseLyrics(lyricsBean.getLrc().getLyric());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.getMessage());
                    }
                });
    }

    private void parseLyrics(String lyric) {
        ArrayList<LyricContent> lyricsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+:\\d+\\.\\d+)\\](.*)"); // 匹配时间戳和歌词文本
        Matcher matcher = pattern.matcher(lyric);
        while (matcher.find()) {
            String timestampStr = matcher.group(1); // 提取时间戳
            String text = matcher.group(2).trim(); // 提取歌词文本，并去除首尾空格
            long timestamp = parseTimestamp(timestampStr); // 将时间戳字符串转换为毫秒数
            LyricContent lyricContent = new LyricContent(text, (int) timestamp);
            lyricsList.add(lyricContent); // 将歌词对象加入列表
        }
        lyricContents = lyricsList;
        handler.sendEmptyMessageDelayed(WHAT_UPDATE_LYRIC, 300);
        mBinding.lrcShowView.setMyLyricList(lyricsList);
    }


    @SuppressLint("CheckResult")
    private void insertAudio(AudioBean audioBean) {
        if (mDatabase != null) {
            DbUtils.insertAudio(mDatabase, audioBean);
        }
    }

    // 辅助方法：将时间戳字符串转换为毫秒数
    private long parseTimestamp(String timestampStr) {
        String[] parts = timestampStr.split(":|\\.");
        long minute = Long.parseLong(parts[0]);
        long second = Long.parseLong(parts[1]);
        long millisecond = Long.parseLong(parts[2]);
        return (minute * 60 + second) * 1000 + millisecond;
    }

    private void updateSeekBar() {
        try {
            if (mMusicPlayer.mPlayException) return;
            int duration = mMusicPlayer.getDuration();
            int currentPosition = mMusicPlayer.getCurrentPosition();
            int progress = (currentPosition * 100) / duration;
            mBinding.seekBar.setProgress(progress);
            mBinding.toolbar.setTitle(mMusicPlayer.getCurrentSongName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止前台服务
        stopService(mServiceIntent);
        //解绑广播
        unregisterReceiver(mHeadsetReceiver);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mMusicPlayer != null) {
            mMusicPlayer.releaseMediaPlayer();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY://播放
                if (mMusicPlayer != null) {
                    mMusicPlayer.resume();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE://暂停
                if (mMusicPlayer != null) {
                    mMusicPlayer.pause();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS://上一曲
                if (mMusicPlayer != null) {
                    mMusicPlayer.next();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT://下一曲
                if (mMusicPlayer != null) {
                    mMusicPlayer.previous();
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}