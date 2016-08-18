package com.meunicorn.videocontrolview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Fancy on 2016/8/17.
 */
public class VideoControlView extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG = getClass().getSimpleName();
    private View controlView;
    private View view;
    private VideoController controller;
    private SeekBar sbProgress;
    private ImageView ivPlay;
    private ImageView ivBack;
    private ImageView ivShare;
    private ImageView ivFullscreen;
    private TextView tvCurrentTime;
    private TextView tvEndTime;
    private MessageHandler messageHandler = new MessageHandler();
    private boolean isPlaying = false;

    private boolean isDragging = false;

    /**
     * Instantiates a new Video control view.
     *
     * @param context the context
     */
    public VideoControlView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Video control view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public VideoControlView(Context context, AttributeSet attrs) {
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
        sbProgress.setMax(1000);
        sbProgress.setOnSeekBarChangeListener(this);
        ivPlay.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivFullscreen.setOnClickListener(this);

    }

    /**
     * Attach video view.
     *
     * @param view       the view
     * @param controller the controller
     */
    public void attachVideoView(View view, VideoController controller) {
        addView(view, 0);
        this.view = view;
        this.controller = controller;
        startListener();

    }

    private void startListener() {
        messageHandler.sendEmptyMessage(Constant.MESSAGE_SHOW_PROGRESS);
        messageHandler.sendEmptyMessage(Constant.MESSAGE_IS_PLAYING);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play) {
            doPauseResume();
        } else if (view.getId() == R.id.iv_back) {

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
        if (controller == null || isDragging) {
            return 0;
        }
        long position = controller.getCurrentPosition();
        long duration = controller.getDuration();
        if (sbProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                sbProgress.setProgress((int) pos);
            }
            double percent = controller.getCachePercentage();
            sbProgress.setSecondaryProgress((int) (percent * 10));
        }

        if (tvEndTime != null)
            tvEndTime.setText(stringForTime(duration));
        if (tvCurrentTime != null)
            tvCurrentTime.setText(stringForTime(position));
        return position;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long duration = controller.getDuration();
        long newposition = (duration * progress) / 1000L;
        controller.seekTo((int) newposition);
        if (tvCurrentTime != null)
            tvCurrentTime.setText(stringForTime((int) newposition));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isDragging = true;
        messageHandler.removeMessages(Constant.MESSAGE_SHOW_PROGRESS);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isDragging = false;
        messageHandler.sendEmptyMessage(Constant.MESSAGE_SHOW_PROGRESS);
    }

    private void doPauseResume() {
        if (controller.isPlaying()) {
            controller.pause();
        } else {
            controller.start();
        }
    }

    private void updatePlayIcon() {
        if (controller.isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_index_video_pause);
        } else {
            ivPlay.setImageResource(R.drawable.ic_index_video_play);
        }
    }


    /**
     * The interface Video controller.
     */
    public interface VideoController {
        /**
         * Start.
         */
        void start();

        /**
         * Pause.
         */
        void pause();

        /**
         * Stop.
         */
        void stop();

        /**
         * Seek to.
         *
         * @param progress the progress
         */
        void seekTo(long progress);

        /**
         * Is playing boolean.
         *
         * @return the boolean
         */
        boolean isPlaying();

        /**
         * Gets current position.
         *
         * @return the current position
         */
        long getCurrentPosition();

        /**
         * 视频的duration
         *
         * @return the duration
         */
        long getDuration();

        /**
         * 传入缓存的进度，100进制。
         *
         * @return the cache percentage
         */
        long getCachePercentage();

    }

    /**
     * The type Message handler.
     */
    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_SHOW_PROGRESS:
                    msg = obtainMessage(Constant.MESSAGE_SHOW_PROGRESS);
                    long pos = changeProgress();
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
                case Constant.MESSAGE_IS_PLAYING:
                    isPlaying = controller.isPlaying();
                    updatePlayIcon();
                    sendEmptyMessage(Constant.MESSAGE_IS_PLAYING);
            }
        }
    }
}
