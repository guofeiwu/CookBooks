package com.wgf.cookbooks.activity;

import android.content.Intent;
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

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 修改手机号码
 */
public class CheckOldPhoneActivity extends AppCompatActivity {
    private CustomToolbar mCustomToolbar;
    private AutoCompleteTextView mOldPhone;
    private Button mNextStep;
    private EditText mVerifyCode;
    private TextView mGetCode;
    private EventHandler mEventHandler;
    private CountDownTimer mCountDownTimer;
    private String oldPhone;
    private SqliteDao dao;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.SUCCESS) {
                //IntentUtils.jump(CheckOldPhoneActivity.this,ModifyPhoneActivity.class);
                Intent intent = new Intent(CheckOldPhoneActivity.this,ModifyPhoneActivity.class);
                startActivity(intent);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_old_phone);
        dao = new SqliteDao(this);
        initView();
        initInstance();
        setListener();

        oldPhone = dao.queryUserInfo();
        String p = oldPhone.substring(0,3)+"****"+oldPhone.substring(7,11);
        mOldPhone.setText(p);
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


        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.toast(CheckOldPhoneActivity.this, getString(R.string.text_sms_code));
                    return;
                }
                //提交验证码
                SMSSDK.submitVerificationCode("86",oldPhone,code);
                mNextStep.setEnabled(false);//不可见
//                mHandler.sendEmptyMessage(Constants.SUCCESS);
            }
        });
        mGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                SMSSDK.getVerificationCode("86", oldPhone);
                mGetCode.setClickable(false);
                mCountDownTimer.start();
            }
        });
    }


    /**
     * 初始化实例
     */
    private void initInstance(){
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
                                ToastUtils.toast(CheckOldPhoneActivity.this, getString(R.string.text_get_verify_code));
                            }
                        });
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
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
                                mNextStep.setEnabled(true);//确认按钮可用
                                ToastUtils.toast(CheckOldPhoneActivity.this, finalResultDesc);
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
        mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mGetCode.setText(millisUntilFinished / 1000 + "S");
                // mVerifiyCode.setEnabled(false);
                mGetCode.setClickable(false);
            }

            @Override
            public void onFinish() {
                mGetCode.setText(getString(R.string.text_get_code));
                //mVerifiyCode.setEnabled(true);
                mGetCode.setClickable(true);
            }
        };
    }


    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_alter_phone_back);
        mOldPhone = (AutoCompleteTextView) findViewById(R.id.id_user_old_phone);
        mVerifyCode = (EditText) findViewById(R.id.id_verification_code);
        mGetCode = (TextView) findViewById(R.id.id_get_verify_code);
        mNextStep = (Button) findViewById(R.id.id_btn_next_step);
    }
}
