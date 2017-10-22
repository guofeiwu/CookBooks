package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.GetUserMenuAsyncTask;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UserMenuActivity extends AppCompatActivity implements GetUserMenuAsyncTask.IGetUserMenuListener ,MenuRecycleViewAdapter.IMenuDetailListener {
    private EditText mSearch;
    private TextView mMenus;
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private GetUserMenuAsyncTask mGetUserMenuAsyncTask;
    private int pageNo = 1;//页数，初始化为第一页
    private List<Menu> menus;
    private MenuRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private NestedScrollView mNestedScrollView;
    private boolean havaData = true;
    private RelativeLayout mAddMenu;
    //还未发布菜谱的提示
    private TextView mNoMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
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
                SoftInputUtils.hideSoftInput(UserMenuActivity.this);
        }
        });



        //添加滑动事件
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && isLoading) {
                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if(mGetUserMenuAsyncTask!=null){
                        return;
                    }
                    mGetUserMenuAsyncTask = new GetUserMenuAsyncTask(UserMenuActivity.this);
                    mGetUserMenuAsyncTask.setmListener(UserMenuActivity.this);
                    mGetUserMenuAsyncTask.execute(++pageNo);
                }else if(scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && !isLoading && havaData){
                    ToastUtils.toast(UserMenuActivity.this, "加载菜谱中...");
                }
            }
        });
        mAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.jump(UserMenuActivity.this,AddMenuActivity.class);
            }
        });


        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.jump(UserMenuActivity.this, SearchActivity.class);
            }
        });



    }

    /**
     * 初始化控件
     */
    private void initView() {
        mSearch = (EditText) findViewById(R.id.id_et_search);
        mMenus = (TextView) findViewById(R.id.id_tv_menus);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_user_menu);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_user_menus_back);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.id_nsv_layout);
        mAddMenu = (RelativeLayout) findViewById(R.id.id_rl_add_menu);
        mNoMenu = (TextView) findViewById(R.id.id_tv_no_menu);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        if(menus!=null){
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();
        if(mGetUserMenuAsyncTask!=null){
            return;
        }
        mGetUserMenuAsyncTask = new GetUserMenuAsyncTask(this);
        mGetUserMenuAsyncTask.setmListener(this);
        mGetUserMenuAsyncTask.execute(pageNo);
    }

    /**
     * 获取用户发布的菜谱的回调
     * @param list
     */
    @Override
    public void userMenus(List<Menu> list) {
        if(list!=null && list.size()>0){
            menus.addAll(list);
            mMenus.setText("全部菜谱("+menus.size()+")");
            if(mAdapter == null){
                mAdapter = new MenuRecycleViewAdapter(this,list);
                //设置点击事件的回调监听
                mAdapter.setmListener(this);
                //mRecyclerView.addItemDecoration(mRecycleDivider);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        }else{
            havaData = false;
            if (mAdapter!=null){
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(this,"菜谱已全部加载完成...");

            //加载所有后还是无数据
            if(menus.size() == 0 ){
                mNoMenu.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

        }
        if(mGetUserMenuAsyncTask!=null){
            mGetUserMenuAsyncTask = null;
        }
    }

    @Override
    public void detail(int position) {
        //跳转界面，显示详情
        Intent intent = new Intent(this,MenuDetailActivity.class);
        intent.putExtra("menuPkId",menus.get(position).getMenuPkId());
        startActivity(intent);
    }
}
