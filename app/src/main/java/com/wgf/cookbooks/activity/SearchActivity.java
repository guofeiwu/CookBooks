package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.MenuAsyncTask;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 搜索界面
 */
public class SearchActivity extends AppCompatActivity implements MenuAsyncTask.IGetMenuListener
        , MenuRecycleViewAdapter.IMenuDetailListener {
    private EditText mEditTextSearch;
    private ImageView mImageViewBack;
    private RecyclerView mRecyclerViewSearch;
    private MenuAsyncTask mMenuAsyncTask;
    private Map map;//查询的条件
    private List<Menu> menus;

    private MenuRecycleViewAdapter mAdapter;
    private boolean isLoading = true;//没在加载
    private boolean havaData = true;//有数据
    private int pageNo = 1;//第几页
    private String keyword;//查找的关键字
    private TextView mNoSearch;//没有相关内容

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();

        setListener();

    }

    /**
     * 设置监听
     */
    private void setListener() {
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SoftInputUtils.hideSoftInput(SearchActivity.this);
            }
        });

        mEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = mEditTextSearch.getText().toString();
                    if (TextUtils.isEmpty(keyword)) {
                        ToastUtils.toast(SearchActivity.this, "请输入要搜索的内容");
                        return true;
                    } else {
                        if(menus!=null && menus.size()>0){
                            menus.clear();
                        }
                        if (mAdapter != null) {
                            mAdapter= null;
                        }
                        if (mMenuAsyncTask != null) {
                            return true;
                        }
                        map = new HashMap();
                        map.put("pageNo", 1);
                        map.put("keyword", keyword);

                        mMenuAsyncTask = new MenuAsyncTask(SearchActivity.this);
                        mMenuAsyncTask.setmListener(SearchActivity.this);
                        mMenuAsyncTask.execute(map);
                        //ToastUtils.toast(SearchActivity.this,keyword);
                    }
                    return true;
                }
                return false;
            }
        });


        mRecyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem+1 == mAdapter.getItemCount()){
                    isLoading = false;//正在加载
                    mAdapter.setLoadStatus(1);//上拉加载更多数据
                    if(mMenuAsyncTask!=null){
                        return;
                    }
                    mMenuAsyncTask = new MenuAsyncTask(SearchActivity.this);
                    mMenuAsyncTask.setmListener(SearchActivity.this);
                    map.put("pageNo",++pageNo);
                    map.put("keyword", keyword);
                    mMenuAsyncTask.execute(map);
                }else  if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(SearchActivity.this, "加载菜谱中...");
                    mAdapter.setLoadStatus(2);//正在加载更多数据
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerViewSearch.getLayoutManager();
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }
    /**
     * 绑定控件
     */
    private void initView() {
        mEditTextSearch = (EditText) findViewById(R.id.id_et_search);
        mImageViewBack = (ImageView) findViewById(R.id.id_iv_back);
        mRecyclerViewSearch = (RecyclerView) findViewById(R.id.id_rv_search);
        mNoSearch = (TextView) findViewById(R.id.id_tv_no_search);
    }

    //返回搜索菜谱结果的回调
    @Override
    public void result(List<Menu> list) {
        if (menus == null) {
            menus = new ArrayList<>();
        }
        if (list != null && list.size()>0) {
            menus.addAll(list);
            if (mAdapter == null) {
                mAdapter = new MenuRecycleViewAdapter(this, list);
                mRecyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
                mAdapter.setmListener(this);
                mRecyclerViewSearch.setAdapter(mAdapter);
            } else {
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
            mNoSearch.setVisibility(View.GONE);
            mRecyclerViewSearch.setVisibility(View.VISIBLE);
        }else{
            havaData = false;
            if(pageNo == 1){//第一页数据都没有
                mNoSearch.setVisibility(View.VISIBLE);
                mRecyclerViewSearch.setVisibility(View.GONE);
                mNoSearch.setText("暂无更多关于\""+keyword+"\"的内容");
                SoftInputUtils.hideSoftInput(this);
            }
            if (mAdapter!=null){
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(this,"暂无更多搜索结果...");
        }
        if(mMenuAsyncTask!=null){
            mMenuAsyncTask = null;
        }
    }


    //点击查看菜谱详情的回调
    @Override
    public void detail(int position) {
        //跳转界面，显示详情
        Intent intent = new Intent(SearchActivity.this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMenuAsyncTask!=null){
            mMenuAsyncTask = null;
        }

        if(menus!=null){
            menus.clear();
            menus = null;
        }
    }
}
