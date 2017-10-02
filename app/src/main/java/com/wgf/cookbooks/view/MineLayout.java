package com.wgf.cookbooks.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;


/**
 * 自定义的布局
 * email：guofei_wu@163.com
 */

public class MineLayout extends LinearLayout {
    private String title;
    private TextView tv_mine_title;
    public MineLayout(Context context) {
        this(context,null);
    }

    public MineLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }


    public void setTitle(String title){
        this.title = title;
        tv_mine_title.setText(title);
    }

    public String getTitle(){
        return title;
    }
    private void initView(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.mine_layout,null);
        this.addView(view);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MineLayout);

        Drawable icon = ta.getDrawable(R.styleable.MineLayout_mineIcon);
        title = ta.getString(R.styleable.MineLayout_mineTitle);

        ImageView iv_mine = (ImageView) findViewById(R.id.iv_mine);
        iv_mine.setImageDrawable(icon);

        tv_mine_title= (TextView) findViewById(R.id.tv_mine_title);
        tv_mine_title.setText(title);
        ta.recycle();

    }

}
