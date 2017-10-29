package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuRankRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.MenuRankAsyncTask;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MenuRankActivity extends AppCompatActivity implements MenuRankAsyncTask.IGetMenuRankListener,MenuRankRecycleViewAdapter.IMenuDetailListener{
    private CustomToolbar mCustomToolbar;
    private RecyclerView mRankRecyclerView;
    private int flag = -1;
    private MenuRankAsyncTask mMenuRankAsyncTask;
    private MenuRankRecycleViewAdapter mAdapter;
    private List<Menu> menus;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rank);
        //这个是用来判断加载哪种类型的排行，1表示是从点赞排行，2表示是收藏排行，3表示浏览排行
        flag = getIntent().getIntExtra("flag",-1);
        initView();
        setListener();
        initData();

    }

    /**
     * 设置监听
     */
    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_rank_back);
        mRankRecyclerView = (RecyclerView) findViewById(R.id.id_rv_rank);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if(menus!=null){
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();

        String url = null;
        if(flag == 1){
            url = BASE_URL+"/app/menu/likeRank";
            mCustomToolbar.setToolbarTitle(getString(R.string.text_like_rank));
        }else if(flag ==2 ){
            url = BASE_URL+"/app/menu/collectRank";
            mCustomToolbar.setToolbarTitle(getString(R.string.text_collect_rank));
        }else if(flag == 3){
            url = BASE_URL+"/app/menu/lookRank/1";
            mCustomToolbar.setToolbarTitle(getString(R.string.text_look_rank));
        }
        if(mMenuRankAsyncTask!=null){
            return;
        }
        mMenuRankAsyncTask = new MenuRankAsyncTask(this);
        mMenuRankAsyncTask.setmListener(this);
        mMenuRankAsyncTask.execute(url);

    }

    @Override
    public void result(List<Menu> list) {
        if(mMenuRankAsyncTask!=null){
            mMenuRankAsyncTask = null;
        }
        if(list!=null && list.size()>0){
            menus.addAll(list);
            mRankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new MenuRankRecycleViewAdapter(this,list);
            mAdapter.setmListener(this);
            mRankRecyclerView.setAdapter(mAdapter);
        }else{
            ToastUtils.toast(this,getString(R.string.text_failed_msg));
        }
    }

    @Override
    public void detail(int position) {
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        startActivity(intent);
    }
}
