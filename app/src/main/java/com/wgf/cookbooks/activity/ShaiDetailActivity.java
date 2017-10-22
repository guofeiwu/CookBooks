package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.CommentRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.asynctask.DeleteShaiAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.DeleteCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetShaiDetailAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.JudgeUserHaveShaiCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.UpdateLookTotalAsyncTask;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_SHAI;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒详情
 */
public class ShaiDetailActivity extends AppCompatActivity implements View.OnClickListener,CommentRecycleViewAdapter.ICommentDeleteListener
,JudgeUserHaveShaiCommentAsyncTask.IJudgeUserHaveShaiCommentListener{
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private List<Comment> comments;
    private CommentRecycleViewAdapter mAdapter;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private int shaiPkId;
    private int position;
    private TextView mUserName;
    private RecycleDivider mRecycleDivider;
    private TextView mCommentTotal;
    private LinearLayout mMore;//查看更多
    private CircleImageView mUserIcon;
    private TextView mShaiContent;
    private ImageView mShaImageView;
    private TextView mShaiTime;
    private TextView mLookTotal;//浏览总数
    private TextView mDelete;//删除
    private GetShaiDetailAsyncTask mGetShaiDetailAsyncTask;
    private UpdateLookTotalAsyncTask mUpdateLookTotalAsyncTask;
    private DeleteShaiAsyncTask mDeleteShaiAsyncTask;
    private TextView mNoComment;
    private DeleteCommentAsyncTask mDeleteCommentAsyncTask;
    private int commentTotal;//评论的总数
    private LinearLayout mCommentLayout;
    private JudgeUserHaveShaiCommentAsyncTask mJudgeUserHaveShaiCommentAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shai_detail_layout);

        initView();

        initData();

        setListener();

    }

    /**
     * 初始化数据
     */
    private void initData() {

        Intent intent = getIntent();
        shaiPkId =intent.getIntExtra("shaiPkId",0);
        position = intent.getIntExtra("position",-1);

        if(mGetShaiDetailAsyncTask!=null){
            return;
        }else{
            initShaiDetail();
        }


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(ShaiDetailActivity.this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);

        loadComments();
    }

    /**
     * 加载评论
     */
    private void loadComments() {
        if(comments!=null){
            comments.clear();
            comments = null;
        }
        comments = new ArrayList<>();

        if(mGetCommentAsyncTask !=null){
            return;
        }

        mGetCommentAsyncTask = new GetCommentAsyncTask(ShaiDetailActivity.this,new GetCommentAsyncTask.ICommentListener(){

            @Override
            public void success(List<Comment> commentList) {

                if(mAdapter !=null){
                    mAdapter = null;//之前的adapter置为空
                }
                //成功了就是有数据，没数据的一律回调 fail() 方法
                comments.addAll(commentList);
                mAdapter = new CommentRecycleViewAdapter(ShaiDetailActivity.this, commentList);
                mAdapter.setmListener(ShaiDetailActivity.this);
                mRecyclerView.setAdapter(mAdapter);
//                mRecyclerView.setVisibility(View.VISIBLE);
                setVisibility();
                if (mGetCommentAsyncTask != null) {
                    mGetCommentAsyncTask = null;
                }
            }
            @Override
            public void fail(int result) {
                mNoComment.setVisibility(View.VISIBLE);
                if(result==-1){
                    ToastUtils.toast(ShaiDetailActivity.this,"获取评论失败");
                }
                if(mGetCommentAsyncTask!= null){
                    mGetCommentAsyncTask = null;
                }
            }
        });
        mGetCommentAsyncTask.execute(shaiPkId,1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从评论列表返回时候
        int result = SpUtils.getSharedPreferences(this).getInt("shaiCommentChange",-1);//返回剩下的评论数量
        if (result >0){//改变了，需要刷新列表
            mCommentTotal.setText("评论("+result+")");
            commentTotal = result;
            mNoComment.setVisibility(View.GONE);
            setVisibility();
            if(result>6){
                mMore.setVisibility(View.VISIBLE);
            }else{
                mMore.setVisibility(View.GONE);
            }
            loadComments();
            SpUtils.getEditor(this).putInt("shaiCommentChange",-1).commit();
        }else if (result == 0){
            //删除完了
            mCommentTotal.setText("评论(0)");
            mMore.setVisibility(View.GONE);
            mNoComment.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            SpUtils.getEditor(this).putInt("shaiCommentChange",-1).commit();
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
     * 初始化晒详情
     */
    private void initShaiDetail(){
        mGetShaiDetailAsyncTask = new GetShaiDetailAsyncTask(ShaiDetailActivity.this,new GetShaiDetailAsyncTask.IGetShaiListener() {
            @Override
            public void getShaiDetail(Shai shai) {
                Glide.with(ShaiDetailActivity.this).load(BASE_URL_FILE_ICON+shai.getIcon()).into(mUserIcon);
                mUserName.setText(shai.getUserName());
                mShaiContent.setText(shai.getDescr());
                Glide.with(ShaiDetailActivity.this).load(BASE_URL_FILE_SHAI+shai.getAddress()).into(mShaImageView);
                mShaiTime.setText(shai.getTime());
                int lookTotal = shai.getLookTotal();
                mLookTotal.setText("浏览:"+(lookTotal+1));

                commentTotal= shai.getCommentTotal();
                mUserName.setText(shai.getUserName());
                mCommentTotal.setText("评论("+commentTotal+")");
                if(commentTotal>6){
                    mMore.setVisibility(View.VISIBLE);
                }else{
                    mMore.setVisibility(View.GONE);
                }

                int currentUser = shai.getCurrentUser();
                if(currentUser == 0){//是当前用户
                    mDelete.setVisibility(View.VISIBLE);
                }else{
                    mDelete.setVisibility(View.GONE);
                }
                //更新浏览次数
                if(mUpdateLookTotalAsyncTask!=null){
                    return;
                }
                mUpdateLookTotalAsyncTask = new UpdateLookTotalAsyncTask();
                mUpdateLookTotalAsyncTask.execute(shaiPkId,(lookTotal+1));

            }
        });
        mGetShaiDetailAsyncTask.execute(shaiPkId);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGetShaiDetailAsyncTask!=null){
            mGetShaiDetailAsyncTask = null;
        }

        if(mUpdateLookTotalAsyncTask!=null){
            mUpdateLookTotalAsyncTask = null;
        }

        if(mDeleteShaiAsyncTask != null){
            mDeleteShaiAsyncTask = null;
        }

        if(mDeleteCommentAsyncTask !=null){
            mDeleteCommentAsyncTask = null;
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_comment);
        mUserName = (TextView) findViewById(R.id.id_tv_user_name_item);
        mCommentTotal = (TextView) findViewById(R.id.id_comment_total);
        mMore = (LinearLayout) findViewById(R.id.id_ll_more);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.ct_back);
        mUserIcon = (CircleImageView) findViewById(R.id.id_civ_icon_item);
        mShaImageView = (ImageView) findViewById(R.id.id_iv_shai_item);
        mShaiContent = (TextView) findViewById(R.id.id_tv_desc_item);
        mShaiTime = (TextView) findViewById(R.id.id_tv_shai_time_item);
        mLookTotal = (TextView) findViewById(R.id.id_tv_look);
        mDelete = (TextView) findViewById(R.id.id_tv_delete);
        mNoComment = (TextView) findViewById(R.id.id_tv_no_comment);
        mCommentLayout = (LinearLayout) findViewById(R.id.id_ll_comment);
    }

    /**
     * 初始化监听
     */
    private void setListener(){
        mMore.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mCommentLayout.setOnClickListener(this);
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_ll_more:
                jumpActivity();
                //ToastUtils.toast(ShaiDetailActivity.this,"查看更多");
                break;
            case R.id.id_tv_delete://删除当前晒晒
                if(mDeleteShaiAsyncTask!=null){
                    return;
                }
                mDeleteShaiAsyncTask = new DeleteShaiAsyncTask(ShaiDetailActivity.this,new DeleteShaiAsyncTask.IDeleteShaiListener() {
                    @Override
                    public void result(int code) {
                        if(code == SUCCESS){
                            SpUtils.getEditor(ShaiDetailActivity.this).putInt("deleteShaiPosition",position).commit();
                            ToastUtils.toast(ShaiDetailActivity.this,getString(R.string.text_delete_success));
                            finish();
                        }else{
                            ToastUtils.toast(ShaiDetailActivity.this,getString(R.string.text_delete_failed));
                        }
                        if(mDeleteShaiAsyncTask != null){
                            mDeleteShaiAsyncTask = null;
                        }
                    }
                });
                mDeleteShaiAsyncTask.execute(shaiPkId);
                break;
            case R.id.id_ll_comment:
                String token = GetAuthorizationUtil.getAuth(this);
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this, LoginActivity.class);
                }else{
                    jumpActivity();
                }
                break;
        }
    }



    //判断当前用户在此菜谱中是否还有评论
    private void isCurrentUserHaveComment() {
        if(mJudgeUserHaveShaiCommentAsyncTask!=null){
            return;
        }
        mJudgeUserHaveShaiCommentAsyncTask = new JudgeUserHaveShaiCommentAsyncTask(this);
        mJudgeUserHaveShaiCommentAsyncTask.setmListener(this);
        mJudgeUserHaveShaiCommentAsyncTask.execute(shaiPkId);
    }

    @Override
    public void hasComment(boolean has) {
        //没有了，需要移除
        if(!has){
            SpUtils.getEditor(this).putInt("userShaiNoComment",position).commit();
        }
        if(mJudgeUserHaveShaiCommentAsyncTask !=null){
            mJudgeUserHaveShaiCommentAsyncTask = null;
        }
    }




    //跳转到评论列表界面
    private void jumpActivity(){
        Intent intent = new Intent(this,CommentListActivity.class);
        intent.putExtra("flag","shai");
        intent.putExtra("shaiPkId",shaiPkId);
        intent.putExtra("commentTotal",commentTotal);
        intent.putExtra("shaiPos",position);
        startActivity(intent);
    }

    private int flag = 1;//删除的数量
    @Override
    public void delete(int position) {

        Comment comment = comments.get(position);
        comments.remove(position);

        commentTotal = commentTotal -flag;
        mCommentTotal.setText("评论("+commentTotal+")");
        if(commentTotal>6){
            mMore.setVisibility(View.VISIBLE);
        }else{
            mMore.setVisibility(View.GONE);
        }

        if(comments.size() == 0){
            mNoComment.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mNoComment.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        //删除评论
        //mAdapter.deleteComment(position);

        //flag ++;
        if(mDeleteCommentAsyncTask !=null){
            return;
        }

        mDeleteCommentAsyncTask = new DeleteCommentAsyncTask(this, new DeleteCommentAsyncTask.IDeleteShaiCommentListener() {
            @Override
            public void result(int code) {
                if(code == SUCCESS){
                    ToastUtils.toast(ShaiDetailActivity.this,getString(R.string.text_delete_success));
                    isCurrentUserHaveComment();
                    loadComments();//重新加载数据
                }else{
                    ToastUtils.toast(ShaiDetailActivity.this,getString(R.string.text_delete_failed));
                }

                if(mDeleteCommentAsyncTask !=null){
                    mDeleteCommentAsyncTask = null;
                }
            }
        });
        mDeleteCommentAsyncTask.execute(comment.getCommnetPkId());
    }

}
