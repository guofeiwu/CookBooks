package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.CommentListRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.clazz.DeleteCommentAsyncTask;
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
public class CommentListActivity extends AppCompatActivity implements CommentListRecycleViewAdapter.ICommentDeleteListener {
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private int commentTotal;
    private int shaiPkId;
    private List<Comment> comments;
    private RecycleDivider mRecycleDivider;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private CommentListRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private boolean havaData = true;//有数据
    private int pageNo = 1;//第几页数据
    private DeleteCommentAsyncTask mDeleteCommentAsyncTask;
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

                    mGetCommentAsyncTask = new GetCommentAsyncTask(CommentListActivity.this, new GetCommentAsyncTask.ICommentListener() {
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

                    if(flag.equals("menu")){
                        mGetCommentAsyncTask.execute(shaiPkId,pageNo,100);//菜谱id
                    }else{
                        mGetCommentAsyncTask.execute(shaiPkId,pageNo);
                    }

                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(CommentListActivity.this, "加载评论中...");
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


    private String flag;

    /**
     * 初始化数据
     */
    private void initData() {

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if(flag.equals("menu")){
            shaiPkId =intent.getIntExtra("menuPkId",0);//菜谱id
        }else{
            shaiPkId =intent.getIntExtra("shaiPkId",0);
        }

        commentTotal = intent.getIntExtra("commentTotal",0);
        mCustomToolbar.setToolbarTitle("评论("+commentTotal+")");//设置toolbar的标题

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(CommentListActivity.this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);


        comments = new ArrayList<>();
        if(mGetCommentAsyncTask !=null){
            return;
        }

        mGetCommentAsyncTask = new GetCommentAsyncTask(CommentListActivity.this,new GetCommentAsyncTask.ICommentListener(){

            @Override
            public void success(List<Comment> commentList) {
                comments.addAll(commentList);
                mAdapter = new CommentListRecycleViewAdapter(CommentListActivity.this,commentList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setmListener(CommentListActivity.this);
                pageNo++;//下一页
                if(mGetCommentAsyncTask!= null){
                    mGetCommentAsyncTask = null;
                }
            }
            @Override
            public void fail(int result) {
            }
        });

        if(flag.equals("menu")){
            mGetCommentAsyncTask.execute(shaiPkId,pageNo,100);//菜谱id
        }else{
            mGetCommentAsyncTask.execute(shaiPkId,pageNo);
        }

    }



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
        commentTotal = commentTotal -1;

        mCustomToolbar.setToolbarTitle("评论("+commentTotal+")");
        if(mDeleteCommentAsyncTask !=null){
            return;
        }

        mDeleteCommentAsyncTask = new DeleteCommentAsyncTask(this, new DeleteCommentAsyncTask.IDeleteShaiCommentListener() {
            @Override
            public void result(int code) {
                if(code == SUCCESS){
                    ToastUtils.toast(CommentListActivity.this,getString(R.string.text_delete_success));
                    if(flag.equals("menu")){
                        SpUtils.getEditor(CommentListActivity.this).putInt("menuCommentChange",commentTotal).commit();
                    }else{
                        SpUtils.getEditor(CommentListActivity.this).putInt("shaiCommentChange",commentTotal).commit();
                    }

                }else{
                    ToastUtils.toast(CommentListActivity.this,getString(R.string.text_delete_failed));
                }

                if(mDeleteCommentAsyncTask !=null){
                    mDeleteCommentAsyncTask = null;
                }
            }
        });

        if(flag.equals("menu")){
            mDeleteCommentAsyncTask.execute(comment.getCommnetPkId(),100);//菜谱id
        }else{
            mDeleteCommentAsyncTask.execute(comment.getCommnetPkId());
        }

    }
}
