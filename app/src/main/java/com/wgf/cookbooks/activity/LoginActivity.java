package com.wgf.cookbooks.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.Md5;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.SHOW_DATA;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 用户登录
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener{
    private int requestCode = 0;//判断是哪个页面跳转过来的
    private static final String TAG = "TAG";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mTextViewRegister,mTextViewForgetPassword;
    private AutoCompleteTextView mNumberView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;//scroll view
    private CustomToolbar mCustomToolbar;
    private TextView mTvSmsLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestCode = getIntent().getIntExtra("requestCode",0);

        // Set up the login form.
        findView();
        mTextViewForgetPassword.setOnClickListener(this);
        mTextViewRegister.setOnClickListener(this);
        mTvSmsLogin.setOnClickListener(this);

        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    Log.i(TAG, "onEditorAction: "+"user login");
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mNumberSignInButton = (Button) findViewById(R.id.number_sign_in_button);
        //登录
        mNumberSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();//
            }
        });

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.tv_register:/**注册**/
               IntentUtils.jump(LoginActivity.this,RegisterActivity.class);
                break;
            case R.id.tv_forget_password:/**忘记密码**/
                IntentUtils.jump(LoginActivity.this,ModifyPasswordActivity.class);
                break;
            case R.id.tv_sms_login:/** 短信快速登录**/
                IntentUtils.jump(LoginActivity.this,SmsFastLoginActivity.class);
                break;
        }
    }



    private void findView() {
        mNumberView = (AutoCompleteTextView) findViewById(R.id.number);
        mPasswordView = (EditText) findViewById(R.id.password);
        mTextViewRegister = (TextView) findViewById(R.id.tv_register);
        mTextViewForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.toolbar_back);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mTvSmsLogin = (TextView) findViewById(R.id.tv_sms_login);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid number, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mNumberView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String number = mNumberView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        L.e("Text:"+TextUtils.isEmpty(password)+password.length());
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.text_input_password));
            focusView = mPasswordView;
            cancel = true;

        }
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password_length));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid phone number.
        if (TextUtils.isEmpty(number)) {
            mNumberView.setError(getString(R.string.text_phone_number));
            focusView = mNumberView;
            cancel = true;
        } else if (!isnumberValid(number)) {
            mNumberView.setError(getString(R.string.error_invalid_phone_number));
            focusView = mNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
           // showProgress(true);
            showLoginDialog();
            mAuthTask = new UserLoginTask(number, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * 判断手机号码是否有效
     * @param number
     * @return
     */

    private boolean isnumberValid(String number) {
        //TODO: Replace this with your own logic
        return number.length()==11;
    }

    /**
     * 判断密码是否有效 密码在6-16位
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }


    private AlertDialog dialog;
    /**
     * 登录对话框
     */
    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_login,null);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }




    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private final String mNumber;
        private final String mPassword;
        UserLoginTask(String number, String password) {
            this.mNumber = number;
            this.mPassword = password;
        }


        @Override
        protected Integer doInBackground(Void... params) {
            int flag = Constants.FAILED;
            // 尝试对网络服务的身份验证。
            //TODO 修改这个就可以
            String url = Constants.BASE_URL + "/login";
            final Map<String, String> json = new HashMap<>();
            json.put("phone", mNumber);
            json.put("password", Md5.hashMd5(mPassword));
            JSONObject jsonObject = new JSONObject(json);
            okhttp3.Response response;
            try {
                response = OkGo.<String>post(url)
                        .upJson(jsonObject)
                        .execute();
                System.out.println("resbody:"+response.body());
                String jsonStr = response.body().string();
                JSONObject jo = new JSONObject(jsonStr);
                int code = jo.getInt("code");//响应总code
                JSONObject object = jo.getJSONObject("extend");
                if (code == Constants.SUCCESS) {
                    String authorization = object.getString(AUTHORIZATION);
                    L.e("ss:" + authorization);
                    //写入配置信息
                    SharedPreferences.Editor editor = SpUtils.getEditor(LoginActivity.this);
                    editor.putString(AUTHORIZATION, authorization);//保存 token
                    editor.putBoolean(SHOW_DATA,false);
                    editor.commit();
                    flag = Constants.SUCCESS;
                    L.e("log" + flag);
                } else if (code == Constants.FAILED) {
                    flag = Constants.FAILED;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return flag;
        }
  
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mAuthTask = null;

            //showProgress(false);
            if(dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
            if (result == Constants.SUCCESS) {
                //login or register  success logic
                ToastUtils.toast(LoginActivity.this,"登录成功");
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            if(dialog!=null && dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
}

