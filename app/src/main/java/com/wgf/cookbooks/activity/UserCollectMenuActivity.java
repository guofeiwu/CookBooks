package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.UserMenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.GetUserCollectMenuAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.MenuAsyncTask;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UserCollectMenuActivity extends AppCompatActivity implements
        GetUserCollectMenuAsyncTask.IGetUserCollectMenuListener, UserMenuRecycleViewAdapter.IMenuDetailListener {
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private UserMenuRecycleViewAdapter mAdapter;
    private GetUserCollectMenuAsyncTask mGetUserCollectMenuAsyncTask;
    //初始化页数
    private int pageNo = 1;
    private List<Menu> menus;
    //正在加载
    private boolean isLoading = true;
    //有数据
    private boolean havaData = true;
    //第一次加载数据
    private boolean firstLoading = true;
    private TextView mNoCollect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collect_menu);

        initView();

        setListener();

        setData();
    }

    /**
     * 设置数据
     */
    private void setData() {

        if (menus != null) {
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();

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

        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mGetUserCollectMenuAsyncTask != null) {
            return;
        }
        mGetUserCollectMenuAsyncTask = new GetUserCollectMenuAsyncTask(this);
        mGetUserCollectMenuAsyncTask.setmListener(this);
        mGetUserCollectMenuAsyncTask.execute(pageNo);
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mGetUserCollectMenuAsyncTask != null) {
                        return;
                    }
                    mGetUserCollectMenuAsyncTask = new GetUserCollectMenuAsyncTask(UserCollectMenuActivity.this);
                    mGetUserCollectMenuAsyncTask.setmListener(UserCollectMenuActivity.this);
                    mGetUserCollectMenuAsyncTask.execute(++pageNo);
                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(UserCollectMenuActivity.this, "加载菜谱中...");
                    mAdapter.setLoadStatus(2);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_user_collect);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_user_collect_back);
        mNoCollect = (TextView) findViewById(R.id.id_tv_no_collect);
    }

    @Override
    public void userCollectMenus(List<Menu> list) {
        if (mGetUserCollectMenuAsyncTask != null) {
            mGetUserCollectMenuAsyncTask = null;
        }
        if (list != null && list.size() > 0) {
            //不是第一加载数据
            firstLoading = false;
            menus.addAll(list);
            if (mAdapter == null) {
                mAdapter = new UserMenuRecycleViewAdapter(this, list);
                mAdapter.setmListener(this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        } else {
            //无数据，并且是第一次
            if (firstLoading) {
                mRecyclerView.setVisibility(View.GONE);
                mNoCollect.setVisibility(View.VISIBLE);
            } else {
                havaData = false;
                if (mAdapter != null) {
                    mAdapter.setLoadStatus(0);
                }
                ToastUtils.toast(this, "菜谱已全部加载完成...");
            }
        }
    }

    /**
     * 查看菜谱详情的回调
     *
     * @param position
     */
    @Override
    public void detail(int position) {
        ToastUtils.toast(this, "detail:" + position);
        //跳转界面，显示详情
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        intent.putExtra("menuPos", position);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        int pos = SpUtils.getSharedPreferences(this).getInt("cancelCollectPosition", -1);
        if (pos != -1) {
            //说明取消收藏了
            menus.remove(pos);
            mAdapter.removeItem(pos);
            SpUtils.getEditor(this).putInt("cancelCollectPosition", -1).commit();
            if(menus.size() == 0){
                mRecyclerView.setVisibility(View.GONE);
                mNoCollect.setVisibility(View.VISIBLE);
            }

        }
    }
}
