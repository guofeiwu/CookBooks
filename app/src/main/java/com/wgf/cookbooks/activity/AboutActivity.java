package com.wgf.cookbooks.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.view.CustomToolbar;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 意见反馈
 */
public class AboutActivity extends AppCompatActivity {
    private CustomToolbar mCustomToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_about_back);
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }
}
