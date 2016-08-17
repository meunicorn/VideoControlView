package com.meunicorn.mvpvideoplayer.videodetail;

import com.meunicorn.mvpvideoplayer.MvpView;

/**
 * Created by Fancy on 2016/8/17.
 */
public interface VideoMvpView extends MvpView{
    void start();
    void pause();
    void stop();
    void showProgress();
}
