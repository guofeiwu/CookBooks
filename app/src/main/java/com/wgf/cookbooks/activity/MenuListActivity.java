package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.MenuAsyncTask;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 一日三餐的界面
 */
public class MenuListActivity extends AppCompatActivity implements MenuAsyncTask.IGetMenuListener, MenuRecycleViewAdapter.IMenuDetailListener {
    private CustomToolbar mCustomToolbar;
    private RecyclerView mThreeMealsRecyclerView;
    private MenuRecycleViewAdapter mAdapter;
    private RecycleDivider mRecycleDivider;
    private int position;//位置
    private MenuAsyncTask mMenuAsyncTask;
    private List<Menu> menus;
    private boolean isLoading = true;//没在加载数据
    private int pageNo = 1;//第几页
    private Map map;//条件
    private boolean havaData = true;//有数据
    private String type;//分类的类型
    private boolean firstLoad = false;//是否是第一次加载数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Intent intent = getIntent();
        position = intent.getIntExtra("pos", 0);
        type = intent.getStringExtra("type");

        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_three_meal);
        mThreeMealsRecyclerView = (RecyclerView) findViewById(R.id.id_rv_three_meal);

        mThreeMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(this, RecycleDivider.VERITCAL_LIST);
        mThreeMealsRecyclerView.addItemDecoration(mRecycleDivider);


        initData();


        setListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        menus = new ArrayList<>();
        if (mMenuAsyncTask != null) {
            return;
        }

        if (type.equals(Constants.YIRISANCAN)) {
            map = new HashMap();
            map.put("pageNo", pageNo);
            map.put("pType", 0);//一日三餐
            switch (position) {
                case 0://早餐
                    map.put("sunType", 0);
                    mCustomToolbar.setToolbarTitle("早餐");
                    break;
                case 1://中餐
                    map.put("sunType", 1);
                    mCustomToolbar.setToolbarTitle("中餐");
                    break;
                case 2://晚餐
                    map.put("sunType", 2);
                    mCustomToolbar.setToolbarTitle("晚餐");
                    break;
                case 3://夜宵
                    map.put("sunType", 3);
                    mCustomToolbar.setToolbarTitle("夜宵");
                    break;
            }
        } else if (type.equals(Constants.CAISHI)) {
            map = new HashMap();
            map.put("pageNo", pageNo);
            map.put("pType", 1);//菜式
            switch (position) {
                case 0://家常菜
                    map.put("sunType", 0);
                    mCustomToolbar.setToolbarTitle("家常菜");
                    break;
                case 1://素菜
                    map.put("sunType", 1);
                    mCustomToolbar.setToolbarTitle("素菜");
                    break;
                case 2://汤
                    map.put("sunType", 2);
                    mCustomToolbar.setToolbarTitle("汤");
                    break;
                case 3://凉菜
                    map.put("sunType", 3);
                    mCustomToolbar.setToolbarTitle("凉菜");
                    break;
                case 4://私房菜
                    map.put("sunType", 4);
                    mCustomToolbar.setToolbarTitle("私房菜");
                    break;
                case 5://荤菜
                    map.put("sunType", 5);
                    mCustomToolbar.setToolbarTitle("荤菜");
                    break;
            }
        } else if (type.equals(Constants.CAIXI)) {
            map = new HashMap();
            map.put("pageNo", pageNo);
            map.put("pType", 2);//菜系
            switch (position) {
                case 0://川菜
                    map.put("sunType", 0);
                    mCustomToolbar.setToolbarTitle("川菜");
                    break;
                case 1://粤菜
                    map.put("sunType", 1);
                    mCustomToolbar.setToolbarTitle("粤菜");
                    break;
                case 2://东北菜
                    map.put("sunType", 2);
                    mCustomToolbar.setToolbarTitle("东北菜");
                    break;
                case 3://湘菜
                    map.put("sunType", 3);
                    mCustomToolbar.setToolbarTitle("湘菜");
                    break;
                case 4://鲁菜
                    map.put("sunType", 4);
                    mCustomToolbar.setToolbarTitle("鲁菜");
                    break;
                case 5://清真
                    map.put("sunType", 5);
                    mCustomToolbar.setToolbarTitle("清真");
                    break;
            }
        } else if (type.equals(Constants.TIANDIAN)) {
            map = new HashMap();
            map.put("pageNo", pageNo);
            map.put("pType", 3);//烘焙甜点
            switch (position) {
                case 0://蛋糕
                    map.put("sunType", 0);
                    mCustomToolbar.setToolbarTitle("蛋糕");
                    break;
                case 1://饼干
                    map.put("sunType", 1);
                    mCustomToolbar.setToolbarTitle("饼干");
                    break;
                case 2://蛋挞
                    map.put("sunType", 2);
                    mCustomToolbar.setToolbarTitle("蛋挞");
                    break;
                case 3://饮品
                    map.put("sunType", 3);
                    mCustomToolbar.setToolbarTitle("饮品");
                    break;
            }
        } else if (type.equals(Constants.ZHUSHI)) {
            map = new HashMap();
            map.put("pageNo", pageNo);
            map.put("pType", 4);//主食
            switch (position) {
                case 0://饭
                    map.put("sunType", 0);
                    mCustomToolbar.setToolbarTitle("饭");
                    break;
                case 1://面
                    map.put("sunType", 1);
                    mCustomToolbar.setToolbarTitle("面");
                    break;
                case 2://糕点
                    map.put("sunType", 2);
                    mCustomToolbar.setToolbarTitle("糕点");
                    break;
                case 3://粥
                    map.put("sunType", 3);
                    mCustomToolbar.setToolbarTitle("粥");
                    break;
                case 4://米粉
                    map.put("sunType", 4);
                    mCustomToolbar.setToolbarTitle("米粉");
                    break;
            }
        }
        mMenuAsyncTask = new MenuAsyncTask(this);
        mMenuAsyncTask.setmListener(this);
        mMenuAsyncTask.execute(map);
        firstLoad = true;
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        //添加滑动事件
        mThreeMealsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mMenuAsyncTask != null) {
                        return;
                    }
                    mMenuAsyncTask = new MenuAsyncTask(MenuListActivity.this);
                    mMenuAsyncTask.setmListener(MenuListActivity.this);
                    map.put("pageNo", ++pageNo);
                    mMenuAsyncTask.execute(map);
                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(MenuListActivity.this, "加载菜谱中...");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mThreeMealsRecyclerView.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
            }
        });


    }

    @Override
    public void result(List<Menu> list) {
        if (list != null && list.size() > 0) {
            firstLoad = false;
            menus.addAll(list);
            if (mAdapter == null) {
                mAdapter = new MenuRecycleViewAdapter(this, list);
                //设置点击事件的回调监听
                mAdapter.setmListener(this);
                mThreeMealsRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        } else {
            //第一次加载
            if(firstLoad){
                mThreeMealsRecyclerView.setVisibility(View.GONE);
                firstLoad = false;
            }
            havaData = false;
            if (mAdapter != null) {
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(this, "菜谱已全部加载完成...");
        }
        if (mMenuAsyncTask != null) {
            mMenuAsyncTask = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMenuAsyncTask != null) {
            mMenuAsyncTask = null;
        }

        if (menus != null) {
            menus = null;
        }
    }

    @Override
    public void detail(int position) {
        //ToastUtils.toast(this,"pos:"+position);
        //跳转界面，显示详情
        Intent intent = new Intent(MenuListActivity.this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        startActivity(intent);
    }

}
