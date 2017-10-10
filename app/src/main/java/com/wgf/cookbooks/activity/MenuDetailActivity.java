package com.wgf.cookbooks.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.MenuStepAdapter;
import com.wgf.cookbooks.bean.Materials;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.MenuDetailAsyncTask;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MenuDetailActivity extends AppCompatActivity implements MenuDetailAsyncTask.IMenuDetailListener{

    private LinearLayout mMaterialsDose;//食材及用量
    private MenuDetailAsyncTask mMenuDetailAsyncTask;
    private CustomToolbar mCustomToolbar;
    private ImageView mMainIcon;
    private CircleImageView muserIcon;
    private TextView mUserName;
    private TextView mIntroduce;

    private RecyclerView mRecyclerView;
    private MenuStepAdapter mMenuStepAdapter;
    private RecycleDivider mRecycleDivider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        initView();


        initData();

        setListener();


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
    }

    private int menuPkId = 0;
    /**
     * 初始化数据
     */
    private void initData() {
        menuPkId = getIntent().getIntExtra("menuPkId",0);

        if (mMenuDetailAsyncTask != null) {
            return;
        }
        mMenuDetailAsyncTask = new MenuDetailAsyncTask(this);
        mMenuDetailAsyncTask.setmListener(this);
        mMenuDetailAsyncTask.execute(menuPkId);
    }

    /**
     * 绑定控件
     */
    private void initView() {

        mMaterialsDose = (LinearLayout) findViewById(R.id.id_ll_materials_dose);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.ct_back);
        mMainIcon = (ImageView) findViewById(R.id.id_iv_main_icon);
        muserIcon = (CircleImageView) findViewById(R.id.id_civ_icon_item);
        mUserName = (TextView) findViewById(R.id.id_tv_user_name_item);
        mIntroduce = (TextView) findViewById(R.id.id_tv_menu_introduce);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_step);

    }





    @Override
    public void result(Menu menu) {
        if(menu==null){
            ToastUtils.toast(this,"系统或网络出错");
            return;
        }

        String mainIcon= menu.getMainIcon();
        Glide.with(this).load(BASE_URL_FILE_MENUS+mainIcon).into(mMainIcon);
        String userIconUrl = menu.getUserIconUrl();
        Glide.with(this).load(BASE_URL_FILE_ICON+userIconUrl).into(muserIcon);
        mUserName.setText(menu.getUserName());
        mIntroduce.setText(menu.getIntroduce());

        //食材
        List<Materials> materialses =  menu.getMaterials();
        for(Materials materials : materialses){
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 15;
            layoutParams.rightMargin = 15;
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(layoutParams);

            //textView
            TextView dose = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 75);
            dose.setGravity(Gravity.CENTER);
            dose.setLayoutParams(textParams);
            dose.setText(materials.getMaterialsName());

            //view
            View view = new View(this);
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
            viewParams.topMargin = 10;
            viewParams.bottomMargin=10;
            view.setBackgroundColor(getResources().getColor(R.color.item_gray));
            view.setLayoutParams(viewParams);
            layout.addView(dose);
            layout.addView(view);

            mMaterialsDose.addView(layout);
        }

        mRecycleDivider = new RecycleDivider(this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //步骤
        if(mMenuStepAdapter == null){
            mMenuStepAdapter = new MenuStepAdapter(this,menu.getSteps());
            mRecyclerView.setAdapter(mMenuStepAdapter);
        }else{
            mRecyclerView.setAdapter(mMenuStepAdapter);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMenuStepAdapter!=null){
            mMenuStepAdapter = null;
        }

        if (mMenuDetailAsyncTask != null) {
            mMenuDetailAsyncTask = null;
        }
    }
}
