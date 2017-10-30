package com.wgf.cookbooks.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.util.IntentUtils;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 启动界面
 */
public class SplashActivity extends AppCompatActivity{

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //休眠三秒
        mCountDownTimer = new CountDownTimer(3*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                IntentUtils.jump(SplashActivity.this,MainActivity.class);
                finish();
            }
        };
        mCountDownTimer.start();





    }

}
