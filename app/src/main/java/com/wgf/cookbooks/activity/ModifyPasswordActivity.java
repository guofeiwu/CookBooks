package com.wgf.cookbooks.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.Md5;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Response;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 修改（忘记）密码
 */
public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private CustomToolbar mCustomToolbar;
    private EventHandler mEventHandler;
    private String number;//电话号码
    private TextView mVerifiyCode;//获取验证码
    private Button mModify;//确认修改
    private AutoCompleteTextView mUserPhone;
    private CountDownTimer mCountDownTimer;
    private EditText mUserPassword;//用户密码
    private EditText mCode;//验证码
    private String password;//密码
    private ModifyPasswordAsyncTask modifyPasswordAsyncTask;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.SUCCESS) {
                if(modifyPasswordAsyncTask != null){
                    return;
                }
                modifyPasswordAsyncTask = new ModifyPasswordAsyncTask();
                modifyPasswordAsyncTask.execute();
            }
        }
    };

    //修改密码
    public class ModifyPasswordAsyncTask extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            String url = Constants.BASE_URL+"/login/modifyPassword";
            Map<String,String> map = new HashMap<>();
            map.put("phone",number);
            map.put("password", Md5.hashMd5(password));
            JSONObject json = new JSONObject(map);
            try {
                Response response = OkGo.<String>post(url)
                        .upJson(json)
                        .execute();
                String resJosn = response.body().string();
                int code = JsonUtils.getCode(resJosn);//总code
                JSONObject content = JsonUtils.getContent(resJosn);
                if(code == Constants.SUCCESS){
                    return Constants.SUCCESS;
                }else{
                    return content.getInt("code");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Constants.FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            modifyPasswordAsyncTask = null;
            mModify.setEnabled(true);//确认修改可见
            if(integer == Constants.SUCCESS){
                ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_modify_success));
                finish();
            }else if (integer == Constants.LOGIN_ERROR_CODE){
                ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_modify_failed));
            }else{
                ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_not_exist_user));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            modifyPasswordAsyncTask = null;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();

        initInstance();//初始化实例

        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mModify.setOnClickListener(this);
        mVerifiyCode.setOnClickListener(this);
    }

    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_toolbar);
        mVerifiyCode = (TextView) findViewById(R.id.get_verify_code);
        mModify = (Button) findViewById(R.id.btn_sure_modify);
        mUserPhone = (AutoCompleteTextView) findViewById(R.id.user_phone);
        mUserPassword = (EditText) findViewById(R.id.user_password);
        mCode = (EditText) findViewById(R.id.verification_code);
    }


    /**
     * 初始化EventHandler,并注册
     */
    private void initInstance() {
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
                                ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_get_verify_code));
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
                                mModify.setEnabled(true);//确认按钮可用
                                ToastUtils.toast(ModifyPasswordActivity.this, finalResultDesc);
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
                mVerifiyCode.setText(millisUntilFinished / 1000 + "S");
                // mVerifiyCode.setEnabled(false);
                mVerifiyCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                mVerifiyCode.setText(getString(R.string.text_get_code));
                //mVerifiyCode.setEnabled(true);
                mVerifiyCode.setClickable(true);
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure_modify:
                number = mUserPhone.getText().toString().trim();
                if(TextUtils.isEmpty(number)){
                    ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_phone_number));
                }else{
                    if (!number.matches(Constants.TEL_REGEX)) {
                        ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_correct_phone));
                        return;
                    }

                    //判断密码
                    password = mUserPassword.getText().toString().trim();
                    if(TextUtils.isEmpty(password)){
                        ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_input_password));
                        return;
                    }

                    if(password.length()<6 || password.length()>16){
                        ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_correct_password));
                        return;
                    }

                    //发送短信的手机号和现在的手机号不一致
                    if(!mUserPhone.getText().toString().trim().equals(number)){
                        ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_correct_phone));
                        return;
                    }

                    String code = mCode.getText().toString().trim();
                    if(TextUtils.isEmpty(code)){
                        ToastUtils.toast(ModifyPasswordActivity.this,getString(R.string.text_verify_code));
                        return;
                    }

                    SMSSDK.submitVerificationCode("86",number,code);
                    mModify.setEnabled(false);//不可见
                }
                break;
            case R.id.get_verify_code:
                //获取验证码
                number = mUserPhone.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_phone_number));
                } else {
                    if (!number.matches(Constants.TEL_REGEX)) {
                        ToastUtils.toast(ModifyPasswordActivity.this, getString(R.string.text_correct_phone));
                    } else {
                        //获取验证码
                        SMSSDK.getVerificationCode("86", number);
                        mVerifiyCode.setClickable(false);
                        mCountDownTimer.start();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventHandler != null) {
            SMSSDK.unregisterEventHandler(mEventHandler);
        }
    }
}
