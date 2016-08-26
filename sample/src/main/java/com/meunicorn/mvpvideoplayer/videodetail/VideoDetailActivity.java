package com.meunicorn.mvpvideoplayer.videodetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meunicorn.mvpvideoplayer.BaseActivity;
import com.meunicorn.mvpvideoplayer.R;
import com.meunicorn.videocontrolview.VideoControlView;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoDetailActivity extends BaseActivity implements SurfaceHolder.Callback, VideoControlView.VideoController {
    private VideoControlView vcvControl;
    private FrameLayout flVideo, flOtherView;
    private SurfaceView svVideo;
    private String videoUrl = "";
    private IjkMediaPlayer player;
    private String title;
    private String imgUrl;
    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        imgUrl = intent.getStringExtra("imgUrl");
        getSupportActionBar().hide();
        initView();
        try {
            player.setDataSource(videoUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setLogEnabled(false);
    }

    private void initView() {
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.item_video,null);
        TextView tvBottom = (TextView) bottomSheetView.findViewById(R.id.tv_video_title);
        tvBottom.setText(title);
        SimpleDraweeView sdvBottom = (SimpleDraweeView) bottomSheetView.findViewById(R.id.sdv_video_cover);
        sdvBottom.setImageURI(imgUrl);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        flOtherView = (FrameLayout) findViewById(R.id.fl_otherview);
        flOtherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(VideoDetailActivity.this).setMessage("hello").setTitle("what").show();
            }
        });
        vcvControl = (VideoControlView) findViewById(R.id.vcv_control);
        initVideo();
    }

    private void initVideo() {
        player = new IjkMediaPlayer();
        player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                vcvControl.setVideoCompleted();
            }
        });
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

    @Override
    public View getOtherView() {
        return flOtherView;
    }

    @Override
    public void onBackClick() {
        onBackPressed();
    }

    @Override
    public void onShareClick() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
            bottomSheetDialog.cancel();
            bottomSheetDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        player.stop();
        player.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
