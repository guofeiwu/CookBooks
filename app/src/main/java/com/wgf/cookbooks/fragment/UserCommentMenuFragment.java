package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.MenuDetailActivity;
import com.wgf.cookbooks.adapter.UserMenuRecycleViewAdapter;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.GetUserCommentMenuAsyncTask;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UserCommentMenuFragment extends Fragment implements GetUserCommentMenuAsyncTask.IGetUserCommentMenuListener
        ,UserMenuRecycleViewAdapter.IMenuDetailListener {

    private RecyclerView mRecyclerView;
    private UserMenuRecycleViewAdapter mAdapter;
    private GetUserCommentMenuAsyncTask mGetUserCommentMenuAsyncTask;
    //初始化页数
    private int pageNo = 1;
    private List<Menu> menus;
    //正在加载
    private boolean isLoading = true;
    //有数据
    private boolean havaData = true;
    //第一次加载数据
    private boolean firstLoading = true;
    private TextView mNoCollect;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_comment_menu, container, false);
        initView(view);
        return view;
    }


    /**
     * 初始化控件
     */
    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_rv_user_collect);
        mNoCollect = (TextView) view.findViewById(R.id.id_tv_no_collect);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setData();
        setListener();
    }

    /**
     * 设置数据
     */
    private void setData() {

        if (menus != null) {
            menus.clear();
            menus = null;
        }
        menus = new ArrayList<>();

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                if (itemViewType == mAdapter.TYPE_FOOTER) {//判断当前类型是否是FootView
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });

        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mGetUserCommentMenuAsyncTask != null) {
            return;
        }
        mGetUserCommentMenuAsyncTask = new GetUserCommentMenuAsyncTask(getActivity());
        mGetUserCommentMenuAsyncTask.setmListener(this);
        mGetUserCommentMenuAsyncTask.execute(pageNo);
    }

    /**
     * 设置监听
     */
    private void setListener() {

        //添加滑动事件
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;

                    if (mGetUserCommentMenuAsyncTask != null) {
                        return;
                    }
                    mGetUserCommentMenuAsyncTask = new GetUserCommentMenuAsyncTask(getActivity());
                    mGetUserCommentMenuAsyncTask.setmListener(UserCommentMenuFragment.this);
                    mGetUserCommentMenuAsyncTask.execute(++pageNo);
                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(getActivity(), "加载菜谱中...");
                    mAdapter.setLoadStatus(2);
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
    }




    @Override
    public void userCommentMenus(List<Menu> list) {
        if (mGetUserCommentMenuAsyncTask != null) {
            mGetUserCommentMenuAsyncTask = null;
        }
        if (list != null && list.size() > 0) {
            //不是第一加载数据
            firstLoading = false;
            menus.addAll(list);
            if (mAdapter == null) {
                mAdapter = new UserMenuRecycleViewAdapter(getActivity(), list);
                mAdapter.setmListener(this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.addMoreItem(list);
                isLoading = true;
            }
        } else {
            //无数据，并且是第一次
            if (firstLoading) {
                mRecyclerView.setVisibility(View.GONE);
                mNoCollect.setVisibility(View.VISIBLE);
            } else {
                havaData = false;
                if (mAdapter != null) {
                    mAdapter.setLoadStatus(0);
                }
                ToastUtils.toast(getActivity(), "菜谱已全部加载完成...");
            }
        }
    }

    /**
     * 查看菜谱详情的回调
     *
     * @param position
     */
    @Override
    public void detail(int position) {
        ToastUtils.toast(getActivity(), "detail:" + position);
        //跳转界面，显示详情
        Intent intent = new Intent(getActivity(), MenuDetailActivity.class);
        intent.putExtra("menuPkId", menus.get(position).getMenuPkId());
        intent.putExtra("menuPos", position);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        int pos = SpUtils.getSharedPreferences(getActivity()).getInt("userMenuNoComment", -1);
        if (pos != -1) {
            //说明没有评论了
            menus.remove(pos);
            mAdapter.removeItem(pos);
            SpUtils.getEditor(getActivity()).putInt("userMenuNoComment", -1).commit();
            if(menus.size() == 0){
                mRecyclerView.setVisibility(View.GONE);
                mNoCollect.setVisibility(View.VISIBLE);
            }

        }
    }
}
