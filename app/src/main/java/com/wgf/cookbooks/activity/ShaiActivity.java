package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.GetShaiAsyncTask;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SwitchAnimationUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒列表界面
 */
public class ShaiActivity extends AppCompatActivity implements ShaiDetailRecycleViewAdapter.IShaiClickListener {
    private CustomToolbar mCustomToolbar;
    private ImageView mAddShai;
    private RecycleDivider mRecycleDivider;
    private GetShaiAsyncTask mGetShaiAsyncTask;
    private RecyclerView mRecyclerViewShai;
    private ShaiDetailRecycleViewAdapter mShaiDetailRecycleViewAdapter;
    private boolean isLoading = true;//正在加载
    private int pageNo = 1;//加载第几页
    private boolean havaData = true;//有数据
    private List<Shai> shaiList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwitchAnimationUtils.exitActivitySlideLeft(this);


        setContentView(R.layout.activity_shai);
        shaiList = new ArrayList<>();

        initView();

        initData();

        setListener();

    }

    /**
     * 初始化显示的数据
     */
    private void initData() {
        if (mGetShaiAsyncTask != null) {
            return;
        }
        mGetShaiAsyncTask = new GetShaiAsyncTask(new GetShaiAsyncTask.IGetShaiListener() {
            @Override
            public void getShaiList(List<Shai> lists) {
                if(lists!=null){
                    shaiList.addAll(lists);
                    mRecycleDivider = new RecycleDivider(ShaiActivity.this, RecycleDivider.VERITCAL_LIST);
                    mShaiDetailRecycleViewAdapter = new ShaiDetailRecycleViewAdapter(ShaiActivity.this, lists);
                    mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(ShaiActivity.this));
                    mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                    mRecyclerViewShai.setAdapter(mShaiDetailRecycleViewAdapter);

                    mShaiDetailRecycleViewAdapter.setmIShaiClickListener(ShaiActivity.this);

                    if (mGetShaiAsyncTask != null) {
                        mGetShaiAsyncTask = null;
                        pageNo++;//刷新时显示下一页
                    }
                }
            }
        });
        mGetShaiAsyncTask.execute(1);//开始加载第一页数据
    }


    /**
     * 初始化监听
     */

    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        mAddShai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.toast(ShaiActivity.this, "go to share shai");
            }
        });


        mRecyclerViewShai.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mShaiDetailRecycleViewAdapter.getItemCount()) {

                    mShaiDetailRecycleViewAdapter.setLoadStatus(1);
                    isLoading = false;
                    if (mGetShaiAsyncTask != null) {
                        return;
                    }
                    mGetShaiAsyncTask = new GetShaiAsyncTask(new GetShaiAsyncTask.IGetShaiListener() {
                        @Override
                        public void getShaiList(List<Shai> lists) {
                            if (lists != null) {
                                shaiList.addAll(lists);
                                mShaiDetailRecycleViewAdapter.addMoreItem(lists);
                                isLoading = true;
                                if (mGetShaiAsyncTask != null) {
                                    mGetShaiAsyncTask = null;
                                    pageNo++;//显示下一页
                                }
                            } else {
                                mShaiDetailRecycleViewAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                            }
                        }
                    }

                    );
                    mGetShaiAsyncTask.execute(pageNo);//开始加载第一页数据

                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mShaiDetailRecycleViewAdapter.getItemCount()) {
                    ToastUtils.toast(ShaiActivity.this, "加载晒晒中...");

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerViewShai.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
            }
        });

    }


    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_toolbar_back);
        mAddShai = (ImageView) findViewById(R.id.id_iv_shai);
        mRecyclerViewShai = (RecyclerView) findViewById(R.id.id_rv_shais);
    }



    @Override
    public void like(int position) {
        String token = GetAuthorizationUtil.getAuth(ShaiActivity.this);
        if(token != null){
            mShaiDetailRecycleViewAdapter.flashItem(position);
        }else{
            ToastUtils.toast(ShaiActivity.this,"请先登录");
            IntentUtils.jump(ShaiActivity.this,LoginActivity.class);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mShaiDetailRecycleViewAdapter!=null) {
            mShaiDetailRecycleViewAdapter.flashLikeContent();
        }
    }


    @Override
    public void comment(int position) {
        ToastUtils.toast(this, "comment pos:" + position);
    }


    @Override
    public void detail(int position) {
        ToastUtils.toast(this, "detail pos:" + position);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mShaiDetailRecycleViewAdapter!=null){
            mShaiDetailRecycleViewAdapter = null;
        }
    }
}
