package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.MenuRankActivity;
import com.wgf.cookbooks.activity.SearchActivity;
import com.wgf.cookbooks.activity.ShaiActivity;
import com.wgf.cookbooks.activity.ShaiDetailActivity;
import com.wgf.cookbooks.activity.ThematicActivity;
import com.wgf.cookbooks.adapter.ShaiRecycleViewAdapter;
import com.wgf.cookbooks.adapter.ThematicRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.bean.Thematic;
import com.wgf.cookbooks.clazz.asynctask.GetShaiAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetThematicAsyncTask;
import com.wgf.cookbooks.util.Constants;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;


import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_SHAI;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 发现主页
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener,GetThematicAsyncTask.IGetThematicListener,
        ThematicRecycleViewAdapter.IThematicDetailListener{

    private RecyclerView mRecyclerViewShai;
    private ShaiRecycleViewAdapter mShaiRecycleViewAdapter;
    private List<String> urls;
    private RecycleDivider mRecycleDivider;
    private GetShaiAsyncTask mGetShaiAsyncTask;
    private RelativeLayout mShaiYiShai;

    private List<Shai> shais;
    private EditText mEditTextSearch;
    private RecyclerView mRecyclerViewThematics;
    private List<Thematic> thematics;

    private ImageView mLikeRank,mCollectRank,mLookRank;


    private GetThematicAsyncTask mGetThematicAsyncTask;
    private ThematicRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private boolean haveDate = true;
    private int pageNo = 1;
    private NestedScrollView mNestedScrollView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        //initData();

        initView(view);

        setListener();
        return view;
    }

    private void initView(View view) {
        mRecyclerViewShai = (RecyclerView) view.findViewById(R.id.id_rv_shaiyishai);
        mShaiYiShai = (RelativeLayout) view.findViewById(R.id.rl_shai_yi_shai);
        mEditTextSearch = (EditText) view.findViewById(R.id.id_et_search);
        mLikeRank = (ImageView) view.findViewById(R.id.id_iv_like_rank);
        mCollectRank = (ImageView) view.findViewById(R.id.id_iv_collect_rank);
        mLookRank = (ImageView) view.findViewById(R.id.id_iv_look_rank);
        mRecyclerViewThematics = (RecyclerView) view.findViewById(R.id.id_rv_thematics);
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.id_nsv_layout);

//        mThematicFirst = (ImageView) view.findViewById(R.id.id_iv_thematic_first);
//        mThematicSecond = (ImageView) view.findViewById(R.id.id_iv_thematic_second);
//        mThematicThird = (ImageView) view.findViewById(R.id.id_iv_thematic_third);
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mShaiYiShai.setOnClickListener(this);
        mEditTextSearch.setOnClickListener(this);
        mLikeRank.setOnClickListener(this);
        mCollectRank.setOnClickListener(this);
        mLookRank.setOnClickListener(this);


        //添加滑动事件
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && isLoading) {
                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if(mGetThematicAsyncTask!=null){
                        return;
                    }
                    mGetThematicAsyncTask = new GetThematicAsyncTask(getActivity());
                    mGetThematicAsyncTask.setmListener(DiscoverFragment.this);
                    mGetThematicAsyncTask.execute(++pageNo);
                }else if(scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && !isLoading && haveDate){
                    ToastUtils.toast(getActivity(), "加载专题中...");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume");
        initData();
    }

    /**
     * 获取初始化数据
     */
    void initData() {

        /**
         * 这里是初始化晒一晒信息
         */
        shais = new ArrayList<>();
        urls = new ArrayList<>();
        if (mGetShaiAsyncTask != null) {
            return;
        }
        mGetShaiAsyncTask = new GetShaiAsyncTask(new GetShaiAsyncTask.IGetShaiListener() {
            @Override
            public void getShaiList(List<Shai> list) {
                if (list != null) {
                    shais.addAll(list);
                    try {
                        for (int i = 0; i < list.size(); i++) {
                            urls.add(BASE_URL_FILE_SHAI + list.get(i).getAddress());
                        }
                        mRecycleDivider = new RecycleDivider(getContext(), RecycleDivider.HORIZONTAL_LIST);
                        mShaiRecycleViewAdapter = new ShaiRecycleViewAdapter(getActivity(), urls, new ShaiRecycleViewAdapter.IShaiImageClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(getActivity(),ShaiDetailActivity.class);
                                Shai shai = shais.get(position);
                                intent.putExtra("shaiPkId",shai.getShaiPkId());
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                        mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
                        mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                        mRecyclerViewShai.setAdapter(mShaiRecycleViewAdapter);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(mGetShaiAsyncTask!=null)
                        {
                            mGetShaiAsyncTask=null;
                        }
                    }
                }
            }
        });
        mGetShaiAsyncTask.execute(1);//开始加载第一页数据
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerViewThematics.setLayoutManager(linearLayoutManager);
        mRecyclerViewThematics.setHasFixedSize(true);
        mRecyclerViewThematics.setNestedScrollingEnabled(false);
        initThematic();
    }


    /**
     * 初始化专题信息
     */
    private void initThematic(){
        if(thematics!=null){
            thematics.clear();
            thematics = null;
        }
        thematics = new ArrayList<>();

        if(mGetThematicAsyncTask!=null){
            return;
        }

        mGetThematicAsyncTask = new GetThematicAsyncTask(getActivity());
        mGetThematicAsyncTask.setmListener(DiscoverFragment.this);
        mGetThematicAsyncTask.execute(pageNo);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_shai_yi_shai:
                //跳转，查看更多的晒一晒
                IntentUtils.jump(getActivity(), ShaiActivity.class);
                break;
            case R.id.id_et_search:
                IntentUtils.jump(getActivity(), SearchActivity.class);
                break;
            case R.id.id_iv_like_rank:
                Intent intent1 = new Intent(getActivity(), MenuRankActivity.class);
                intent1.putExtra("flag",1);
                startActivity(intent1);
                break;
            case R.id.id_iv_collect_rank:
                Intent intent2 = new Intent(getActivity(), MenuRankActivity.class);
                intent2.putExtra("flag",2);
                startActivity(intent2);
                break;
            case R.id.id_iv_look_rank:
                Intent intent3 = new Intent(getActivity(), MenuRankActivity.class);
                intent3.putExtra("flag",3);
                startActivity(intent3);
                break;
        }
    }

    /**
     * 获取专题的回调
     * @param list
     */
    @Override
    public void thematic(List<Thematic> list) {

        if(mGetThematicAsyncTask!=null){
            mGetThematicAsyncTask = null;
        }
        //返回有数据
        if(list!=null&& list.size()>0){
            thematics.addAll(list);
            if(mAdapter == null){
                mAdapter = new ThematicRecycleViewAdapter(getActivity(),list);
                mAdapter.setmListener(DiscoverFragment.this);
                mRecyclerViewThematics.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        }else{
            haveDate = false;
            if (mAdapter != null) {
                mAdapter.setLoadStatus(0);
            }
            ToastUtils.toast(getActivity(), "专题已全部加载完成...");
        }
    }
    //查看专题详情的回调
    @Override
    public void detail(int position) {
        ToastUtils.toast(getActivity(),"title:"+thematics.get(position).getThematicName());
        Intent intent = new Intent(getActivity(), ThematicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("thematic",thematics.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
