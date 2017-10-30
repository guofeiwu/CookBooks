package com.wgf.cookbooks.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.AppVer;
import com.wgf.cookbooks.clazz.asynctask.GetAppVerAsyncTask;
import com.wgf.cookbooks.util.GlideCacheUtils;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.SwitchAnimationUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.util.UpdateAppVerUtils;
import com.wgf.cookbooks.util.WxUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.DOWNLOADING;
import static com.wgf.cookbooks.util.Constants.SHOW_DATA;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 系统相关的设置
 */
public class SystemSettingActivity  extends AppCompatActivity implements View.OnClickListener,GetAppVerAsyncTask.IGetAppVerListener{
    private RelativeLayout mLogout,mUserInfo,mChangePhone,mModifyPassword,mShareApp,
            mFeedback,mCleanCache,mAboutUs,mAppUpdate;
    private LinearLayout mLayoutAccount;
    private SharedPreferences mSharedPreferences;
    private String token;
    private CustomToolbar mCustomToolbar;
    private IWXAPI api;
    private PopupWindow pw;
    private TextView mCacheSize;
    private GetAppVerAsyncTask mGetAppVerAsyncTask;
    private int currentVersion = 0;
    private View mPoint;
    private boolean click = false;//表示 点击更新
    private TextView mVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitchAnimationUtils.enterActivitySlideRight(this);
        SwitchAnimationUtils.exitActivitySlideLeft(this);
        api = WxUtils.register(this);
        setContentView(R.layout.activity_system_setting);

        initView();

        initListener();

        initData();

    }

    /**
     * 初始化布局和数据，若用户未登录则不显示账号栏和退出登录栏
     */
    private void initData() {
        mSharedPreferences= SpUtils.getSharedPreferences(this);
        token = mSharedPreferences.getString(AUTHORIZATION,null);
        if(token == null){
            mLayoutAccount.setVisibility(View.GONE);
            mLogout.setVisibility(View.GONE);
        }

        //设置缓存大小
        String cacheSize = GlideCacheUtils.getCacheSize(this);
        mCacheSize.setText(cacheSize);

        //判断是否要进行更新、设置版本号
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(),0);
            currentVersion = pi.versionCode;
            mVersion.setText("版本 "+pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(mGetAppVerAsyncTask!=null){
            return;
        }
        mGetAppVerAsyncTask = new GetAppVerAsyncTask();
        mGetAppVerAsyncTask.setmListener(this);
        mGetAppVerAsyncTask.execute();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mLogout.setOnClickListener(this);
        mUserInfo.setOnClickListener(this);
        mChangePhone.setOnClickListener(this);
        mModifyPassword.setOnClickListener(this);
        mShareApp.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mCleanCache.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);
        mAppUpdate.setOnClickListener(this);

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
        mShareApp = (RelativeLayout) findViewById(R.id.id_rl_share);
        mFeedback = (RelativeLayout) findViewById(R.id.id_rl_feedback);
        mCleanCache = (RelativeLayout) findViewById(R.id.id_rl_clean_cache);
        mCacheSize = (TextView) findViewById(R.id.id_tv_cache_size);
        mAboutUs = (RelativeLayout) findViewById(R.id.id_rl_about);
        mAppUpdate = (RelativeLayout) findViewById(R.id.id_rl_app_update);
        mPoint = findViewById(R.id.id_v_point);
        mVersion = (TextView) findViewById(R.id.id_tv_version_code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_logout:
                confirmDialog();
                break;
            case R.id.rl_user_info:
                IntentUtils.jump(SystemSettingActivity.this,UserInfoActivity.class);
                break;
            case R.id.rl_change_phone:
                IntentUtils.jump(SystemSettingActivity.this,CheckOldPhoneActivity.class);
                break;
            case R.id.rl_modify_password:
                IntentUtils.jump(SystemSettingActivity.this,ModifyPasswordActivity.class);
                break;
            //分享app
            case R.id.id_rl_share:
                wxShare();
                break;
            //意见反馈
            case R.id.id_rl_feedback:
                IntentUtils.jump(this,FeedbackActivity.class);
                break;
            //清除缓存
            case R.id.id_rl_clean_cache:
                mCacheSize.setText("正在清除...");
                GlideCacheUtils.clearImageAllCache(this);
                mCacheSize.setText("0.0KB");
                break;
            //关于我们
            case R.id.id_rl_about:
                IntentUtils.jump(this,AboutActivity.class);
                break;
            case R.id.id_rl_app_update:
                needUpdateVer();
                break;

        }
    }

    //weChat share
    private void wxShare() {
        View view = View.inflate(this, R.layout.share_layout, null);

        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);

        //显示popupwindow
        if (pw != null && !pw.isShowing()) {
            //L.e("showing");
            pw.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
        }
        LinearLayout layoutCancel = (LinearLayout) view.findViewById(R.id.id_ll_share_cancel);
        ImageView weChat = (ImageView) view.findViewById(R.id.id_iv_we_chat);
        ImageView moments = (ImageView) view.findViewById(R.id.id_iv_moments);
        //设置监听事件
        ShareClickListener listener = new ShareClickListener();
        layoutCancel.setOnClickListener(listener);
        weChat.setOnClickListener(listener);
        moments.setOnClickListener(listener);
    }

    class ShareClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final SendMessageToWX.Req req;
            switch (v.getId()) {
                case R.id.id_ll_share_cancel:
                    //显示popupwindow
                    if (pw != null && pw.isShowing()) {
                        pw.dismiss();
                    }
                    break;
                case R.id.id_iv_we_chat:
                    req = WxUtils.shareApp(SystemSettingActivity.this, false);
                    api.sendReq(req);
                    dismiss();
                    break;
                case R.id.id_iv_moments:
                    req = WxUtils.shareApp(SystemSettingActivity.this,true);
                    api.sendReq(req);
                    dismiss();
                    break;
            }
        }
    }


    //关闭popupwindow
    private void dismiss() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
    }



    private Dialog dialog;

    /**
     * 退出发布意见反馈提示框
     */
    private void confirmDialog() {
        //弹出是否要退出的对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettingActivity.this);
        builder.setTitle("提示");
        builder.setMessage(R.string.text_confirm_logout);

        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = SpUtils.getEditor(SystemSettingActivity.this);
                editor.putString(AUTHORIZATION,null);
                editor.putBoolean(SHOW_DATA,false);
                editor.commit();
                //返回到上一个界面
                dialog.dismiss();
                finish();
            }
        });

        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 判断是否需要更新
     */
    private void needUpdateVer(){
        if(DOWNLOADING){
            ToastUtils.toast(this,"正在下载中,请稍后 ...");
            return;
        }
        if(mGetAppVerAsyncTask!=null){
            return;
        }
        mGetAppVerAsyncTask = new GetAppVerAsyncTask();
        mGetAppVerAsyncTask.setmListener(this);
        mGetAppVerAsyncTask.execute();
        click = true;

    }

    @Override
    public void appVer(AppVer appVer) {
        if(mGetAppVerAsyncTask!=null){
            mGetAppVerAsyncTask = null;
        }
        //回调成功
        if(appVer!=null){
            //设置红点
            if(appVer.getVer()>currentVersion){
                mPoint.setVisibility(View.VISIBLE);
            }else{
                mPoint.setVisibility(View.GONE);
            }
            if(click){
                UpdateAppVerUtils.updateApp(this,appVer,true);
            }
        }else{
            ToastUtils.toast(this,"检查更新失败");
        }
    }
}
