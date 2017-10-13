package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.CommentListRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.clazz.DeleteCommentAsyncTask;
import com.wgf.cookbooks.clazz.GetCommentAsyncTask;
import com.wgf.cookbooks.clazz.UpCommentAsyncTask;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
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
    private int pkId;
    private List<Comment> comments;
    private RecycleDivider mRecycleDivider;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private CommentListRecycleViewAdapter mAdapter;
    private boolean isLoading = true;
    private boolean havaData = true;//有数据
    private int pageNo = 1;//第几页数据
    private DeleteCommentAsyncTask mDeleteCommentAsyncTask;
    private TextView mSendComment;
    private EditText mCommentContent;
    private UpCommentAsyncTask mUpCommentAsyncTask;
    private LinearLayout mCommentLayout;
    private TextView mNoComment;
    private boolean firstNoData = false;//第一次进有数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comment);
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


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(firstNoData){//没数据
                    ToastUtils.toast(CommentListActivity.this, "暂无更多评论...");
                    if (mGetCommentAsyncTask != null) {
                        mGetCommentAsyncTask = null;
                    }
                    mAdapter.setLoadStatus(0);
                    return;
                }

                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    mCommentLayout.setVisibility(View.VISIBLE);
                    mAdapter.setLoadStatus(1);
                    isLoading = false;
                    if (mGetCommentAsyncTask != null) {
                        return;
                    }

                    mGetCommentAsyncTask = new GetCommentAsyncTask(CommentListActivity.this, new GetCommentAsyncTask.ICommentListener() {
                        @Override
                        public void success(List<Comment> commentList) {

                            if (commentList != null &&commentList.size()>0) {
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
                                mCommentLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void fail(int result) {
                            if (result != -1) {
                                mAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                                mCommentLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    if (flag.equals("menu")) {
                        mGetCommentAsyncTask.execute(pkId, pageNo, 100);//菜谱id
                    } else {
                        mGetCommentAsyncTask.execute(pkId, pageNo);
                    }

                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mAdapter.getItemCount()) {
                    ToastUtils.toast(CommentListActivity.this, "加载评论中...");
                    mAdapter.setLoadStatus(2);//正在加载更多数据
                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    //滚动
                    mCommentLayout.setVisibility(View.GONE);
                }else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    mCommentLayout.setVisibility(View.VISIBLE);
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


        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mCommentContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.toast(CommentListActivity.this, "请输入要评论的内容");
                } else {

                    if (flag.equals("menu")) {
                            //将菜谱的评论上传到服务器
                        final Map<String, Object> map = new HashMap<>();
                        map.put("menuPkId", pkId);
                        map.put("content", content);
                        JSONObject jsonObject = new JSONObject(map);
                        if (mUpCommentAsyncTask != null) {
                            return;
                        }

                        mUpCommentAsyncTask = new UpCommentAsyncTask(CommentListActivity.this, new UpCommentAsyncTask.IUpCommentListener() {
                            @Override
                            public void commentSuccess(Comment comment) {
                                setVisibility();
                                SoftInputUtils.hideSoftInput(CommentListActivity.this);
                                ToastUtils.toast(CommentListActivity.this, getString(R.string.text_comment_success));
                                mCommentContent.setText("");
                                comments.add(0,comment);
                                //刷新
                                if(mAdapter == null){
                                    List<Comment> commentList = new ArrayList<>();
                                    commentList.add(comment);
                                    mAdapter = new CommentListRecycleViewAdapter(CommentListActivity.this, commentList);
                                    mAdapter.setmListener(CommentListActivity.this);
                                    mRecyclerView.setAdapter(mAdapter);
                                }else{
                                    mAdapter.setmListener(CommentListActivity.this);
                                    mAdapter.insertFirstComment(comment);
                                }
                                SpUtils.getEditor(CommentListActivity.this).putInt("menuCommentChange", ++commentTotal).commit();
                                mCustomToolbar.setToolbarTitle("评论(" + commentTotal + ")");
                                mRecyclerView.scrollToPosition(0);
                                if (mUpCommentAsyncTask != null) {
                                    mUpCommentAsyncTask = null;
                                }
                            }

                            @Override
                            public void commentFailed() {
                                SoftInputUtils.hideSoftInput(CommentListActivity.this);
                                ToastUtils.toast(CommentListActivity.this, getString(R.string.text_comment_failed));
                                if (mUpCommentAsyncTask != null) {
                                    mUpCommentAsyncTask = null;
                                }
                            }
                        });
                        String url = BASE_URL + "/app/menu/comment";
                        mUpCommentAsyncTask.execute(jsonObject.toString(), url);


                    } else {
                        //将评论内容上传到服务器
                        Map<String, Object> map = new HashMap<>();
                        map.put("shaiPkId", pkId);
                        map.put("content", content);
                        JSONObject jsonObject = new JSONObject(map);

                        if (mUpCommentAsyncTask != null) {
                            return;
                        }

                        mUpCommentAsyncTask = new UpCommentAsyncTask(CommentListActivity.this, new UpCommentAsyncTask.IUpCommentListener() {
                            @Override
                            public void commentSuccess(Comment comment) {
                                setVisibility();

                                SoftInputUtils.hideSoftInput(CommentListActivity.this);
                                ToastUtils.toast(CommentListActivity.this, getString(R.string.text_comment_success));
                                mCommentContent.setText("");
                                comments.add(0,comment);
                                //刷新
                                if(mAdapter == null){
                                    List<Comment> commentList = new ArrayList<>();
                                    commentList.add(comment);
                                    mAdapter = new CommentListRecycleViewAdapter(CommentListActivity.this, commentList);
                                    mAdapter.setmListener(CommentListActivity.this);
                                    mRecyclerView.setAdapter(mAdapter);
                                }else{
                                    mAdapter.setmListener(CommentListActivity.this);
                                    mAdapter.insertFirstComment(comment);
                                }
                                SpUtils.getEditor(CommentListActivity.this).putInt("shaiCommentChange", ++commentTotal).commit();
                                mCustomToolbar.setToolbarTitle("评论(" + commentTotal + ")");
                                mRecyclerView.scrollToPosition(0);
                                if (mUpCommentAsyncTask != null) {
                                    mUpCommentAsyncTask = null;
                                }
                            }

                            @Override
                            public void commentFailed() {
                                SoftInputUtils.hideSoftInput(CommentListActivity.this);
                                ToastUtils.toast(CommentListActivity.this, getString(R.string.text_comment_failed));
                                if (mUpCommentAsyncTask != null) {
                                    mUpCommentAsyncTask = null;
                                }
                            }
                        });
                        String url = BASE_URL + "/app/shai/comment";
                        mUpCommentAsyncTask.execute(jsonObject.toString(), url);
                    }
                }
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_comment_list);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.ct_back);
        mSendComment = (TextView) findViewById(R.id.id_tv_send_comment);
        mCommentContent = (EditText) findViewById(R.id.id_et_comment_content);
        mCommentLayout = (LinearLayout) findViewById(R.id.id_ll_comment);
        mNoComment = (TextView) findViewById(R.id.id_tv_no_comment);
    }


    private String flag;

    /**
     * 初始化数据
     */
    private void initData() {

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (flag.equals("menu")) {
            pkId = intent.getIntExtra("menuPkId", 0);//菜谱id
        } else {
            pkId = intent.getIntExtra("shaiPkId", 0);
        }

        commentTotal = intent.getIntExtra("commentTotal", 0);
        mCustomToolbar.setToolbarTitle("评论(" + commentTotal + ")");//设置toolbar的标题

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(CommentListActivity.this, RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);


        comments = new ArrayList<>();
        if (mGetCommentAsyncTask != null) {
            return;
        }

        mGetCommentAsyncTask = new GetCommentAsyncTask(CommentListActivity.this, new GetCommentAsyncTask.ICommentListener() {

            @Override
            public void success(List<Comment> commentList) {
                setVisibility();

                comments.addAll(commentList);
                mAdapter = new CommentListRecycleViewAdapter(CommentListActivity.this, commentList);
                mAdapter.setmListener(CommentListActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                pageNo++;//下一页
                if (mGetCommentAsyncTask != null) {
                    mGetCommentAsyncTask = null;
                }
            }

            @Override
            public void fail(int result) {
                firstNoData = true;//第一次进来无数据
                mNoComment.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                if (mGetCommentAsyncTask != null) {
                    mGetCommentAsyncTask = null;
                }
            }
        });

        if (flag.equals("menu")) {
            mGetCommentAsyncTask.execute(pkId, pageNo, 100);//菜谱id
        } else {
            mGetCommentAsyncTask.execute(pkId, pageNo);
        }

    }

    /**
     * 设置可见性
     */
    private void setVisibility(){
        int visibility = mRecyclerView.getVisibility();
        if(visibility == View.GONE){
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoComment.setVisibility(View.GONE);
        }
    }



    /**
     * 开发过程中出现的bug
     * 这些bug已经解决
     * 1、第一次进入到晒晒或菜谱详情界面， 有评论，但是不显示，在评论列表，如果没有评论，第一次添加后删除，会出现异常
     * 2、如果有评论，加到第一条，删除错乱问题，会把下一个删除
     * 3、如果没有评论，插入第二条评论的时候出异常
     * 4、第一次进来没评论时候，进行评论闪退
     */



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
        commentTotal = commentTotal - 1;

        mCustomToolbar.setToolbarTitle("评论(" + commentTotal + ")");
        if (mDeleteCommentAsyncTask != null) {
            return;
        }

        mDeleteCommentAsyncTask = new DeleteCommentAsyncTask(this, new DeleteCommentAsyncTask.IDeleteShaiCommentListener() {
            @Override
            public void result(int code) {
                if (code == SUCCESS) {
                    ToastUtils.toast(CommentListActivity.this, getString(R.string.text_delete_success));
                    if (flag.equals("menu")) {
                        SpUtils.getEditor(CommentListActivity.this).putInt("menuCommentChange", commentTotal).commit();
                    } else {
                        SpUtils.getEditor(CommentListActivity.this).putInt("shaiCommentChange", commentTotal).commit();
                    }

                } else {
                    ToastUtils.toast(CommentListActivity.this, getString(R.string.text_delete_failed));
                }

                if (mDeleteCommentAsyncTask != null) {
                    mDeleteCommentAsyncTask = null;
                }
            }
        });

        if (flag.equals("menu")) {
            mDeleteCommentAsyncTask.execute(comment.getCommnetPkId(), 100);//菜谱id
        } else {
            mDeleteCommentAsyncTask.execute(comment.getCommnetPkId());
        }
        //评论删完了显示的样子
        if(commentTotal == 0){
            int visibility = mRecyclerView.getVisibility();
            if(visibility == View.VISIBLE){
                mRecyclerView.setVisibility(View.GONE);
                mNoComment.setVisibility(View.VISIBLE);
            }
        }
    }
}
