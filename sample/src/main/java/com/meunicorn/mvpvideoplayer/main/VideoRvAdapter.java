package com.meunicorn.mvpvideoplayer.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meunicorn.mvpvideoplayer.R;
import com.meunicorn.mvpvideoplayer.main.bean.Video;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Fancy on 2016/8/17.
 */
public class VideoRvAdapter extends RecyclerView.Adapter<VideoRvAdapter.MyViewHolder> {
    private final PublishSubject<Video.DataEntity.ListEntity> onClickSubject = PublishSubject.create();
    private Context context;
    private List<Video.DataEntity.ListEntity> videoList;
    private View view;
    private int lastPosition = -1;

    public VideoRvAdapter(Context context, List<Video.DataEntity.ListEntity> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.sdvCover.setImageURI(videoList.get(position).getVideo_cover());
        holder.tvTitle.setText(videoList.get(position).getTitle());
        holder.cvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubject.onNext(videoList.get(position));
            }
        });
        setAnimation(holder.cvVideo, position);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public Observable<Video.DataEntity.ListEntity> onItemClick() {
        return onClickSubject.asObservable();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView sdvCover;
        private CardView cvVideo;
        private TextView tvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            sdvCover= (SimpleDraweeView) itemView.findViewById(R.id.sdv_video_cover);
            tvTitle= (TextView) itemView.findViewById(R.id.tv_video_title);
            cvVideo= (CardView) itemView.findViewById(R.id.cv_video);
        }
    }
}
