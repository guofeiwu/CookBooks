package com.wgf.cookbooks.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wgf.cookbooks.R;


/**
 * 自定义的toolbar
 * author WuGuofei on 2017/4/24.
 * e-mail：guofei_wu@163.com
 */

public class CustomToolbar extends Toolbar {
    BtnOnBackOnClickListener mBtnOnBackOnClickListener;
    TextView tv_title;

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    public void setBtnOnBackOnClickListener(BtnOnBackOnClickListener mBtnOnBackOnClickListener) {
        this.mBtnOnBackOnClickListener = mBtnOnBackOnClickListener;
    }

    //设置toolbar标题
    public void setToolbarTitle(String title){
        tv_title.setText(title);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.toolbar_view, this);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.CustomToolbar);
        String title = ta.getString(R.styleable.CustomToolbar_toolbarTitle);
        tv_title= (TextView) view.findViewById(R.id.tv_toolbar_title);
        tv_title.setText(title);

        ta.recycle();


        Button btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnOnBackOnClickListener != null) {
                    mBtnOnBackOnClickListener.onClick();
                }
            }
        });

    }
    public interface BtnOnBackOnClickListener{
        void onClick();
    }

}
