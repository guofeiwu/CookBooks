package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiCommentListRecycleViewAdapter;
import com.wgf.cookbooks.adapter.ShaiCommentRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.clazz.DeleteShaiCommentAsyncTask;
import com.wgf.cookbooks.clazz.GetCommentAsyncTask;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 评论列表界面
 */
public class ShaiCommentActivity extends AppCompatActivity implements ShaiCommentListRecycleViewAdapter.ICommentDeleteListener {
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private int commentTotal;
    private int shaiPkId;
    private List<Comment> comments;
    private RecycleDivider mRecycleDivider;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private ShaiCommentListRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private boolean havaData = true;//有数据
    private int pageNo = 1;//第几页数据
    private DeleteShaiCommentAsyncTask mDeleteShaiCommentAsyncTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shai_comment);
        initView();
        initData();

        setListener();



    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {

                    mAdapter.setLoadStatus(1);
                    isLoading = false;
                    if (mGetCommentAsyncTask != null) {
                        return;
                    }

                    mGetCommentAsyncTask = new GetCommentAsyncTask(ShaiCommentActivity.this, new GetCommentAsyncTask.ICommentListener() {
                        @Override
                        public void success(List<Comment> commentList) {
                            if(commentList!=null) {
                                comments.addAll(commentList);
                                mAdapter.addMoreItem(commentList);
                                isLoading = true;
                                if (mGetCommentAsyncTask != null) {
                                    mGetCommentAsyncTask = null;
                                    pageNo++;//显示下一页
                                }
                            } else {
                                mAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                            }
                        }

                        @Override
                        public void fail(int result) {
                            if(result!=-1){
                                mAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                            }
                        }
                    });

                    mGetCommentAsyncTask.execute(shaiPkId,pageNo);
                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(ShaiCommentActivity.this, "加载评论中...");
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

    /**
     * 绑定控件
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_comment_list);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.ct_back);
    }


    private void initData() {

        Intent intent = getIntent();
        shaiPkId =intent.getIntExtra("shaiPkId",0);
        commentTotal = intent.getIntExtra("commentTotal",0);
        mCustomToolbar.setToolbarTitle("评论("+commentTotal+")");//设置toolbar的标题

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(ShaiCommentActivity.this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);


        comments = new ArrayList<>();
        if(mGetCommentAsyncTask !=null){
            return;
        }

        mGetCommentAsyncTask = new GetCommentAsyncTask(ShaiCommentActivity.this,new GetCommentAsyncTask.ICommentListener(){

            @Override
            public void success(List<Comment> commentList) {
                comments.addAll(commentList);
                mAdapter = new ShaiCommentListRecycleViewAdapter(ShaiCommentActivity.this,commentList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setmListener(ShaiCommentActivity.this);
                pageNo++;//下一页
                if(mGetCommentAsyncTask!= null){
                    mGetCommentAsyncTask = null;
                }
            }
            @Override
            public void fail(int result) {
            }
        });
        mGetCommentAsyncTask.execute(shaiPkId,pageNo);
    }


    private int flag = 1;//删除的数量

    /**
     * 删除评论
     * @param position
     */
    @Override
    public void delete(int position) {
        Comment comment = comments.get(position);
        comments.remove(position);


        //删除评论
        mAdapter.deleteComment(position);
        commentTotal = commentTotal -flag;

        mCustomToolbar.setToolbarTitle("评论("+commentTotal+")");
        if(mDeleteShaiCommentAsyncTask!=null){
            return;
        }

        mDeleteShaiCommentAsyncTask = new DeleteShaiCommentAsyncTask(this, new DeleteShaiCommentAsyncTask.IDeleteShaiCommentListener() {
            @Override
            public void result(int code) {
                if(code == SUCCESS){
                    ToastUtils.toast(ShaiCommentActivity.this,getString(R.string.text_delete_success));
                    SpUtils.getEditor(ShaiCommentActivity.this).putInt("commentChange",commentTotal).commit();
                }else{
                    ToastUtils.toast(ShaiCommentActivity.this,getString(R.string.text_delete_failed));
                }

                if(mDeleteShaiCommentAsyncTask!=null){
                    mDeleteShaiCommentAsyncTask = null;
                }
            }
        });
        mDeleteShaiCommentAsyncTask.execute(comment.getCommnetPkId());
    }
}
