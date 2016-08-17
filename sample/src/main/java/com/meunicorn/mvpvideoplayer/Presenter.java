package com.meunicorn.mvpvideoplayer;

/**
 * Created by meunicorn on 2016/8/9.
 */

public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}