package com.wgf.cookbooks.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.clazz.CommitFeedbackAsynctask;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class FeedbackActivity extends AppCompatActivity implements CommitFeedbackAsynctask.ICommitFeedbackListener{
    private CustomToolbar mCustomToolbar;
    private TextView mCommit;
    private EditText mFeedbackContent;
    private TextView mContentLen;
    private CommitFeedbackAsynctask mCommitFeedbackAsynctask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();

        setListener();


    }

    /**
     * 设置监听
     */
    private void setListener() {

        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                confirmDialog();
            }
        });

        mFeedbackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                mContentLen.setText(len+"/20000");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mFeedbackContent.getText().toString();
                if(TextUtils.isEmpty(content)){
                    ToastUtils.toast(FeedbackActivity.this,getString(R.string.text_please_feedback_content));
                }else{
                        if(mCommitFeedbackAsynctask!=null){
                            ToastUtils.toast(FeedbackActivity.this,getString(R.string.text_committing));
                            return;
                        }
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("feedback",content);
                    JSONObject jsonObject = new JSONObject(map);

                    mCommitFeedbackAsynctask = new CommitFeedbackAsynctask(FeedbackActivity.this);
                    mCommitFeedbackAsynctask.setmListener(FeedbackActivity.this);
                    mCommitFeedbackAsynctask.execute(jsonObject.toString());
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_feedback_back);
        mCommit = (TextView) findViewById(R.id.id_tv_content_commit);
        mFeedbackContent = (EditText) findViewById(R.id.id_et_feedback_content);
        mContentLen = (TextView) findViewById(R.id.id_tv_feedback_number);
    }


    @Override
    public void onBackPressed() {
        confirmDialog();
        //super.onBackPressed();
    }

    private Dialog dialog;
    /**
     * 退出发布意见反馈提示框
     */
    private void confirmDialog(){
        String content = mFeedbackContent.getText().toString();
        if(!TextUtils.isEmpty(content)){
            //弹出是否要退出的对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage(R.string.text_confirm_msg);

            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
        }else{
            finish();
        }

    }


    //提交反馈的监听
    @Override
    public void success(int code) {
        if(code == SUCCESS){
            ToastUtils.toast(this,getString(R.string.text_commit_success));
            finish();
        }else{
            ToastUtils.toast(this,getString(R.string.text_commit_failed));
        }
    }

    @Override
    protected void onDestroy() {
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }
}
