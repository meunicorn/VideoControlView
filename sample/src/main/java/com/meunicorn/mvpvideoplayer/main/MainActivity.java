package com.meunicorn.mvpvideoplayer.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.meunicorn.mvpvideoplayer.BaseActivity;
import com.meunicorn.mvpvideoplayer.R;
import com.meunicorn.mvpvideoplayer.main.bean.Video;
import com.meunicorn.mvpvideoplayer.videodetail.VideoDetailActivity;
import com.meunicorn.mvpvideoplayer.videodetail.VideoDetailActivityPro;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends BaseActivity implements MainMvpView, View.OnClickListener {
    private RecyclerView rvVideo;
    private List<Video.DataEntity.ListEntity> listEntityList = new ArrayList<>();
    private MainPresenter presenter = new MainPresenter();
    private VideoRvAdapter adapter;
    private int page = 1;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        presenter.attachView(this);
        rvVideo = (RecyclerView) findViewById(R.id.rv_video);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                type++;
                RecyclerView.LayoutManager layoutManager;
                switch (type%3) {
                    case 0:
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        break;
                    case 1:
                        layoutManager = new GridLayoutManager(MainActivity.this, 4, LinearLayoutManager.HORIZONTAL, false);
                        break;
                    case 2:
                        layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
                        break;
                    default:
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                }
                rvVideo.setLayoutManager(layoutManager);
                return true;
            }
        });
        adapter = new VideoRvAdapter(this, listEntityList);
        rvVideo.setLayoutManager(new LinearLayoutManager(this));
        rvVideo.setAdapter(adapter);
        adapter.onItemClick().subscribe(new Action1<Video.DataEntity.ListEntity>() {
            @Override
            public void call(Video.DataEntity.ListEntity listEntity) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("url", listEntity.getVideo_url());
                intent.putExtra("title", listEntity.getTitle());
                intent.putExtra("imgUrl", listEntity.getImg_url());
                startActivity(intent);
            }
        });
        adapter.onItemLongClick().subscribe(new Action1<Video.DataEntity.ListEntity>() {
            @Override
            public void call(Video.DataEntity.ListEntity listEntity) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivityPro.class);
                intent.putExtra("url", listEntity.getVideo_url());
                intent.putExtra("title", listEntity.getTitle());
                intent.putExtra("imgUrl", listEntity.getImg_url());
                startActivity(intent);
            }
        });

        presenter.getVideoList(page, "hot", "test");

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
        listEntityList.clear();
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
                presenter.getVideoList(page++, "hot", "test");
                break;
        }
    }
}
