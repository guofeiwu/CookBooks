package com.wgf.cookbooks.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.fragment.UserCommentMenuFragment;
import com.wgf.cookbooks.fragment.UserCommentShaiFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UserCommentMenuShaiActivity extends AppCompatActivity {
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<String> titles = Arrays.asList("菜谱评论", "晒一晒评论");

    private List<Fragment> mFragments;

    private UserCommentMenuFragment userCommentMenuFragment;
    private UserCommentShaiFragment userCommentShaiFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comment_menu_shai);
        initView();
        initData();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        TabLayout.Tab collectTab = mTabLayout.newTab().setText(titles.get(0));
        TabLayout.Tab buyTab = mTabLayout.newTab().setText(titles.get(1));

        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FAFAED04"));
        mTabLayout.addTab(collectTab);
        mTabLayout.addTab(buyTab);


        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(titles.get(0));
        mTabLayout.getTabAt(1).setText(titles.get(1));


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.id_content_viewpager);
    }


    private void initData() {
        mFragments = new ArrayList<>();
        //生成fragment
        userCommentMenuFragment = new UserCommentMenuFragment();
        mFragments.add(userCommentMenuFragment);
        userCommentShaiFragment = new UserCommentShaiFragment();
        mFragments.add(userCommentShaiFragment);


        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
    }
}
