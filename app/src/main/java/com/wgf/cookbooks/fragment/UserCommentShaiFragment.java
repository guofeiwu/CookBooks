package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.AddShaiActivity;
import com.wgf.cookbooks.activity.LoginActivity;
import com.wgf.cookbooks.activity.ShaiDetailActivity;
import com.wgf.cookbooks.activity.UserShaiActivity;
import com.wgf.cookbooks.adapter.ShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.adapter.UserShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.asynctask.GetUserCommentShaiAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetUserShaiAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.UpCommentAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
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
public class UserCommentShaiFragment extends Fragment implements  UserShaiDetailRecycleViewAdapter.IShaiClickListener{
    private RecycleDivider mRecycleDivider;
    private GetUserCommentShaiAsyncTask mGetUserCommentShaiAsyncTask;
    private RecyclerView mRecyclerViewShai;
    private UserShaiDetailRecycleViewAdapter mUserShaiDetailRecycleViewAdapter;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new SqliteDao(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_comment_shai, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
        initComment();
    }


    /**
     * 初始化监听
     */

    private void setListener() {
        mRecyclerViewShai.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isLoading && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mUserShaiDetailRecycleViewAdapter.getItemCount()) {

                    mUserShaiDetailRecycleViewAdapter.setLoadStatus(1);
                    isLoading = false;
                    if (mGetUserCommentShaiAsyncTask != null) {
                        return;
                    }
                    mGetUserCommentShaiAsyncTask = new GetUserCommentShaiAsyncTask(getActivity(),new GetUserCommentShaiAsyncTask.IGetUserCommentShaiListener() {
                        @Override
                        public void userCommentShais(List<Shai> lists) {
                            if (lists != null && lists.size()>0) {
                                shaiList.addAll(lists);
                                mUserShaiDetailRecycleViewAdapter.addMoreItem(lists);
                                isLoading = true;
                                if (mGetUserCommentShaiAsyncTask != null) {
                                    mGetUserCommentShaiAsyncTask = null;
                                    pageNo++;//显示下一页
                                }
                            } else {
                                mUserShaiDetailRecycleViewAdapter.setLoadStatus(0);
                                havaData = false;//已经无数据，无需提醒
                            }

                            if(mGetUserCommentShaiAsyncTask!=null){
                                mGetUserCommentShaiAsyncTask = null;
                            }
                        }
                    }

                    );
                    mGetUserCommentShaiAsyncTask.execute(pageNo);//开始加载第一页数据

                } else if (!isLoading && havaData && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mUserShaiDetailRecycleViewAdapter.getItemCount()) {
                    ToastUtils.toast(getActivity(), "加载晒晒中...");

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
        SoftInputUtils.hideSoftInput(getActivity());
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
        if (mGetUserCommentShaiAsyncTask != null) {
            return;
        }
        mGetUserCommentShaiAsyncTask = new GetUserCommentShaiAsyncTask(getActivity(),new GetUserCommentShaiAsyncTask.IGetUserCommentShaiListener() {
            @Override
            public void userCommentShais(List<Shai> lists) {

                if (lists != null && lists.size()>0) {

                    int vi = mRecyclerViewShai.getVisibility();
                    if(vi != View.VISIBLE){
                        //无数据提醒
                        mNoShai.setVisibility(View.GONE);
                        //设置为不可见
                        mRecyclerViewShai.setVisibility(View.VISIBLE);
                    }
                    shaiList.addAll(lists);
                    mRecycleDivider = new RecycleDivider(getActivity(), RecycleDivider.VERITCAL_LIST);
                    mUserShaiDetailRecycleViewAdapter = new UserShaiDetailRecycleViewAdapter(getActivity(), lists);
                    mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                    mRecyclerViewShai.setAdapter(mUserShaiDetailRecycleViewAdapter);
                    mRecyclerViewShai.scrollToPosition(0);
                    mUserShaiDetailRecycleViewAdapter.setmIShaiClickListener(UserCommentShaiFragment.this);

                    pageNo++;//刷新时显示下一页
                }else{
                    if (shaiList.size() == 0){
                        //无数据提醒
                        mNoShai.setVisibility(View.VISIBLE);
                        //设置为不可见
                        mRecyclerViewShai.setVisibility(View.GONE);
                    }
                }

                if (mGetUserCommentShaiAsyncTask != null) {
                    mGetUserCommentShaiAsyncTask = null;
                }
            }
        });
        mGetUserCommentShaiAsyncTask.execute(1);//开始加载第一页数据
    }




