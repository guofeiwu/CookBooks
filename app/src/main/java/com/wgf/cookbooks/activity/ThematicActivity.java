package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ThematicMenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.bean.Thematic;
import com.wgf.cookbooks.clazz.asynctask.GetThematicMenuAsyncTask;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class ThematicActivity extends AppCompatActivity implements GetThematicMenuAsyncTask.IGetThematicMenuListener,ThematicMenuRecycleViewAdapter.IMenuDetailListener{
    private CustomToolbar mCustomToolbar;
    private RecyclerView mThematicRecyclerView;
    private List<Menu> menus;
    private GetThematicMenuAsyncTask mGetThematicMenuAsyncTask;
    private Map<String,Object> map;
    private int pageNo = 1;
    private ThematicMenuRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private boolean haveData = true;
    private String type = null;

    //传递过来的
    private Thematic thematic;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thematic);
        thematic = (Thematic) getIntent().getExtras().getSerializable("thematic");
        type = thematic.getThematicName();

        initView();
        setListener();
        initData();

    }

    /**
     * 初始化数据
     */
    private void initData() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                if (itemViewType == mAdapter.TYPE_FOOTER ||itemViewType == mAdapter.TYPE_HEAD) {//判断当前类型是否是FootView
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
        mThematicRecyclerView.setLayoutManager(gridLayoutManager);

            if(menus!=null){
                menus.clear();
                menus = null;
            }
        menus  = new ArrayList<>();

        map = new HashMap<>();

        if(mGetThematicMenuAsyncTask!=null){
            return;
        }

        mGetThematicMenuAsyncTask = new GetThematicMenuAsyncTask(this);
        mGetThematicMenuAsyncTask.setmListener(this);

        map.put("pageNo",pageNo);
        map.put("type",type);
        mGetThematicMenuAsyncTask.execute(map);
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
        mThematicRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mGetThematicMenuAsyncTask != null) {
                        return;
                    }
                    mGetThematicMenuAsyncTask = new GetThematicMenuAsyncTask(ThematicActivity.this);
                    mGetThematicMenuAsyncTask.setmListener(ThematicActivity.this);
                    map.put("pageNo", ++pageNo);
                    mGetThematicMenuAsyncTask.execute(map);
                } else if (!isLoading && haveData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(ThematicActivity.this, "加载菜谱中...");
                    mAdapter.setLoadStatus(2);//正在加载更多数据
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mThematicRecyclerView.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_thematic_back);
        mThematicRecyclerView = (RecyclerView) findViewById(R.id.id_rv_thematic);
    }

    /**
     * 获取专题菜谱的回调
     * @param list
     */
    @Override
    public void thematicMenus(List<Menu> list) {
        if(mGetThematicMenuAsyncTask !=null){
            mGetThematicMenuAsyncTask = null;
        }
        if(list !=null && list.size()> 0){
            menus.addAll(list);
            if(mAdapter== null){
                mAdapter = new ThematicMenuRecycleViewAdapter(this,thematic,list);
                mAdapter.setmListener(this);
                mThematicRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        }else{
            haveData = false;
            if (mAdapter != null) {
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(this, "专题菜谱已全部加载完成...");
        }
    }

    @Override
    public void detail(int position) {
        //跳转界面，显示详情
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
//        intent.putExtra("menuPos", position);
        startActivity(intent);
    }
}
