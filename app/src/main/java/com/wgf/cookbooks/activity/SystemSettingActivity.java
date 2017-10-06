package com.wgf.cookbooks.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.SwitchAnimationUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.SHOW_DATA;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 系统相关的设置
 */
public class SystemSettingActivity  extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout mLogout,mUserInfo,mChangePhone,mModifyPassword;
    private LinearLayout mLayoutAccount;
    private SharedPreferences mSharedPreferences;
    private String token;
    private CustomToolbar mCustomToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SwitchAnimationUtils.enterActivitySlideRight(this);
        SwitchAnimationUtils.exitActivitySlideLeft(this);

        setContentView(R.layout.activity_system_setting);

        initView();

        initListener();

        initLayout();

    }

    /**
     * 初始化布局，若用户未登录则不显示账号栏和退出登录栏
     */
    private void initLayout() {
        mSharedPreferences= SpUtils.getSharedPreferences(this);
        token = mSharedPreferences.getString(AUTHORIZATION,null);
        if(token == null){
            mLayoutAccount.setVisibility(View.GONE);
            mLogout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mLogout.setOnClickListener(this);
        mUserInfo.setOnClickListener(this);
        mChangePhone.setOnClickListener(this);
        mModifyPassword.setOnClickListener(this);

        //toolbar 的返回
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mLogout = (RelativeLayout) findViewById(R.id.rl_logout);
        mLayoutAccount = (LinearLayout) findViewById(R.id.id_ll_account);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.customToolbar);
        mUserInfo = (RelativeLayout) findViewById(R.id.rl_user_info);
        mChangePhone = (RelativeLayout) findViewById(R.id.rl_change_phone);
        mModifyPassword = (RelativeLayout) findViewById(R.id.rl_modify_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_logout:
                SharedPreferences.Editor editor = SpUtils.getEditor(this);
                editor.putString(AUTHORIZATION,null);
                editor.putBoolean(SHOW_DATA,false);
                editor.commit();
                //返回到上一个界面
                finish();
                break;
            case R.id.rl_user_info:
                IntentUtils.jump(SystemSettingActivity.this,UserInfoActivity.class);
                break;
            case R.id.rl_change_phone:

                break;
            case R.id.rl_modify_password:
                IntentUtils.jump(SystemSettingActivity.this,ModifyPasswordActivity.class);
                break;

        }
    }
}