    /**
     * 初始化控件
     */
    private void initView(View view) {
        mRecyclerViewShai = (RecyclerView) view.findViewById(R.id.id_rv_shais);
        mSendComment = (TextView) view.findViewById(R.id.id_tv_send_comment);
        mCommentLayout = (LinearLayout) view.findViewById(R.id.id_ll_comment);
        mCommentContent = (EditText) view.findViewById(R.id.id_et_comment_content);
        mNoShai = (TextView) view.findViewById(R.id.id_tv_no_shai);
    }



    @Override
    public void like(int position) {
        String token = GetAuthorizationUtil.getAuth(getActivity());
        if (token != null) {
            mUserShaiDetailRecycleViewAdapter.flashItem(position);
        } else {
            IntentUtils.jump(getActivity(), LoginActivity.class);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//activity启动的时候输入法默认不开启
        if (mUserShaiDetailRecycleViewAdapter != null && GetAuthorizationUtil.getAuth(getActivity()) !=null) {
            mUserShaiDetailRecycleViewAdapter.flashLikeContent();
        }

        //删除时候回调
        int pos = SpUtils.getSharedPreferences(getActivity()).getInt("deleteShaiPosition",-1);//删除晒一晒，删除的位置
        if(pos != -1){
            shaiList.remove(pos);
            mUserShaiDetailRecycleViewAdapter.removeItem(pos);
            SpUtils.getEditor(getActivity()).putInt("deleteShaiPosition",-1).commit();

            if (shaiList.size() == 0){
                //无数据提醒
                mNoShai.setVisibility(View.VISIBLE);
                //设置为不可见
                mRecyclerViewShai.setVisibility(View.GONE);
            }
        }

        //添加时候回调
        int add = SpUtils.getSharedPreferences(getActivity()).getInt("addShai",0);//添加时候回调
        if(add != 0){
            initData();
            SpUtils.getEditor(getActivity()).putInt("addShai",0).commit();
        }

        int shaiPos = SpUtils.getSharedPreferences(getActivity()).getInt("userShaiNoComment", -1);
        if (shaiPos != -1) {
            //说明没有评论了
            shaiList.remove(shaiPos);
            mUserShaiDetailRecycleViewAdapter.removeItem(shaiPos);
            SpUtils.getEditor(getActivity()).putInt("userShaiNoComment", -1).commit();
            if(shaiList.size() == 0){
                mRecyclerViewShai.setVisibility(View.GONE);
                mNoShai.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public void comment(final int position) {
        //判断用户是否登录
        String token = GetAuthorizationUtil.getAuth(getActivity());
        if (TextUtils.isEmpty(token)) {
            //跳转到登录
            IntentUtils.jump(getActivity(), LoginActivity.class);
        } else {
            initComment();

            mCommentLayout.setVisibility(View.VISIBLE);//设置可见

            //EditText获取焦点并显示软键盘
            mCommentContent.setFocusable(true);
            mCommentContent.setFocusableInTouchMode(true);
            mCommentContent.requestFocus();
            SoftInputUtils.showSoftInput(getActivity());

            mSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    String content = mCommentContent.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtils.toast(getActivity(), "请输入要评论的内容");
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

                        mUpCommentAsyncTask = new UpCommentAsyncTask(getActivity(), new UpCommentAsyncTask.IUpCommentListener() {
                            @Override
                            public void commentSuccess(Comment comment) {
                                SoftInputUtils.hideSoftInput(getActivity());
                                ToastUtils.toast(getActivity(), getString(R.string.text_comment_success));
                                mCommentContent.setText("");
                                mCommentLayout.setVisibility(View.GONE);
                                if(mUpCommentAsyncTask!= null){
                                    mUpCommentAsyncTask = null;
                                }


                            }
                            @Override
                            public void commentFailed() {
                                SoftInputUtils.hideSoftInput(getActivity());
                                ToastUtils.toast(getActivity(),  getString(R.string.text_comment_failed));
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
        Intent intent = new Intent(getActivity(),ShaiDetailActivity.class);
        Shai shai = shaiList.get(position);
        intent.putExtra("shaiPkId",shai.getShaiPkId());
        intent.putExtra("position",position);
        startActivity(intent);
    }

}
