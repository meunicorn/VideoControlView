package com.meunicorn.mvpvideoplayer.common;

import com.meunicorn.mvpvideoplayer.main.bean.Video;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Fancy on 2016/8/17.
 */
public interface VideoService {
    @GET("api/article/video")
    Observable<Video> getVideoList(
            @Query("page") int page,
            @Query("type") String type,
            @Query("fkey") String fkey);
}
