package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuRankRecycleViewAdapter;
import com.wgf.cookbooks.adapter.UserMenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.MenuAsyncTask;
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
public class MenuHotActivity extends AppCompatActivity implements MenuRankAsyncTask.IGetMenuRankListener, UserMenuRecycleViewAdapter.IMenuDetailListener {
    private CustomToolbar mCustomToolbar;
    private RecyclerView mRankRecyclerView;
    private MenuRankAsyncTask mMenuRankAsyncTask;
    private UserMenuRecycleViewAdapter mAdapter;
    private List<Menu> menus;
    private String url = null;
    private int pageNo = 1;
    private boolean isLoading = true;
    private boolean haveData = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rank);
        initView();
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                if (itemViewType == mAdapter.TYPE_FOOTER) {//判断当前类型是否是FootView
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });

        mRankRecyclerView.setLayoutManager(gridLayoutManager);


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


        //添加滑动事件
        mRankRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mMenuRankAsyncTask != null) {
                        return;
                    }
                    mMenuRankAsyncTask = new MenuRankAsyncTask(MenuHotActivity.this);
                    mMenuRankAsyncTask.setmListener(MenuHotActivity.this);
                    ++pageNo;
                    url = BASE_URL + "/app/menu/lookRank/" + pageNo;
                    mMenuRankAsyncTask.execute(url);
                } else if (!isLoading && haveData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(MenuHotActivity.this, "加载菜谱中...");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRankRecyclerView.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
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
        if (menus != null) {
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();

        url = BASE_URL + "/app/menu/lookRank/" + pageNo;
        mCustomToolbar.setToolbarTitle(getString(R.string.text_hot_menu));
        if (mMenuRankAsyncTask != null) {
            return;
        }
        mMenuRankAsyncTask = new MenuRankAsyncTask(this);
        mMenuRankAsyncTask.setmListener(this);
        mMenuRankAsyncTask.execute(url);

    }

    @Override
    public void result(List<Menu> list) {
        if (mMenuRankAsyncTask != null) {
            mMenuRankAsyncTask = null;
        }
        if (list != null && list.size() > 0) {
            menus.addAll(list);
            if (mAdapter == null) {
                mAdapter = new UserMenuRecycleViewAdapter(this, list);
                mAdapter.setmListener(this);
                mRankRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        } else {
            haveData = false;
            if (mAdapter != null) {
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(this,getString(R.string.text_hot_menu_load_complete));
        }
    }

    @Override
    public void detail(int position) {
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        startActivity(intent);
    }
}
