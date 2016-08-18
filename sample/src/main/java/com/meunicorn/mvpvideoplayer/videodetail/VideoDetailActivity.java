package com.meunicorn.mvpvideoplayer.videodetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.meunicorn.mvpvideoplayer.BaseActivity;
import com.meunicorn.mvpvideoplayer.R;
import com.meunicorn.videocontrolview.VideoControlView;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoDetailActivity extends BaseActivity implements SurfaceHolder.Callback, VideoControlView.VideoController {
    private VideoControlView vcvControl;
    private FrameLayout flVideo, flOtherView;
    private SurfaceView svVideo;
    private String videoUrl = "";
    private IjkMediaPlayer player;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        initView();
        try {
            player.setDataSource(videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setLogEnabled(false);
    }

    private void initView() {
        flOtherView = (FrameLayout) findViewById(R.id.fl_otherview);
        vcvControl = (VideoControlView) findViewById(R.id.vcv_control);
        initVideo();
    }

    private void initVideo() {
        player = new IjkMediaPlayer();
        svVideo = new SurfaceView(this);
        vcvControl.attachVideoView(svVideo, this);
        SurfaceHolder holder = svVideo.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        player.setDisplay(surfaceHolder);
        player.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void stop() {
        player.stop();
    }

    @Override
    public void seekTo(long progress) {
        player.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
    }

    @Override
    public long getCachePercentage() {
        return 0;
    }


}
