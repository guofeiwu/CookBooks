package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.AddMenuActivity;
import com.wgf.cookbooks.activity.LoginActivity;
import com.wgf.cookbooks.activity.MenuDetailActivity;
import com.wgf.cookbooks.activity.SearchActivity;
import com.wgf.cookbooks.adapter.MenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.GetBannerAsyncTask;
import com.wgf.cookbooks.clazz.GlideImageLoader;
import com.wgf.cookbooks.clazz.MenuAsyncTask;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class HomePageFragment extends Fragment implements MenuAsyncTask.IGetMenuListener,MenuRecycleViewAdapter.IMenuDetailListener,View.OnClickListener,GetBannerAsyncTask.IGetBannerListener{
    private NestedScrollView mNestedScrollView;
    private MenuAsyncTask mMenuAsyncTask;
    private Banner banner;
    private RecyclerView mRecyclerView;
    private boolean isLoading = true;
    private MenuRecycleViewAdapter mAdapter;
    private int pageNo = 1;
    private boolean havaData = true;
    private Map map;
    private List<Menu> menus;
    private RecycleDivider mRecycleDivider;
    private EditText mEditTextSearch;
    private GetBannerAsyncTask mGetBannerAsyncTask;
    private ImageView mImageViewAddMenu;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        initView(view);

        initBanner();

        initMenuData();

        setListener();

        return view;
    }

    /**
     * 设置监听
     */
    private void setListener() {
        //添加滑动事件
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    L.e("Scroll DOWN");
//                }
//                if (scrollY < oldScrollY) {
//                    L.e("Scroll UP");
//                }
//
//                if (scrollY == 0) {
//                    L.e("TOP SCROLL");
//                }
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && isLoading) {
                    L.e("BOTTOM SCROLL");
                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if(mMenuAsyncTask!=null){
                        return;
                    }
                    mMenuAsyncTask = new MenuAsyncTask(getActivity());
                    mMenuAsyncTask.setmListener(HomePageFragment.this);
                    map.put("pageNo",++pageNo);
                    mMenuAsyncTask.execute(map);
                }else if(scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && !isLoading && havaData){
                    ToastUtils.toast(getActivity(), "加载菜谱中...");
                }
            }
        });

        mEditTextSearch.setOnClickListener(this);
        mImageViewAddMenu.setOnClickListener(this);
    }

    /**
     * 绑定控件
     * @param view
     */
    private void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_rv_menu_list);
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.id_nsv_parent);
        mEditTextSearch = (EditText) view.findViewById(R.id.id_et_search);
        mImageViewAddMenu = (ImageView) view.findViewById(R.id.iv_add_menu);
    }


    /**
     * 初始化banner数据
     */
    private void initBanner(){
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);

        if (mGetBannerAsyncTask!=null){
            return;
        }

        mGetBannerAsyncTask = new GetBannerAsyncTask();
        mGetBannerAsyncTask.setmListener(this);
        mGetBannerAsyncTask.execute();
    }

    /**
     * 初始化菜谱数据
     */
    private void initMenuData(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleDivider = new RecycleDivider(getActivity(),RecycleDivider.VERITCAL_LIST);
        if(menus!=null){
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();
        map = new HashMap();
        map.put("pageNo",1);
        mMenuAsyncTask = new MenuAsyncTask(getActivity());
        mMenuAsyncTask.setmListener(this);
        mMenuAsyncTask.execute(map);
    }


    @Override
    public void result(List<Menu> list) {
        if(list!=null&&list.size()>0){
            menus.addAll(list);
            if(mAdapter == null){
                mAdapter = new MenuRecycleViewAdapter(getActivity(),list);
                //设置点击事件的回调监听
                mAdapter.setmListener(this);
                //mRecyclerView.addItemDecoration(mRecycleDivider);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        }else{
            havaData = false;
            if (mAdapter!=null){
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(getActivity(),"菜谱已全部加载完成...");
        }
        if(mMenuAsyncTask!=null){
            mMenuAsyncTask = null;
        }
    }

    @Override
    public void detail(int position) {
        //跳转界面，显示详情
        Intent intent = new Intent(getActivity(),MenuDetailActivity.class);
        intent.putExtra("menuPkId",menus.get(position).getMenuPkId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_et_search://搜索
                IntentUtils.jump(getActivity(), SearchActivity.class);
                break;
            case R.id.iv_add_menu:
                //添加菜谱
                String token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(getActivity(),AddMenuActivity.class);
                }
                break;
        }
    }

    //获取banner数据的回调
    @Override
    public void getBanner(List<com.wgf.cookbooks.bean.Banner> banners) {
        if(banners!=null){
            List<String> images = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            final List<Integer> menuPkIds = new ArrayList<>();
            for (com.wgf.cookbooks.bean.Banner banner1:banners){
                images.add(BASE_URL_FILE_MENUS+banner1.getMainIcon());
                titles.add(banner1.getMenuDesc());
                menuPkIds.add(banner1.getMenuPkId());
            }
            //设置图片集合
            banner.setImages(images);
            banner.setBannerTitles(titles);
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    //ToastUtils.toast(getActivity(),"position:"+position);
                    //跳转界面，显示详情
                    Intent intent = new Intent(getActivity(), MenuDetailActivity.class);
                    intent.putExtra("menuPkId", menuPkIds.get(position));
                    startActivity(intent);
                }
            });
            //banner设置方法全部调用完毕时最后调用
            banner.start();
        }else{
            ToastUtils.toast(getActivity(),"获取banner失败");
        }
    }
}
