package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.MenuListActivity;
import com.wgf.cookbooks.adapter.MenuTypeAdapter;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MenuFragment extends Fragment {
    private GridView mThreeMeals,mCaiShi,mCaiXi,mTianDian,mZhuShi;

    private int[] threeMealsImageId = {R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu};
    private String[] threeMealsTitles = {"早餐","中餐","晚餐","夜宵"};

    private int[] caiShiImageId = {R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu};
    private String[] caiShiTitles = {"家常菜","素菜","汤","凉菜","私房菜","荤菜"};

    private int[] caiXiImageId = {R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu};
    private String[] caiXiTitles = {"川菜","粤菜","东北菜","湘菜","鲁菜","清真"};


    private int[] tianDianImageId = {R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu};
    private String[] tianDianTitles = {"蛋糕","饼干","蛋挞","饮品"};

    private int[] zhuShiImageId = {R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu,R.drawable.type_menu};
    private String[] zhuShiTitles = {"饭","面","糕点","粥","米粉"};



    private MenuTypeAdapter threeAdapter;
    private MenuTypeAdapter caiShiAdapter;
    private MenuTypeAdapter caiXiAdapter;
    private MenuTypeAdapter tianDianAdapter;
    private MenuTypeAdapter zhuShiAdapter;
    private List<Map<String,Object>> threeMaps;
    private List<Map<String,Object>> caiShiMaps;
    private List<Map<String,Object>> caiXiMaps;
    private List<Map<String,Object>> tianDianMaps;
    private List<Map<String,Object>> zhuShiMaps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initView(view);

        initData();

        setListener();

        return view;
    }

    private void initData() {

        initItem();

        initAdapter();
    }


    /**
     * 初始化监听
     */
    private void setListener(){
        mThreeMeals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.toast(getActivity(),"pos:"+position);
                //
                Intent intent = new Intent(getActivity(), MenuListActivity.class);
                intent.putExtra("type", Constants.YIRISANCAN);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });

        mCaiShi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MenuListActivity.class);
                intent.putExtra("type", Constants.CAISHI);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });


        mCaiXi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MenuListActivity.class);
                intent.putExtra("type", Constants.CAIXI);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });


        mTianDian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MenuListActivity.class);
                intent.putExtra("type", Constants.TIANDIAN);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });


        mZhuShi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MenuListActivity.class);
                intent.putExtra("type", Constants.ZHUSHI);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
    }






    /**
     * 初始化适配器
     */
    private void initAdapter(){
        if(threeAdapter == null){
            threeAdapter = new MenuTypeAdapter(threeMaps);
            mThreeMeals.setAdapter(threeAdapter);
        }

        if(caiShiAdapter == null){
            caiShiAdapter = new MenuTypeAdapter(caiShiMaps);
            mCaiShi.setAdapter(caiShiAdapter);
        }
        if(caiXiAdapter == null){
            caiXiAdapter = new MenuTypeAdapter(caiXiMaps);
            mCaiXi.setAdapter(caiXiAdapter);
        }
        if(tianDianAdapter == null){
            tianDianAdapter = new MenuTypeAdapter(tianDianMaps);
            mTianDian.setAdapter(tianDianAdapter);
        }
        if(zhuShiAdapter == null){
            zhuShiAdapter = new MenuTypeAdapter(zhuShiMaps);
            mZhuShi.setAdapter(zhuShiAdapter);
        }
    }


    /**
     * 初始化item的数据
     */
    private void initItem(){
        threeMaps = new ArrayList<>();
        for (int i = 0;i<threeMealsImageId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("imageId",threeMealsImageId[i]);
            map.put("title",threeMealsTitles[i]);
            threeMaps.add(map);
        }

        caiShiMaps= new ArrayList<>();
        for (int i = 0;i<caiShiImageId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("imageId",caiShiImageId[i]);
            map.put("title",caiShiTitles[i]);
            caiShiMaps.add(map);
        }

        caiXiMaps= new ArrayList<>();
        for (int i = 0;i<caiXiImageId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("imageId",caiXiImageId[i]);
            map.put("title",caiXiTitles[i]);
            caiXiMaps.add(map);
        }

        tianDianMaps= new ArrayList<>();
        for (int i = 0;i<tianDianImageId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("imageId",tianDianImageId[i]);
            map.put("title",tianDianTitles[i]);
            tianDianMaps.add(map);
        }

        zhuShiMaps= new ArrayList<>();
        for (int i = 0;i<zhuShiImageId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("imageId",zhuShiImageId[i]);
            map.put("title",zhuShiTitles[i]);
            zhuShiMaps.add(map);
        }
    }


    /**
     * 绑定事件
     */
    private void initView(View view) {
        mThreeMeals = (GridView) view.findViewById(R.id.id_gv_three);
        mCaiXi = (GridView) view.findViewById(R.id.id_gv_caixi);
        mCaiShi = (GridView) view.findViewById(R.id.id_gv_caishi);
        mTianDian = (GridView) view.findViewById(R.id.id_gv_tiandian);
        mZhuShi = (GridView) view.findViewById(R.id.id_gv_zhushi);
    }
}
