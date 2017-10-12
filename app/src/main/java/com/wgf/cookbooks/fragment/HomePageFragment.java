package com.wgf.cookbooks.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.clazz.GlideImageLoader;
import com.wgf.cookbooks.util.ToastUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class HomePageFragment extends Fragment {
    private Banner banner;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        initView(view);
        initBanner();
        return view;
    }

    /**
     * 绑定控件
     * @param view
     */
    private void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
    }


    /**
     * 初始化banner数据
     */
    private void initBanner(){
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);

        List<String> images = new ArrayList<>();
        images.add(BASE_URL_FILE_MENUS+"menu1/0.jpg");
        images.add(BASE_URL_FILE_MENUS+"menu2/0.jpg");
        images.add(BASE_URL_FILE_MENUS+"menu3/0.jpg");
        //设置图片集合
        banner.setImages(images);
        List<String> titles = new ArrayList<>();
        titles.add("秋凉至,来一晚泪流满面");
        titles.add("给我一个土豆我就能玩转厨房");
        titles.add("秋冬必吃肥牛菜,再来一碗");
        banner.setBannerTitles(titles);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ToastUtils.toast(getActivity(),"position:"+position);
            }
        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }



}
