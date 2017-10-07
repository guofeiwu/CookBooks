package com.wgf.cookbooks.activity;

import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiCommentRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.GetCommentAsyncTask;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒详情
 */
public class ShaiDetailActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<Comment> comments;
    private ShaiCommentRecycleViewAdapter mAdapter;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private Shai shai;
    private TextView mUserName;
    private RecycleDivider mRecycleDivider;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shai_detail_layout);


        initView();

        shai = (Shai) getIntent().getExtras().getSerializable("shai");

        initData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(ShaiDetailActivity.this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        comments = new ArrayList<>();

        mUserName.setText(shai.getUserName());

        if(mGetCommentAsyncTask !=null){
            return;
        }

        mGetCommentAsyncTask = new GetCommentAsyncTask(new GetCommentAsyncTask.ICommentListener(){

            @Override
            public void success(List<Comment> comments) {
                mAdapter = new ShaiCommentRecycleViewAdapter(ShaiDetailActivity.this,comments);
                mRecyclerView.setAdapter(mAdapter);

                if(mGetCommentAsyncTask!= null){
                    mGetCommentAsyncTask = null;
                }

            }
            @Override
            public void fail() {
                ToastUtils.toast(ShaiDetailActivity.this,"获取评论失败");
            }
        });
        mGetCommentAsyncTask.execute(shai.getShaiPkId(),1);
    }



    /**
     * 初始化控件
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_comment);
        mUserName = (TextView) findViewById(R.id.id_tv_user_name_item);
    }
}
