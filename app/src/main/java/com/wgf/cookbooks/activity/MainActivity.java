package com.wgf.cookbooks.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.AppVer;
import com.wgf.cookbooks.clazz.asynctask.GetAppVerAsyncTask;
import com.wgf.cookbooks.fragment.DiscoverFragment;
import com.wgf.cookbooks.fragment.HomePageFragment;
import com.wgf.cookbooks.fragment.MenuFragment;
import com.wgf.cookbooks.fragment.MineFragment;
import com.wgf.cookbooks.util.SwitchAnimationUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.util.UpdateAppVerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 主界面
 */
public class MainActivity extends AppCompatActivity implements GetAppVerAsyncTask.IGetAppVerListener{
    private List<Fragment> mFragments;
    private BottomNavigationBar mBottomNavigationBar;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private Fragment homepage,dicover,menu,mine;
    //记录第一下按下时间
    private long time= 0;
    private int currentVersion = 0;
    private GetAppVerAsyncTask mGetAppVerAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitchAnimationUtils.enterActivitySlideRight(this);
        SwitchAnimationUtils.exitActivitySlideLeft(this);


        setContentView(R.layout.activity_main);


        checkVersion();



        mFragments = new ArrayList<>();
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.my_bottom_navigation_bar);

        if (homepage == null) {
            homepage = new HomePageFragment();
            mFragments.add(homepage);
        }
        mFragmentManager = getSupportFragmentManager();

        mTransaction = mFragmentManager.beginTransaction();//开启事务

        mTransaction.add(R.id.cl_container, mFragments.get(0));

        mTransaction.commit();//提交事务


//        Intent intent = getIntent();
//        if(intent!=null){
//            loginFlag = intent.getStringExtra("flag");
//
//            switch (loginFlag){
//                case Constants.LOGIN_FLAG_MIME:
//                    if(mFragments.get(3) != null){
//
//                }
//                    break;
//            }
//    }


        mBottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar
                .setActiveColor(R.color.color_red)
                .setInActiveColor(R.color.text_gray)
        // .setBarBackgroundColor(R.color.color_white)
        ;


        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.homepage_fill, getString(R.string.text_home)))
                .addItem(new BottomNavigationItem(R.drawable.discover_32, getString(R.string.text_discover)))
                .addItem(new BottomNavigationItem(R.drawable.category_32, getString(R.string.text_category)))
                .addItem(new BottomNavigationItem(R.drawable.mine_32, getString(R.string.text_mine)))
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int
                                              position) {
                mTransaction = mFragmentManager.beginTransaction();
                //隐藏其他的fragment
                if (mFragments.size() > 0) {
                    for (int i = 0; i < mFragments.size(); i++) {
                        //if (i != position) {
                        // L.e("hide position:"+i);
                        mTransaction.hide(mFragments.get(i));
                        //}
                    }
                }
                switch (position){
                    case 0:
                        if (homepage == null) {
                            homepage = new HomePageFragment();
                            mTransaction.add(R.id.cl_container, homepage);
                        } else {
                            if (!homepage.isVisible()) {
                                mTransaction.show(homepage);
                            }
                        }
                        break;
                    case 1:
                        if (dicover == null) {
                            dicover = new DiscoverFragment();
                            mTransaction.add(R.id.cl_container, dicover);
                            mFragments.add(dicover);
                        } else {
                            if (!dicover.isVisible()) {
                                mTransaction.show(dicover);
                            }
                        }
                        break;
                    case 2:
                        if (menu == null) {
                            menu = new MenuFragment();
                            mFragments.add(menu);
                            mTransaction.add(R.id.cl_container, menu);
                        } else {
                            if (!menu.isVisible()) {
                                mTransaction.show(menu);
                            }
                        }
                        break;
                    case 3:
                        if (mine == null) {
                            mine = new MineFragment();
                            mFragments.add(mine);
                            mTransaction.add(R.id.cl_container, mine);
                        } else {
                            if (!mine.isVisible()) {
                                mTransaction.show(mine);
                            }
                        }
                        break;
                }
                mTransaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - time<2000){
            //System.exit(0);
            finish();
        }else{
            ToastUtils.toast(this,"在按一次退出程序");
            time = System.currentTimeMillis();
        }
    }

    /**
     * 检查版本信息
     */
    private void checkVersion() {
        //判断是否要进行更新、设置版本号
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(),0);
            currentVersion = pi.versionCode;
            //mVersion.setText("版本 "+pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(mGetAppVerAsyncTask!=null){
            return;
        }
        mGetAppVerAsyncTask = new GetAppVerAsyncTask();
        mGetAppVerAsyncTask.setmListener(this);
        mGetAppVerAsyncTask.execute();
    }

    @Override
    public void appVer(AppVer appVer) {
        if(appVer!=null) {
            UpdateAppVerUtils.updateApp(this, appVer,false);
        }
    }
}
