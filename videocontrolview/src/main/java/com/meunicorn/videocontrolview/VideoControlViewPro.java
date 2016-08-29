package com.meunicorn.videocontrolview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Fancy on 2016/8/17
 * 如果要设置横屏状态下播放结束时显示分享界面，请在Activity/Fragment中调用此类中的方法 setVideoCompleted()
 */
public class VideoControlViewPro extends FrameLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, View.OnSystemUiVisibilityChangeListener,
        View.OnTouchListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener {
    private String TAG = getClass().getSimpleName();
    private View controlView;
    private View videoView;
    private VideoController controller;
    private IjkMediaPlayer player;
    private SeekBar sbProgress;
    private ImageView ivPlay;
    private ImageView ivBack;
    private ImageView ivShare;
    private ImageView ivFullscreen;
    private TextView tvCurrentTime;
    private TextView tvEndTime;
    private RelativeLayout rlshare;
    private RelativeLayout rlController;
    private LinearLayout llProgressbar;
    private View vDim;
    private MessageHandler messageHandler = new MessageHandler();
    private boolean isShowing = false;
    private boolean isDragging = false;
    private boolean isCompleted = false;
    private int videoViewHeight = 0;
    private int defaultSystemUiVisiblity = 0;
    private long defaultTime = 3000;


    /**
     * Instantiates a new Video control view.
     *
     * @param context the context
     */
    public VideoControlViewPro(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Video control view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public VideoControlViewPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        controlView = LayoutInflater.from(context).inflate(R.layout.layout_video_control, null);
        addView(controlView);
        initControlUI();
    }

    private void initControlUI() {
        ivPlay = (ImageView) controlView.findViewById(R.id.iv_play);
        ivBack = (ImageView) controlView.findViewById(R.id.iv_back);
        ivShare = (ImageView) controlView.findViewById(R.id.iv_share);
        ivFullscreen = (ImageView) controlView.findViewById(R.id.iv_fullscreen);
        tvCurrentTime = (TextView) controlView.findViewById(R.id.tv_current_time);
        tvEndTime = (TextView) controlView.findViewById(R.id.tv_end_time);
        sbProgress = (SeekBar) controlView.findViewById(R.id.sb_progress);
        rlshare = (RelativeLayout) controlView.findViewById(R.id.rl_share);
        rlController = (RelativeLayout) controlView.findViewById(R.id.rl_controller);
        llProgressbar = (LinearLayout) controlView.findViewById(R.id.ll_progressbar);

        vDim = controlView.findViewById(R.id.v_dim);
        sbProgress.setMax(1000);
        sbProgress.setOnSeekBarChangeListener(this);
        ivPlay.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivFullscreen.setOnClickListener(this);
        setOnSystemUiVisibilityChangeListener(this);
    }

    /**
     * Attach video view.
     *
     * @param videoView  the view
     * @param player     the player
     * @param controller the controller
     */
    public void attachVideoView(View videoView, IjkMediaPlayer player, VideoController controller) {
        addView(videoView, 0);
        this.videoView = videoView;
        this.player = player;
        this.controller = controller;
        startListener();
        videoViewHeight = getLayoutParams().height;
        defaultSystemUiVisiblity = getSystemUiVisibility();
        show();
    }

    private void startListener() {
        messageHandler.sendEmptyMessage(Constant.MESSAGE_SHOW_PROGRESS);
        messageHandler.sendEmptyMessage(Constant.MESSAGE_IS_PLAYING);
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);
        setOnTouchListener(this);
    }

    /**
     * Show.
     */
    public void show() {
        show(defaultTime);
    }

    /**
     * Show.
     *
     * @param timeOut the time out
     */
    public void show(long timeOut) {
        if (isShowing) {
            hideUnsupportedUI();
            return;
        }
        if (!isCompleted) {
            //未播放完时的show()
            setVisible(ivBack, ivShare, ivPlay, llProgressbar, rlController, vDim);
        }
        if (timeOut > 0) {
            if (!isCompleted) {
                messageHandler.removeMessages(Constant.MESSAGE_FADE_OUT);
                messageHandler.sendEmptyMessageDelayed(Constant.MESSAGE_FADE_OUT, timeOut);
            }
        }
        isShowing = true;
    }

    // TODO: 2016/8/22 处理播放完后，点击播放后不隐藏的bug
    private void hideUnsupportedUI() {
        if (isLandScape()) {
            setInvisible(ivBack, ivShare, ivPlay, llProgressbar, vDim);
        }
        if (!isLandScape()) {
            setInvisible(ivPlay, llProgressbar, vDim);
        }
        isShowing = false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play) {
            doPauseResume();
        } else if (view.getId() == R.id.iv_back) {
            if (isLandScape()) {
                doToggleFullscreen();
            } else {
                controller.onBackClick();
            }
        } else if (view.getId() == R.id.iv_fullscreen) {
            doToggleFullscreen();
        } else if (view.getId() == R.id.iv_share) {
            controller.onShareClick();
        }
    }

    private void setInvisible(View... views) {
        for (View view : views) {
            view.setVisibility(GONE);
        }
    }

    private void setVisible(View... views) {
        for (View view : views) {
            view.setVisibility(VISIBLE);
        }
    }

    private void doToggleFullscreen() {
        if (!isLandScape()) {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            LinearLayout.LayoutParams videoFullscreenLayoutParam =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setLayoutParams(videoFullscreenLayoutParam);
            setInvisible(controller.getOtherView());
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            LinearLayout.LayoutParams original = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, videoViewHeight);
            this.setLayoutParams(original);
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setVisible(controller.getOtherView());
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            decorView.setSystemUiVisibility(defaultSystemUiVisiblity);
        }

    }

    /**
     * 毫秒转为播放的时间，格式为 mm:ss
     *
     * @param timeMs 毫秒
     * @return 时间 string
     */
    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        java.util.Formatter mFormatter = new java.util.Formatter();
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private long changeProgress() {
        if (player == null || isDragging) {
            return 0;
        }
        long position = player.getCurrentPosition();
        long duration = player.getDuration();
        if (sbProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                sbProgress.setProgress((int) pos);
            }
        }

        if (tvEndTime != null)
            tvEndTime.setText(stringForTime(duration));
        if (tvCurrentTime != null)
            tvCurrentTime.setText(stringForTime(position));
        return position;
    }

    /**
     * Is land scape boolean.
     *
     * @return the boolean
     */
    public boolean isLandScape() {
        // 横屏 true , 竖屏 false
        Configuration mConfiguration = this.getResources().getConfiguration(); // 获取设置的配置信息
        int ori = mConfiguration.orientation; // 获取屏幕方向

        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return false;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long duration = player.getDuration();
        long newPosition = (duration * progress) / 1000L;
        player.seekTo((int) newPosition);
        if (tvCurrentTime != null)
            tvCurrentTime.setText(stringForTime((int) newPosition));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        messageHandler.removeMessages(Constant.MESSAGE_SHOW_PROGRESS);
        messageHandler.removeMessages(Constant.MESSAGE_FADE_OUT);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDragging = false;
        messageHandler.sendEmptyMessage(Constant.MESSAGE_SHOW_PROGRESS);
        messageHandler.sendEmptyMessageDelayed(Constant.MESSAGE_FADE_OUT, defaultTime);
    }

    private void doPauseResume() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    private void updatePlayIcon() {
        if (player.isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_index_video_pause);
        } else {
            ivPlay.setImageResource(R.drawable.ic_index_video_play);
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int i) {
        if (isLandScape()) {
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            decorView.setSystemUiVisibility(defaultSystemUiVisiblity);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            show();
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        sbProgress.setSecondaryProgress(percent * 10);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        isCompleted = true;
    }

    /**
     * Release.
     */
    public void release() {
        messageHandler.removeMessages(Constant.MESSAGE_FADE_OUT);
        messageHandler.removeMessages(Constant.MESSAGE_SHOW_PROGRESS);
        messageHandler.removeMessages(Constant.MESSAGE_IS_PLAYING);

        if (player != null) {
            player.stop();
            player.release();
            player = null;
            AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    /**
     * The interface Video controller.
     */
    public interface VideoController {
        /**
         * 视频的duration
         *
         * @return the duration
         */
        long getDuration();

        /**
         * Gets other view.
         *
         * @return the other view
         */
        View getOtherView();

        /**
         * On back click.
         */
        void onBackClick();

        /**
         * On share click.
         */
        void onShareClick();


        /**
         * 这个接口用于显示当前测试的数据。
         *
         * @return the test text
         */
        TextView getTestText();
    }

    /**
     * The type Message handler.
     */
    class MessageHandler extends Handler {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_SHOW_PROGRESS:
                    msg = obtainMessage(Constant.MESSAGE_SHOW_PROGRESS);
                    long pos = changeProgress();
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
                case Constant.MESSAGE_IS_PLAYING:
                    updatePlayIcon();
                    if (isLandScape() && isCompleted) {
                        setVisible(rlshare, ivBack);
                        ivBack.setImageResource(R.drawable.ic_index_video_close);
                    } else {
                        setInvisible(rlshare);
                        ivBack.setImageResource(R.drawable.ic_index_video_back);
                    }
                    if (!isLandScape() && isCompleted) {
                        setVisible(ivBack, ivShare, ivPlay, llProgressbar);
                    }

                    if (player.isPlaying()) {
                        isCompleted = false;
                    }
                    controller.getTestText().setText(
                            "isPlaying : " + player.isPlaying() + "\n"
                                    + "isCompleted :" + isCompleted + "\n"
                                    + "Duration : " + player.getCurrentPosition() + " / " + player.getDuration()
                    );
                    sendEmptyMessage(Constant.MESSAGE_IS_PLAYING);
                    break;
                case Constant.MESSAGE_FADE_OUT:
                    hideUnsupportedUI();
                    break;
            }
        }
    }
}
