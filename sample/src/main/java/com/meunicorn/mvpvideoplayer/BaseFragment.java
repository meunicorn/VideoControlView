package com.meunicorn.mvpvideoplayer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class BaseFragment extends Fragment {
    String TAG = "|-> " + getClass().getSimpleName() + " <-|";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s has been created", TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.i("%s call onCreateView", TAG);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("%s has been destoryed", TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("%s call onDestoryView", TAG);
    }
}
