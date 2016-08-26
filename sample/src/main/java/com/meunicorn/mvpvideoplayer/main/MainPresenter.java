package com.meunicorn.mvpvideoplayer.main;

import com.meunicorn.mvpvideoplayer.BasePresenter;
import com.meunicorn.mvpvideoplayer.common.Url;
import com.meunicorn.mvpvideoplayer.common.VideoService;
import com.meunicorn.mvpvideoplayer.main.bean.Video;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Fancy on 2016/8/17
 */
public class MainPresenter extends BasePresenter<MainMvpView> {
    public void getVideoList(int page, String type, String fkey) {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        VideoService service = retrofit.create(VideoService.class);
        service.getVideoList(page, type, fkey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Video>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.toString());
//                        getMvpView().showGetError(e.toString());
                    }

                    @Override
                    public void onNext(Video video) {
                        Timber.i("siez is %d",video.getData().getList().size());
                        getMvpView().showVideoList(video.getData().getList());
                    }
                });
    }
}
