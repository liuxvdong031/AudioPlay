package com.xvdong.audioplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xvdong.audioplayer.MusicPlayer;
import com.xvdong.audioplayer.R;
import com.xvdong.audioplayer.databinding.ActivityAudioDetailBinding;
import com.xvdong.audioplayer.http.ApiService;
import com.xvdong.audioplayer.http.RetrofitClient;
import com.xvdong.audioplayer.impl.DefaultOnSeekBarChangeListener;
import com.xvdong.audioplayer.lyrics.LyricContent;
import com.xvdong.audioplayer.model.AudioBean;
import com.xvdong.audioplayer.model.LyricsBean;
import com.xvdong.audioplayer.model.WYAudio;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.reactivex.Observable;
import io.reactivex.internal.observers.BlockingBaseObserver;

public class AudioDetailActivity extends AppCompatActivity {

    private ActivityAudioDetailBinding mBinding;
    private MusicPlayer mMusicPlayer;
    private SeekBar seekBar;
    private Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            handler.postDelayed(this, 200);
        }
    };

    private final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            mBinding.lrcShowView.setIndex(lyricIndex());
            mBinding.lrcShowView.invalidate();                                    //调用后自定义View会自动调用onDraw()方法来重新绘制歌词
            handler.postDelayed(myRunnable, 300);
        }
    };
    private ArrayList<LyricContent> lyricContents;
    private int currentTime;
    private int duration;
    private int index;


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
                        index = i;
                        break;
                    }
                    if (currentTime > lyricContents.get(i).getLyricTime()
                            && currentTime < lyricContents.get(i + 1).getLyricTime()) {
                        index = i;
                        break;
                    }
                }
                if (i == size - 1
                        && currentTime > lyricContents.get(i).getLyricTime()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_detail);
        seekBar = mBinding.seekBar;
        handler = new Handler();
        initParams();
        initView();
    }


    private void initParams() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("bundle");
            ArrayList<AudioBean> audioList = bundle.getParcelableArrayList("bean");
            int position = bundle.getInt("position");
            mMusicPlayer = new MusicPlayer(this, audioList, position);
            mMusicPlayer.setOnLyricsListener(name -> displayLyricsSimultaneously());
            mMusicPlayer.setOnPlayExceptionListener(() -> AudioDetailActivity.this.finish());
            mMusicPlayer.play(audioList.get(position));
            handler.post(runnable);
        }
    }

    private void initView() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.pre.setOnClickListener(view -> {
            mMusicPlayer.pre();
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
    }


    private void displayLyricsSimultaneously() {
        String currentSongName = mMusicPlayer.getCurrentSongName();

        String json = SPUtils.getInstance().getString(currentSongName);
        if (TextUtils.isEmpty(json)) {
            getMusicId(currentSongName);
        } else {
            ArrayList<LyricContent> lyricsList = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\[(\\d+:\\d+\\.\\d+)\\](.*)"); // 匹配时间戳和歌词文本
            Matcher matcher = pattern.matcher(json);
            while (matcher.find()) {
                String timestampStr = matcher.group(1); // 提取时间戳
                String text = matcher.group(2).trim(); // 提取歌词文本，并去除首尾空格
                long timestamp = parseTimestamp(timestampStr); // 将时间戳字符串转换为毫秒数
                LyricContent lyrics = new LyricContent(text, (int) timestamp);
                lyricsList.add(lyrics); // 将歌词对象加入列表
            }
            lyricContents = lyricsList;
            handler.post(myRunnable);
            mBinding.lrcShowView.setMyLyricList(lyricsList);
        }



//        日不落  209643
//        等你爱我  64312
//        等爱的玫瑰  1876116226
//        凤凰展翅  1977187214
//        天籁传奇  5234349
    }

    private void getMusicId(String name){
        Observable<WYAudio> musicId = RetrofitClient.getInstance()
                .create(ApiService.class)
                .getMusicId(name);
        RetrofitClient.execute(musicId, new BlockingBaseObserver<WYAudio>() {
            @Override
            public void onNext(WYAudio result) {
                try {
                    int id = result.getResult().getSongs().get(0).getId();
                    getLyricsById(String.valueOf(id),name);
                } catch (Exception e) {
                    ToastUtils.showLong("result 解析异常: "+ e.getMessage() );
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(e.getMessage());
            }
        });
    }

    private void getLyricsById(String id, String currentSongName) {
        Observable<LyricsBean> lyricsById = RetrofitClient
                .getInstance()
                .create(ApiService.class)
                .getLyricsById(id);
        RetrofitClient.execute(lyricsById, new BlockingBaseObserver<LyricsBean>() {
            @Override
            public void onNext(LyricsBean lyricsBean) {
                String lyric = lyricsBean.getLrc().getLyric();
                SPUtils.getInstance().put(currentSongName, lyric);
                ArrayList<LyricContent> lyricsList = new ArrayList<>();
                Pattern pattern = Pattern.compile("\\[(\\d+:\\d+\\.\\d+)\\](.*)"); // 匹配时间戳和歌词文本
                Matcher matcher = pattern.matcher(lyricsBean.getLrc().getLyric());
                while (matcher.find()) {
                    String timestampStr = matcher.group(1); // 提取时间戳
                    String text = matcher.group(2).trim(); // 提取歌词文本，并去除首尾空格
                    long timestamp = parseTimestamp(timestampStr); // 将时间戳字符串转换为毫秒数
                    LyricContent lyrics = new LyricContent(text, (int) timestamp);
                    lyricsList.add(lyrics); // 将歌词对象加入列表
                }
                lyricContents = lyricsList;
                handler.post(myRunnable);
                mBinding.lrcShowView.setMyLyricList(lyricsList);
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(e.getMessage());
            }
        });
    }

    // 辅助方法：将时间戳字符串转换为毫秒数
    private long parseTimestamp(String timestampStr) {
        String[] parts = timestampStr.split(":|\\.");
        long minute = Long.parseLong(parts[0]);
        long second = Long.parseLong(parts[1]);
        long millisecond = Long.parseLong(parts[2]);
        return (minute * 60 + second) * 1000 + millisecond;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单项
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理菜单项点击事件
        int id = item.getItemId();
        if (id == R.id.action_single_cycle) {//单曲循环
            ToastUtils.showShort("单曲循环");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_SINGLE);
            return true;
        } else if (id == R.id.action_list_loop) {//列表循环
            ToastUtils.showShort("列表循环");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_LOOP);
            return true;
        } else if (id == R.id.action_shuffle_playback) {//随机播放
            ToastUtils.showShort("随机播放");
            mMusicPlayer.setPlayMode(MusicPlayer.MODEL_RANDOM);
            return true;
        }else if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSeekBar() {
        try {
            if (mMusicPlayer.mPlayException)return;
            int duration = mMusicPlayer.getDuration();
            int currentPosition = mMusicPlayer.getCurrentPosition();
            int progress = (currentPosition * 100) / duration;
            seekBar.setProgress(progress);
            mBinding.toolbar.setTitle(mMusicPlayer.getCurrentSongName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
        }
    }

}