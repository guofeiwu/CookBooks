package com.wgf.cookbooks.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.NOT_SIGN;
import static com.wgf.cookbooks.util.Constants.SIGN;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MyPointActivity extends AppCompatActivity implements View.OnClickListener{
    private CustomToolbar mCustomToolbar;
    private TextView mCurrentPoint;
    private RelativeLayout mRelativeLayoutSign,mRelativeLayoutMenu;
    private TextView mSign;
    private TextView mReleaseMenu;
    private int userPoint = 0;
    private JudgeUserSignAsyncTask mJudgeUserSignAsyncTask;
    private String sign;
    private JudgeUserReleaseMenu mJudgeUserReleaseMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_point);
        //获取用的积分，默认为0
        userPoint = getIntent().getIntExtra("userPoint",0);
        initView();
        setListener();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mCurrentPoint.setText(getString(R.string.text_current_point)+userPoint);
        if(mJudgeUserSignAsyncTask!=null){
            return;
        }
        mJudgeUserSignAsyncTask = new JudgeUserSignAsyncTask();
        mJudgeUserSignAsyncTask.execute(1);



        if(mJudgeUserReleaseMenu!=null){
            return;
        }

        mJudgeUserReleaseMenu = new JudgeUserReleaseMenu();
        mJudgeUserReleaseMenu.execute();
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
        mRelativeLayoutSign.setOnClickListener(this);
        mRelativeLayoutMenu.setOnClickListener(this);
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_my_point_back);
        mCurrentPoint = (TextView) findViewById(R.id.id_tv_current_point);
        mRelativeLayoutSign = (RelativeLayout) findViewById(R.id.id_rl_sign);
        mSign = (TextView) findViewById(R.id.id_tv_sign);
        mRelativeLayoutMenu = (RelativeLayout) findViewById(R.id.id_rl_release_menu);
        mReleaseMenu = (TextView) findViewById(R.id.id_tv_release_menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_rl_sign:
                String sign = mSign.getText().toString().trim();
                if(sign.equals(SIGN)){
                    ToastUtils.toast(this,"今日签到任务已经完成，请明日再来");
                    return;
                }
                if(mJudgeUserSignAsyncTask!=null){
                    return;
                }
                mJudgeUserSignAsyncTask = new JudgeUserSignAsyncTask();
                mJudgeUserSignAsyncTask.execute(2);
                break;
            case R.id.id_rl_release_menu:
                String releaseMenu = mReleaseMenu.getText().toString().trim();
                if(releaseMenu.equals(getString(R.string.text_release_no_complete))){
                    IntentUtils.jump(this,AddMenuActivity.class);
                }else{
                    ToastUtils.toast(this,"今日发布菜谱任务已经完成，请明日再来");
                }
                break;
        }
    }

    /**
     * 判断用户是否签到和进行签到
     */
    public class JudgeUserSignAsyncTask extends AsyncTask<Integer,Void,Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int sign = params[0];
            String url = BASE_URL+"/app/user/sign/"+sign;
            String token = SpUtils.getSharedPreferences(MyPointActivity.this).getString(AUTHORIZATION,null);
            try {
                Response response = OkGo.<String>get(url)
                        .headers(AUTHORIZATION, token)
                        .execute();
                String resJson = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(resJson);
                    sign = jsonObject.getJSONObject("extend").getJSONObject("content").getInt("sign");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return sign;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mJudgeUserSignAsyncTask = null;
            //1 已经签到
            if(integer == 1){
                mSign.setVisibility(View.VISIBLE);
                sign = SIGN;
                mSign.setText(sign);
                //2 表示还未签到
            }else if(integer == 2){
                mSign.setVisibility(View.VISIBLE);
                sign = NOT_SIGN;
                mSign.setText(sign);
                //3 表示签到成功
            }else if (integer == 3){
                mSign.setVisibility(View.VISIBLE);
                sign = SIGN;
                mSign.setText(sign);
                mCurrentPoint.setText(getString(R.string.text_current_point)+(++userPoint));
                ToastUtils.toast(MyPointActivity.this,getString(R.string.text_mine_sign_success));
                //4 表示签到失败
            }else if(integer == 4){
                mSign.setVisibility(View.VISIBLE);
                sign = NOT_SIGN;
                mSign.setText(sign);
                ToastUtils.toast(MyPointActivity.this,getString(R.string.text_mine_sign_failed));
            }else{
                //其他的就是系统异常
                ToastUtils.toast(MyPointActivity.this,getString(R.string.text_failed_msg));
            }
        }
    }



    private class JudgeUserReleaseMenu extends AsyncTask<Void,Void,Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            String url = BASE_URL+"/app/menu/release";

            try {
                Response response = OkGo.<String>get(url)
                        .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(MyPointActivity.this))
                        .execute();
                String resJson = response.body().string();
                int code = JsonUtils.getCode(resJson);
                return code;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            mJudgeUserReleaseMenu = null;
            if(code == FAILED){
                mReleaseMenu.setText(getString(R.string.text_release_no_complete));
            }else{
                mReleaseMenu.setText(getString(R.string.text_already_complete));
            }

        }
    }

}
