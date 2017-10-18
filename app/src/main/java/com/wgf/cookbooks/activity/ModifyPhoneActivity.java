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
import com.wgf.cookbooks.clazz.asynctask.ModifyPhoneAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.Constants;
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
public class ModifyPhoneActivity extends AppCompatActivity implements ModifyPhoneAsyncTask.IModifyPhoneListener{
    private CustomToolbar mCustomToolbar;
    private AutoCompleteTextView mNewPhone;
    private Button mCommitModify;
    private EditText mVerifyCode;
    private TextView mGetCode;
    private EventHandler mEventHandler;
    private CountDownTimer mCountDownTimer;
    private String newPhone;
    private ModifyPhoneAsyncTask mModifyPhoneAsyncTask;
    private SqliteDao dao;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.SUCCESS) {
                if(mModifyPhoneAsyncTask !=null){
                    return;
                }
                mModifyPhoneAsyncTask = new ModifyPhoneAsyncTask(ModifyPhoneActivity.this);
                mModifyPhoneAsyncTask.setmListener(ModifyPhoneActivity.this);
                mModifyPhoneAsyncTask.execute(newPhone);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        initView();
        initInstance();
        setListener();
        dao = new SqliteDao(this);
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


        mCommitModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentNewPhone = mNewPhone.getText().toString().trim();
                if (TextUtils.isEmpty(currentNewPhone)) {
                    ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_need_new_phone));
                    return;
                }

                if(!currentNewPhone.matches(Constants.TEL_REGEX)){
                    ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_correct_phone));
                    return;
                }

                if(!TextUtils.isEmpty(newPhone) && !currentNewPhone.equals(newPhone)){
                    ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_phone_not_same));
                    return;
                }
                //设置
                newPhone = currentNewPhone;
                String code = mVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_sms_code));
                    return;
                }

                //提交验证码
                SMSSDK.submitVerificationCode("86",currentNewPhone,code);
                mCommitModify.setEnabled(false);//不可见
//                mHandler.sendEmptyMessage(Constants.SUCCESS);
            }
        });
        mGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                newPhone = mNewPhone.getText().toString().trim();
                if (TextUtils.isEmpty(newPhone)) {
                    ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_phone_number));
                } else {
                    if (!newPhone.matches(Constants.TEL_REGEX)) {
                        ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_correct_phone));
                    } else {
                        //获取验证码
                        SMSSDK.getVerificationCode("86", newPhone);
                        mGetCode.setClickable(false);
                        mCountDownTimer.start();
                    }
                }
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
                                ToastUtils.toast(ModifyPhoneActivity.this, getString(R.string.text_get_verify_code));
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
                                mCommitModify.setEnabled(true);//确认按钮可用
                                ToastUtils.toast(ModifyPhoneActivity.this, finalResultDesc);
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
        mNewPhone = (AutoCompleteTextView) findViewById(R.id.id_user_new_phone);
        mVerifyCode = (EditText) findViewById(R.id.id_verification_code);
        mGetCode = (TextView) findViewById(R.id.id_get_verify_code);
        mCommitModify = (Button) findViewById(R.id.id_btn_sure_modify);
    }

    //修改返回的回调
    @Override
    public void result(int code) {
        mCommitModify.setEnabled(true);
        if(mModifyPhoneAsyncTask!=null){
            mModifyPhoneAsyncTask = null;
        }
        if(code == 0){
            ToastUtils.toast(this,"该手机号已绑定其他账号");
        }else if(code == 1){
            ToastUtils.toast(this,"不能重复绑定旧手机号");
        }else if(code == 2){
            ToastUtils.toast(this,"更改成功，下次登录请记得用新手机号");
            dao.deleteUserInfo();
            dao.insertUserInfo(null,newPhone);
            Intent intent = new Intent(ModifyPhoneActivity.this,SystemSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            ToastUtils.toast(this,"修改失败");
        }
    }
}
