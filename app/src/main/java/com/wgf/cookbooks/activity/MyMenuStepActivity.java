package com.wgf.cookbooks.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.view.CustomToolbar;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 美食足迹
 */
public class MyMenuStepActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu_step);
        initView();
        setListener();
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
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_user_record_back);
        mRecyclerView  = (RecyclerView) findViewById(R.id.id_rv_menu_record);
    }
}
