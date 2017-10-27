package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.asynctask.GetUserShaiAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.UpCommentAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.SwitchAnimationUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UserShaiActivity extends AppCompatActivity implements ShaiDetailRecycleViewAdapter.IShaiClickListener{
    private CustomToolbar mCustomToolbar;
    private RecycleDivider mRecycleDivider;
    private GetUserShaiAsyncTask mGetUserShaiAsyncTask;
    private RecyclerView mRecyclerViewShai;
    private ShaiDetailRecycleViewAdapter mShaiDetailRecycleViewAdapter;
    private boolean isLoading = true;//正在加载
    private int pageNo = 1;//加载第几页
    private boolean havaData = true;//有数据
    private List<Shai> shaiList;
    private TextView mSendComment;
    private LinearLayout mCommentLayout;
    private EditText mCommentContent;
    private UpCommentAsyncTask mUpCommentAsyncTask;
    private List<String> head;
    private SqliteDao dao;
    private TextView mNoShai;
    private ImageView mAddShai;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwitchAnimationUtils.exitActivitySlideLeft(this);
        dao = new SqliteDao(this);

        setContentView(R.layout.activity_user_shai);

        initView();

        initData();

        setListener();
    }

    /**
     * 初始化显示的数据
     */
    private void initData() {
        //初始化页数
        pageNo = 1;
        if(head!= null){
            head.clear();
            head = null;
        }
        List<String> heads = dao.queryUserInfo();
        if(heads!=null){
            head = new ArrayList<>();
            head.add(heads.get(1));
            head.add("2017，我和美食的故事");
        }

        if(shaiList!=null){
            shaiList.clear();
            shaiList = null;
        }
        shaiList = new ArrayList<>();
        if (mGetUserShaiAsyncTask != null) {
            return;
        }
        mGetUserShaiAsyncTask = new GetUserShaiAsyncTask(UserShaiActivity.this,new GetUserShaiAsyncTask.IGetUserShaiListener() {
            @Override
            public void getShaiList(List<Shai> lists) {

                if (lists != null && lists.size()>0) {

                    int vi = mRecyclerViewShai.getVisibility();
                    if(vi != View.VISIBLE){
                        //无数据提醒
                        mNoShai.setVisibility(View.GONE);
                        //设置为不可见
                        mRecyclerViewShai.setVisibility(View.VISIBLE);
                    }
                    shaiList.addAll(lists);
                    mRecycleDivider = new RecycleDivider(UserShaiActivity.this, RecycleDivider.VERITCAL_LIST);
                    mShaiDetailRecycleViewAdapter = new ShaiDetailRecycleViewAdapter(UserShaiActivity.this, lists,head);
                    mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(UserShaiActivity.this));
                    mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                    mRecyclerViewShai.setAdapter(mShaiDetailRecycleViewAdapter);
                    mRecyclerViewShai.scrollToPosition(0);
                    mShaiDetailRecycleViewAdapter.setmIShaiClickListener(UserShaiActivity.this);

                    pageNo++;//刷新时显示下一页
                }else{
                    if (shaiList.size() == 0){
                        //无数据提醒
                        mNoShai.setVisibility(View.VISIBLE);
                        //设置为不可见
                        mRecyclerViewShai.setVisibility(View.GONE);
                    }
                }

                if (mGetUserShaiAsyncTask != null) {
                    mGetUserShaiAsyncTask = null;
                }
            }
        });
        mGetUserShaiAsyncTask.execute(1);//开始加载第一页数据
    }


    /**
     * 初始化监听
     */

    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                SoftInputUtils.hideSoftInput(UserShaiActivity.this);
                finish();
            }
        });

        mRecyclerViewShai.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mShaiDetailRecycleViewAdapter.getItemCount()) {

                    mShaiDetailRecycleViewAdapter.setLoadStatus(1);
                    isLoading = false;
                    if (mGetUserShaiAsyncTask != null) {
                        return;
                    }
                    mGetUserShaiAsyncTask = new GetUserShaiAsyncTask(UserShaiActivity.this,new GetUserShaiAsyncTask.IGetUserShaiListener() {
                        @Override
                        public void getShaiList(List<Shai> lists) {
                            if (lists != null && lists.size()>0) {
                                shaiList.addAll(lists);
                                mShaiDetailRecycleViewAdapter.addMoreItem(lists);
                                isLoading = true;
                                if (mGetUserShaiAsyncTask != null) {
                                    mGetUserShaiAsyncTask = null;
                                    pageNo++;//显示下一页
                                }
                            } else {
                                mShaiDetailRecycleViewAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                            }

                            if(mGetUserShaiAsyncTask!=null){
                                mGetUserShaiAsyncTask = null;
                            }
                        }
                    }

                    );
                    mGetUserShaiAsyncTask.execute(pageNo);//开始加载第一页数据

                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mShaiDetailRecycleViewAdapter.getItemCount()) {
                    ToastUtils.toast(UserShaiActivity.this, "加载晒晒中...");

                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    initComment();
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

        //添加晒一晒
        mAddShai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.jump(UserShaiActivity.this,AddShaiActivity.class);
            }
        });
    }

    /**
     * 初始化评论 工作
     */
    private void initComment() {
        int visibility = mCommentLayout.getVisibility();
        if (visibility == View.VISIBLE) {
            mCommentLayout.setVisibility(View.GONE);
        }
        SoftInputUtils.hideSoftInput(UserShaiActivity.this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_user_album_back);
        mRecyclerViewShai = (RecyclerView) findViewById(R.id.id_rv_shais);
        mSendComment = (TextView) findViewById(R.id.id_tv_send_comment);
        mCommentLayout = (LinearLayout) findViewById(R.id.id_ll_comment);
        mCommentContent = (EditText) findViewById(R.id.id_et_comment_content);
        mNoShai = (TextView) findViewById(R.id.id_tv_no_shai);
        mAddShai = (ImageView) findViewById(R.id.id_iv_shai);
    }


    @Override
    public void like(int position) {
        String token = GetAuthorizationUtil.getAuth(UserShaiActivity.this);
        if (token != null) {
            mShaiDetailRecycleViewAdapter.flashItem(position);
        } else {
            IntentUtils.jump(UserShaiActivity.this, LoginActivity.class);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//activity启动的时候输入法默认不开启
        if (mShaiDetailRecycleViewAdapter != null && GetAuthorizationUtil.getAuth(UserShaiActivity.this) !=null) {
            mShaiDetailRecycleViewAdapter.flashLikeContent();
        }

        //删除时候回调
        int pos = SpUtils.getSharedPreferences(this).getInt("deleteShaiPosition",-1);//删除晒一晒，删除的位置
        if(pos != -1){
            shaiList.remove(pos);
            mShaiDetailRecycleViewAdapter.removeItem(pos);
            SpUtils.getEditor(this).putInt("deleteShaiPosition",-1).commit();

            if (shaiList.size() == 0){
                //无数据提醒
                mNoShai.setVisibility(View.VISIBLE);
                //设置为不可见
                mRecyclerViewShai.setVisibility(View.GONE);
            }
        }

        //添加时候回调
        int add = SpUtils.getSharedPreferences(this).getInt("addShai",0);//添加时候回调
        if(add != 0){
            initData();
            SpUtils.getEditor(this).putInt("addShai",0).commit();
        }
    }


    @Override
    public void comment(final int position) {
        //判断用户是否登录
        String token = GetAuthorizationUtil.getAuth(UserShaiActivity.this);
        if (TextUtils.isEmpty(token)) {
            //跳转到登录
            IntentUtils.jump(UserShaiActivity.this, LoginActivity.class);
        } else {
            initComment();

            mCommentLayout.setVisibility(View.VISIBLE);//设置可见

            //EditText获取焦点并显示软键盘
            mCommentContent.setFocusable(true);
            mCommentContent.setFocusableInTouchMode(true);
            mCommentContent.requestFocus();
            SoftInputUtils.showSoftInput(UserShaiActivity.this);

            mSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    String content = mCommentContent.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtils.toast(UserShaiActivity.this, "请输入要评论的内容");
                    } else {

                        //获取评论的晒晒的主键
                        int shaiPkId = shaiList.get(position).getShaiPkId();

                        //将评论内容上传到服务器
                        Map<String,Object> map = new HashMap<>();
                        map.put("shaiPkId",shaiPkId);
                        map.put("content",content);
                        JSONObject jsonObject = new JSONObject(map);

                        if(mUpCommentAsyncTask!= null){
                            return;
                        }

                        mUpCommentAsyncTask = new UpCommentAsyncTask(UserShaiActivity.this, new UpCommentAsyncTask.IUpCommentListener() {
                            @Override
                            public void commentSuccess(Comment comment) {
                                SoftInputUtils.hideSoftInput(UserShaiActivity.this);
                                ToastUtils.toast(UserShaiActivity.this, getString(R.string.text_comment_success));
                                mCommentContent.setText("");
                                mCommentLayout.setVisibility(View.GONE);
                                if(mUpCommentAsyncTask!= null){
                                    mUpCommentAsyncTask = null;
                                }


                            }
                            @Override
                            public void commentFailed() {
                                SoftInputUtils.hideSoftInput(UserShaiActivity.this);
                                ToastUtils.toast(UserShaiActivity.this,  getString(R.string.text_comment_failed));
                                if(mUpCommentAsyncTask!= null){
                                    mUpCommentAsyncTask = null;
                                }
                            }
                        });

                        String url = BASE_URL+"/app/shai/comment";
                        mUpCommentAsyncTask.execute(jsonObject.toString(),url);

                    }
                }
            });
        }
    }


    @Override
    public void detail(int position) {
        initComment();
        //显示晒晒详情
        Intent intent = new Intent(UserShaiActivity.this,ShaiDetailActivity.class);
        Shai shai = shaiList.get(position);
        intent.putExtra("shaiPkId",shai.getShaiPkId());
        intent.putExtra("position",position);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUpCommentAsyncTask!= null){
            mUpCommentAsyncTask = null;
        }
    }
}
