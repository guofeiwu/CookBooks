package com.wgf.cookbooks.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.GetShaiAsyncTask;
import com.wgf.cookbooks.clazz.UpCommentAsyncTask;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SoftInputUtils;
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
    private TextView mSendComment;
    private LinearLayout mCommentLayout;
    private EditText mCommentContent;
    private UpCommentAsyncTask mUpCommentAsyncTask;

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
                if (lists != null) {
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
                SoftInputUtils.hideSoftInput(ShaiActivity.this);
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
    }

    /**
     * 初始化评论 工作
     */
    private void initComment() {
        int visibility = mCommentLayout.getVisibility();
        if (visibility == View.VISIBLE) {
            mCommentLayout.setVisibility(View.GONE);
        }
        SoftInputUtils.hideSoftInput(ShaiActivity.this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_toolbar_back);
        mAddShai = (ImageView) findViewById(R.id.id_iv_shai);
        mRecyclerViewShai = (RecyclerView) findViewById(R.id.id_rv_shais);
        mSendComment = (TextView) findViewById(R.id.id_tv_send_comment);
        mCommentLayout = (LinearLayout) findViewById(R.id.id_ll_comment);
        mCommentContent = (EditText) findViewById(R.id.id_et_comment_content);
    }


    @Override
    public void like(int position) {
        String token = GetAuthorizationUtil.getAuth(ShaiActivity.this);
        if (token != null) {
            mShaiDetailRecycleViewAdapter.flashItem(position);
        } else {
            IntentUtils.jump(ShaiActivity.this, LoginActivity.class);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mShaiDetailRecycleViewAdapter != null) {
            mShaiDetailRecycleViewAdapter.flashLikeContent();
        }
    }


    @Override
    public void comment(final int position) {
        //判断用户是否登录
        String token = GetAuthorizationUtil.getAuth(ShaiActivity.this);
        if (TextUtils.isEmpty(token)) {
            //跳转到登录
            IntentUtils.jump(ShaiActivity.this, LoginActivity.class);
        } else {
            initComment();

            mCommentLayout.setVisibility(View.VISIBLE);//设置可见

            //EditText获取焦点并显示软键盘
            mCommentContent.setFocusable(true);
            mCommentContent.setFocusableInTouchMode(true);
            mCommentContent.requestFocus();
            SoftInputUtils.showSoftInput(ShaiActivity.this);

            mSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    String content = mCommentContent.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtils.toast(ShaiActivity.this, "请输入要评论的内容");
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

                        mUpCommentAsyncTask = new UpCommentAsyncTask(ShaiActivity.this, new UpCommentAsyncTask.IUpCommentListener() {
                            @Override
                            public void commentSuccess() {
                                SoftInputUtils.hideSoftInput(ShaiActivity.this);
                                ToastUtils.toast(ShaiActivity.this, "评论成功");
                                mCommentContent.setText("");
                                mCommentLayout.setVisibility(View.GONE);
                                if(mUpCommentAsyncTask!= null){
                                    mUpCommentAsyncTask = null;
                                }


                            }
                            @Override
                            public void commentFailed() {
                                SoftInputUtils.hideSoftInput(ShaiActivity.this);
                                ToastUtils.toast(ShaiActivity.this, "评论失败");
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
        //IntentUtils.jump(ShaiActivity.this,ShaiDetailActivity.class);
        Intent intent = new Intent(ShaiActivity.this,ShaiDetailActivity.class);
        Shai shai = shaiList.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shai",shai);
//        intent.putExtras(bundle);
        intent.putExtra("shaiPkId",shai.getShaiPkId());
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShaiDetailRecycleViewAdapter != null) {
            mShaiDetailRecycleViewAdapter = null;
        }

        if(mUpCommentAsyncTask!= null){
            mUpCommentAsyncTask = null;
        }
    }
}
