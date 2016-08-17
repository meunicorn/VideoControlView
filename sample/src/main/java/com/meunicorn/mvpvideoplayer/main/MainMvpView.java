package com.meunicorn.mvpvideoplayer.main;

import com.meunicorn.mvpvideoplayer.MvpView;
import com.meunicorn.mvpvideoplayer.main.bean.Video;

import java.util.List;

/**
 * Created by Fancy on 2016/8/17.
 */
public interface MainMvpView extends MvpView {
    void showVideoList(List<Video.DataEntity.ListEntity> videoList);

    void showGetError(String error);
}
