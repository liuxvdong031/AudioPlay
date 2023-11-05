package com.xvdong.audioplayer.http;

import com.xvdong.audioplayer.model.AudioBody;
import com.xvdong.audioplayer.model.LyricsBean;
import com.xvdong.audioplayer.model.WYAudio;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xvDong on 2023/9/13.
 */

public interface ApiService {
    //获取歌曲ID
    @POST("/api/search/pc")
    Observable<WYAudio> getAudioList(@Body AudioBody body);

//    http://musicapi.leanapp.cn/search?keywords=
//    https://music.163.com/#/search/m/?s=%E7%AD%89%E4%BD%A0%E7%88%B1%E6%88%91&type=1
//    http://iwenwiki.com:3000/search?keywords=%E6%B5%B7%E9%98%94%E5%A4%A9%E7%A9%BA

    //根据ID获取歌词
    @GET("/lyric")
    Observable<LyricsBean> getLyricsById(@Query("id") Integer id);

    @GET("/search")
    Observable<WYAudio> getMusicId(@Query("keywords") String keywords);

}
