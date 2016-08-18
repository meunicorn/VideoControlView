package com.meunicorn.mvpvideoplayer.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.meunicorn.mvpvideoplayer.BaseActivity;
import com.meunicorn.mvpvideoplayer.R;
import com.meunicorn.mvpvideoplayer.main.bean.Video;
import com.meunicorn.mvpvideoplayer.videodetail.VideoDetailActivity;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends BaseActivity implements MainMvpView, View.OnClickListener {
    private RecyclerView rvVideo;
    private FloatingActionButton fab;
    private List<Video.DataEntity.ListEntity> listEntityList = new ArrayList<>();
    private MainPresenter presenter = new MainPresenter();
    private VideoRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        presenter.attachView(this);
        rvVideo = (RecyclerView) findViewById(R.id.rv_video);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        adapter = new VideoRvAdapter(this, listEntityList);
        rvVideo.setLayoutManager(new LinearLayoutManager(this));
        rvVideo.setAdapter(adapter);
        adapter.onItemClick().subscribe(new Action1<Video.DataEntity.ListEntity>() {
            @Override
            public void call(Video.DataEntity.ListEntity listEntity) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("url", listEntity.getVideo_url());
                intent.putExtra("title", listEntity.getTitle());
                startActivity(intent);
            }
        });

        presenter.getVideoList(1, "hot", "test");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showVideoList(List<Video.DataEntity.ListEntity> videoList) {
        listEntityList.addAll(videoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showGetError(String error) {
        Snackbar.make(rvVideo, error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (rvVideo.getLayoutManager() instanceof LinearLayoutManager) {
                    rvVideo.setLayoutManager(new GridLayoutManager(this, 2));
                } else {
                    rvVideo.setLayoutManager(new LinearLayoutManager(this));
                }
                break;
        }
    }
}
