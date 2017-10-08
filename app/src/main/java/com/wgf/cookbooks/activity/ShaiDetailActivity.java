package com.wgf.cookbooks.activity;

import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiCommentRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.GetCommentAsyncTask;
import com.wgf.cookbooks.clazz.GetShaiDetailAsyncTask;
import com.wgf.cookbooks.clazz.UpdateLookTotalAsyncTask;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_SHAI;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒详情
 */
public class ShaiDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView mRecyclerView;
    private CustomToolbar mCustomToolbar;
    private List<Comment> comments;
    private ShaiCommentRecycleViewAdapter mAdapter;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private int shaiPkId;
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
        shaiPkId = getIntent().getIntExtra("shaiPkId",0);
        if(mGetShaiDetailAsyncTask!=null){
            return;
        }else{
            initShaiDetail();
        }

        comments = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleDivider = new RecycleDivider(ShaiDetailActivity.this,RecycleDivider.VERITCAL_LIST);
        mRecyclerView.addItemDecoration(mRecycleDivider);


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
        mGetCommentAsyncTask.execute(shaiPkId,1);
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

                int commentTotal = shai.getCommentTotal();
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
    }

    /**
     * 初始化监听
     */
    private void setListener(){
        mMore.setOnClickListener(this);

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
                ToastUtils.toast(ShaiDetailActivity.this,"查看更多");
                break;
        }
    }
}
