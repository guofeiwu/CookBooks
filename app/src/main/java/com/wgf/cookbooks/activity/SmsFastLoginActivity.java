package com.wgf.cookbooks.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Response;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 短信快捷登录
 */
public class SmsFastLoginActivity extends AppCompatActivity implements View.OnClickListener{
    private AutoCompleteTextView mUserPhone;
    private EditText mVerifyCode;
    private TextView mTvVerifyCode;
    private Button mBtnLogin;
    private EventHandler mEventHandler;
    private String userPhone;
    private SmsLoginAsyncTask mSmsLoginAsyncTask;
    private CountDownTimer mCountDownTimer;
    private String lastPhone;
    private CustomToolbar mCustomToolbar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.SUCCESS) {
                // TODO: 2017/9/28 调用短信快捷登录接口
                if(mSmsLoginAsyncTask != null){
                    return;
                }
                mSmsLoginAsyncTask = new SmsLoginAsyncTask();
                mSmsLoginAsyncTask.execute();
            }
        }
    };

    //短信快捷登录
    public class SmsLoginAsyncTask extends AsyncTask<Void,Void,Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            String url = Constants.BASE_URL+"/login/"+lastPhone;
            try {
                Response response = OkGo.<String>post(url)
                        .execute();
                String resJson = response.body().string();

                int code = JsonUtils.getCode(resJson);
                JSONObject object = JsonUtils.getContent(resJson);
                if(code == Constants.SUCCESS){
                    //成功，获取token
                    String token = object.getString("Authorization");
                    SpUtils.getEditor(SmsFastLoginActivity.this).putString("Authorization",token).commit();
                    return Constants.SUCCESS;
                }else{
                    return object.getInt("code");//返回错误代码
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return Constants.FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mSmsLoginAsyncTask = null;
            mBtnLogin.setEnabled(true);//登录按钮可见
            if(integer == Constants.SUCCESS){
                ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_login_success));
                //Intent intent = new Intent(SmsFastLoginActivity.this,MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
                if(menuDetail!=null && menuDetail.equals("menuDetail")){
                    SpUtils.getEditor(SmsFastLoginActivity.this).putBoolean("menuDetail",true).commit();
                }
                finish();
                SoftInputUtils.hideSoftInput(SmsFastLoginActivity.this);
            }else if (integer == Constants.LOGIN_ERROR_CODE){
                ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_phone_not_exist));
            }else{
                ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_login_failed));
            }
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSmsLoginAsyncTask = null;
        }
    }


    @Override
    public void onBackPressed() {
        IntentUtils.jump(SmsFastLoginActivity.this,LoginActivity.class);
        finish();
        super.onBackPressed();
    }

    private String menuDetail = null;//从菜谱详情界面跳转到登录界面的标志
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_login);
        menuDetail = getIntent().getStringExtra("menuDetail");
        initView();

        mBtnLogin.setOnClickListener(this);
        mTvVerifyCode.setOnClickListener(this);

        mEventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    L.e("RESULT_COMPLETE");
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(SmsFastLoginActivity.this, getString(R.string.text_get_verify_code));
                            }
                        });
                    }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        mHandler.sendEmptyMessage(Constants.SUCCESS);
                    }
                } else {

                    String resultDesc = null;
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String desc = object.optString("detail");
                        int status = object.optInt("status");
                        L.e("status:" + status + ",desc" + desc);
                        if (status == 462) {
                            resultDesc = "每分钟发送次数超限";
                        } else if (status == 463) {
                            resultDesc = "手机号码每天发送次数超限";
                        } else if (status == 464) {
                            resultDesc = "每台手机每天发送次数超限";
                        } else if (status == 468) {
                            resultDesc = "验证码错误";
                        }
                        final String finalResultDesc = resultDesc;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBtnLogin.setEnabled(true);//登录按钮可见
                                ToastUtils.toast(SmsFastLoginActivity.this, finalResultDesc);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(mEventHandler); //注册短信回调


        //倒计时，模拟时间6s
        mCountDownTimer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvVerifyCode.setText(millisUntilFinished / 1000 + "S后获取");
                // mVerifiyCode.setEnabled(false);
                mTvVerifyCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                mTvVerifyCode.setText(getString(R.string.text_get_code));
                //mVerifiyCode.setEnabled(true);
                mTvVerifyCode.setClickable(true);
            }
        };




        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void initView() {
        mUserPhone = (AutoCompleteTextView) findViewById(R.id.user_phone);
        mVerifyCode = (EditText) findViewById(R.id.verification_code);
        mTvVerifyCode = (TextView) findViewById(R.id.get_verify_code);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_toolbar);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_verify_code:
                userPhone = mUserPhone.getText().toString().trim();
                if (judgePhoneNumber(userPhone)) return;
                SMSSDK.getVerificationCode("86",userPhone);
                mCountDownTimer.start();
                mTvVerifyCode.setClickable(false);
                break;
            case R.id.btn_login:
                lastPhone = mUserPhone.getText().toString().trim();
                if (judgePhoneNumber(lastPhone)) return;
                if(!lastPhone.equals(userPhone)&&!TextUtils.isEmpty(userPhone)){
                    ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_correct_phone));
                    return;
                }
                String code = mVerifyCode.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_sms_code));
                    return;
                }
                // TODO: 2017/9/28 发短信的逻辑没问题
                SMSSDK.submitVerificationCode("86",lastPhone,code);
                mBtnLogin.setEnabled(false);//不可用
                //mHandler.sendEmptyMessage(Constants.SUCCESS);
                break;
        }
    }

    private boolean judgePhoneNumber(String userPhone) {
        if(TextUtils.isEmpty(userPhone)){
            ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_phone_number));
            return true;
        }

        if(!userPhone.matches(Constants.TEL_REGEX)){
            ToastUtils.toast(SmsFastLoginActivity.this,getString(R.string.text_correct_phone));
            return true;
        }
        return false;
    }
}
