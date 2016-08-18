package com.meunicorn.videocontrolview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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

    private boolean isDragging = false;

    public VideoControlView(Context context) {
        this(context, null);
    }

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

    }

    public void attachVideoView(View view, VideoController controller) {
        addView(view, 0);
        this.view = view;
        this.controller = controller;
        messageHandler.sendEmptyMessage(Constant.MESSAGE_SHOW_PROGRESS);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play) {
            if (controller.isPlaying()) {
                controller.pause();
            } else {
                controller.start();
            }
        } else if (view.getId() == R.id.iv_back) {
            Toast.makeText(view.getContext(), "you click back", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 毫秒转为播放的时间，格式为 mm:ss
     *
     * @param timeMs 毫秒
     * @return 时间 string
     */
    public String stringForTime(long timeMs) {
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
            int percent = controller.getBufferPercentage();
            sbProgress.setSecondaryProgress(percent * 10);
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
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
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


    public interface VideoController {
        void start();

        void pause();

        void stop();

        void seekTo(long progress);

        boolean isPlaying();

        long getCurrentPosition();

        long getDuration();

        int getBufferPercentage();

    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_SHOW_PROGRESS:
                    msg = obtainMessage(Constant.MESSAGE_SHOW_PROGRESS);
                    long pos = changeProgress();
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
            }
        }
    }
}
