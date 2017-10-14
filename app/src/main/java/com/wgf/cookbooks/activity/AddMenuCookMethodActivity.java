package com.wgf.cookbooks.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.view.CustomToolbar;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class AddMenuCookMethodActivity extends AppCompatActivity {
    private CustomToolbar mCustomToolbar;
    private TextView mReleaseMenu;
    private TextView mStepNumber;

    private EditText mStepDesc;
    private TextView mStepDescNumber;
    private LinearLayout mStepLayout;
    private LayoutInflater mInflater;
    private LinearLayout mAdddStep;
    private int position = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_cook_method);
        initView();
        mInflater = LayoutInflater.from(this);
        addStepItem();//显示第一个

        setListener();

    }

    /**
     * 设置监听
     */
    private void setListener() {
        mAdddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStepItem();
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_back);
        mReleaseMenu = (TextView) findViewById(R.id.id_tv_release);
        mStepNumber = (TextView) findViewById(R.id.id_tv_step_number);
        mStepDesc = (EditText) findViewById(R.id.id_et_step_desc);
        mStepDescNumber = (TextView) findViewById(R.id.id_tv_step_desc);
        mStepLayout = (LinearLayout) findViewById(R.id.id_ll_step_layout);
        mAdddStep = (LinearLayout) findViewById(R.id.id_ll_add_step);
    }

    /**
     * 添加step的步骤view
     */
    private void addStepItem(){
        final View view  = mInflater.inflate(R.layout.add_menu_step_item,null,false);
        view.setTag(position);
        TextView StepNumber = (TextView) view.findViewById(R.id.id_tv_step);
        StepNumber.setText(""+position);

        initDelete();

        view.findViewById(R.id.id_iv_step_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStepLayout.getChildCount()>1){
                    mStepLayout.removeView(view);
                    //重新设置步骤几
                    initStep();
                }
            }
        });
        mStepLayout.addView(view);
        view.requestFocus();//步骤框获得焦点
        position++;
        initStep();
    }


    /**
     * 初始化步骤
     */
    private void initStep(){
        int childCount = mStepLayout.getChildCount();
        for(int i= 0 ;i<childCount;i++){
            View view = mStepLayout.getChildAt(i);
            TextView StepNumber = (TextView) view.findViewById(R.id.id_tv_step);
            StepNumber.setText(""+(i+1));
        }
        mStepNumber.setText("全部步骤("+childCount+"）");
        initDelete();
    }

    /**
     * 初始化删除的位置
     */
    private void initDelete(){
        int childCount = mStepLayout.getChildCount();
        for (int i= 0;i<childCount;i++){
            View view = mStepLayout.getChildAt(i);
            ImageView stepDelete = (ImageView) view.findViewById(R.id.id_iv_step_delete);
            if(childCount == 1){
                stepDelete.setVisibility(View.GONE);
            }else{
                stepDelete.setVisibility(View.VISIBLE);
            }
        }
    }

}
