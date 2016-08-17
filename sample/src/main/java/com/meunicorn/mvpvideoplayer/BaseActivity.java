package com.meunicorn.mvpvideoplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * Created by meunicorn on 2016/8/9.
 */

public class BaseActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s has been created", TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("%s has been destoryed", TAG);
    }
}
