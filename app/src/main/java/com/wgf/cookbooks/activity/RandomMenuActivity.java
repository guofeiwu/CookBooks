package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.UserMenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.GetRandomMenuAsyncTask;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class RandomMenuActivity extends AppCompatActivity implements GetRandomMenuAsyncTask.IGetRandomMenuListener ,UserMenuRecycleViewAdapter.IMenuDetailListener{
    private RecyclerView mRecyclerView;
    private UserMenuRecycleViewAdapter mAdapter;
    private List<Menu> menus;
    private GetRandomMenuAsyncTask mGetRandomMenuAsyncTask;
    private CustomToolbar mCustomToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_menu);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_random_menu);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_random_menus_back);

        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

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

        if (mGetRandomMenuAsyncTask != null) {
            return;
        }
        mGetRandomMenuAsyncTask = new GetRandomMenuAsyncTask(this);
        mGetRandomMenuAsyncTask.setmListener(this);
        mGetRandomMenuAsyncTask.execute();
    }

    @Override
    public void randomMenus(List<Menu> list) {
        if (list != null && list.size() > 0) {
            menus.addAll(list);
            mAdapter = new UserMenuRecycleViewAdapter(this, list);
            mAdapter.setmListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void detail(int position) {
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        startActivity(intent);
    }
}