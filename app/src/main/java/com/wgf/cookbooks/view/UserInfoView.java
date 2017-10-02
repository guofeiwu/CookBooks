package com.wgf.cookbooks.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 用户信息自定义布局
 */
public class UserInfoView extends LinearLayout {
    private String title,content;
    private TextView mTitle,mContent;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
        mContent.setText(content);
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public UserInfoView(Context context) {
        this(context,null);
    }

    public UserInfoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UserInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_info_layout,null);
        this.addView(view);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UserInfoView);

        title = ta.getString(R.styleable.UserInfoView_userInfoViewTitle);
        mTitle = (TextView) findViewById(R.id.title_name);
        mTitle.setText(title);

        content = ta.getString(R.styleable.UserInfoView_userInfoViewContent);
        mContent = (TextView) findViewById(R.id.content_name);
        mContent.setText(content);

        ta.recycle();

    }


}
