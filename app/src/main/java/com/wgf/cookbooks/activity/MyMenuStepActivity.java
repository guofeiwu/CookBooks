package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.UserMenuRecordRecycleViewAdapter;
import com.wgf.cookbooks.bean.Record;
import com.wgf.cookbooks.clazz.asynctask.DeleteUserMenuRecordAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetUserRecordMenuAsyncTask;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 美食足迹
 */
public class MyMenuStepActivity extends AppCompatActivity implements GetUserRecordMenuAsyncTask.IGetUserRecordMenuListener,
        UserMenuRecordRecycleViewAdapter.IMenuOnClickListener ,DeleteUserMenuRecordAsyncTask.IDeleteUserMenuRecordListener{
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private List<Record> records;
    private GetUserRecordMenuAsyncTask mGetUserRecordMenuAsyncTask;
    private int pageNo = 1;
    private UserMenuRecordRecycleViewAdapter mAdapter;
    private TextView mNoMenuRecord;
    private boolean isLoading = true;
    private boolean haveDate = true;
    private boolean firstLoading = true;//第一次加载数据
    private DeleteUserMenuRecordAsyncTask mDeleteUserMenuRecordAsyncTask;
    private TextView mDeleteAll;
    //移除所有的美食足迹
    private boolean removeAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu_step);
        initView();
        setListener();

        initData();

    }

    /**
     * 舒适化数据
     */
    private void initData() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(records != null){
            records.clear();
            records = null;
        }

        records = new ArrayList<>();
        if(mGetUserRecordMenuAsyncTask!=null){
            return;
        }

        mGetUserRecordMenuAsyncTask = new GetUserRecordMenuAsyncTask(this);
        mGetUserRecordMenuAsyncTask.setmListener(this);
        //获取第一页数据
        mGetUserRecordMenuAsyncTask.execute(1);

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
        //添加滑动事件
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mGetUserRecordMenuAsyncTask != null) {
                        return;
                    }
                    mGetUserRecordMenuAsyncTask = new GetUserRecordMenuAsyncTask(MyMenuStepActivity.this);
                    mGetUserRecordMenuAsyncTask.setmListener(MyMenuStepActivity.this);
                    mGetUserRecordMenuAsyncTask.execute(++pageNo);
                } else if (!isLoading && haveDate && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(MyMenuStepActivity.this, "加载菜谱记录中...");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                lastVisiableItem = linearManager.findLastVisibleItemPosition();
            }
        });


        //移除所有的美食足迹
        mDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(records == null || records.size() == 0){
                    ToastUtils.toast(MyMenuStepActivity.this,"还没有美食足迹呢，快去浏览喜欢的美食吧");
                    return;
                }

                if(mDeleteUserMenuRecordAsyncTask!=null){
                    return;
                }
                mDeleteUserMenuRecordAsyncTask = new DeleteUserMenuRecordAsyncTask(MyMenuStepActivity.this);
                mDeleteUserMenuRecordAsyncTask.setmListener(MyMenuStepActivity.this);

                String url = BASE_URL+"/app/menu/record/all";
                mDeleteUserMenuRecordAsyncTask.execute(url);
                removeAll = true;
            }
        });
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_user_record_back);
        mRecyclerView  = (RecyclerView) findViewById(R.id.id_rv_menu_record);
        mNoMenuRecord = (TextView) findViewById(R.id.id_tv_no_menu_record);
        mDeleteAll = (TextView) findViewById(R.id.id_tv_delete_all);
    }

    /**
     * 获取用户浏览的菜谱的回调
     *
     * @param list
     */
    @Override
    public void userRecordMenus(List<Record> list) {
        //有数据
        if(list!=null && list.size()>0){
            firstLoading =false;
            records.addAll(list);
            if(mAdapter == null){
                mAdapter = new UserMenuRecordRecycleViewAdapter(this,list);
                mAdapter.setmListener(this);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        }else{
            //第一次加载数据
            if(firstLoading){
                mNoMenuRecord.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }else{
                haveDate = false;
                if (mAdapter != null) {
                    mAdapter.setLoadStatus(0);
                }
                ToastUtils.toast(this, "菜谱记录已全部加载完成...");
            }
        }
        if(mGetUserRecordMenuAsyncTask !=null){
            mGetUserRecordMenuAsyncTask = null;
        }
    }

    @Override
    public void detail(int position) {
        //跳转界面，显示详情
        Intent intent = new Intent(this, MenuDetailActivity.class);
        intent.putExtra("menuPkId", records.get(position).getMenus().getMenuPkId());
        startActivity(intent);
    }

    @Override
    public void remove(int position) {
        ToastUtils.toast(this,"remove pos:"+position);
        if(mAdapter!=null){

            int recordPkId = records.get(position).getRecordPkId();

            records.remove(position);
            mAdapter.removeItem(position);

            //列表无数据，则将recycleView移除，显示无数据提示
            if(records.size() == 0){
                mRecyclerView.setVisibility(View.GONE);
                mNoMenuRecord.setVisibility(View.VISIBLE);
            }
            if(mDeleteUserMenuRecordAsyncTask!=null){
                return;
            }
            mDeleteUserMenuRecordAsyncTask = new DeleteUserMenuRecordAsyncTask(this);
            mDeleteUserMenuRecordAsyncTask.setmListener(this);

            String url = BASE_URL+"/app/menu/record/"+recordPkId;

            mDeleteUserMenuRecordAsyncTask.execute(url);
        }
    }

    //删除菜谱记录的接口的回调
    @Override
    public void deleteRecord(int code) {
        if(mDeleteUserMenuRecordAsyncTask!=null){
            mDeleteUserMenuRecordAsyncTask = null;
        }
        if(code ==SUCCESS){
            if(removeAll){
                mRecyclerView.setVisibility(View.GONE);
                mNoMenuRecord.setVisibility(View.VISIBLE);
                removeAll = false;

                records.clear();
                records = null;
            }
            ToastUtils.toast(this, "移除美食足迹成功");
        }else{
            ToastUtils.toast(this, "系统异常");
        }
    }
}
