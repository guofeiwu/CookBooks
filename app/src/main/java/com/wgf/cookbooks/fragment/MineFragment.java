package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.LoginActivity;
import com.wgf.cookbooks.activity.MyMenuStepActivity;
import com.wgf.cookbooks.activity.MyPointActivity;
import com.wgf.cookbooks.activity.SystemSettingActivity;
import com.wgf.cookbooks.activity.UserCollectMenuActivity;
import com.wgf.cookbooks.activity.UserCommentMenuShaiActivity;
import com.wgf.cookbooks.activity.UserInfoActivity;
import com.wgf.cookbooks.activity.UserMenuActivity;
import com.wgf.cookbooks.activity.UserShaiActivity;
import com.wgf.cookbooks.clazz.asynctask.GetUserMenuAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.MineLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.NOT_SIGN;
import static com.wgf.cookbooks.util.Constants.SHOW_DATA;
import static com.wgf.cookbooks.util.Constants.SIGN;
import static com.wgf.cookbooks.util.Constants.SUCCESS;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    private MineLayout mInfo,mComent,mPoint,mRecord;
    private ImageView mSetting;
    private TextView mLoginOrRegister,mSign;
    private LinearLayout mMenu,mCollect,mAlbum;
    private GetUserInfoAsyncTask mGetUserInfoAsyncTask;
    private CircleImageView mCircleImageView;
    private JudgeUserSignAsyncTask mJudgeUserSignAsyncTask;

    private JSONObject userInfo = null;//存放用户信息
    private String userName,phone,icon,sign;
    private SqliteDao dao;
    private int point;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        SwitchAnimationUtils.enterActivitySlideRight(getActivity());
//        SwitchAnimationUtils.exitActivitySlideLeft(getActivity());
        super.onCreate(savedInstanceState);
        dao = new SqliteDao(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        SpUtils.getEditor(getActivity()).putBoolean(SHOW_DATA,false).commit();//第一次加载数据

        initView(view);
        setListener();

        return view;
    }


    /**
     * 绑定控件
     * @param view
     */
    private void initView(View view) {

        mLoginOrRegister = (TextView) view.findViewById(R.id.id_tv_login_or_register);
        mSign = (TextView) view.findViewById(R.id.id_sign);

        mMenu = (LinearLayout) view.findViewById(R.id.id_ll_menu);
        mCollect = (LinearLayout) view.findViewById(R.id.id_ll_collect);
        mAlbum = (LinearLayout) view.findViewById(R.id.id_ll_album);


        mSetting = (ImageView) view.findViewById(R.id.id_iv_setting);
        mInfo = (MineLayout) view.findViewById(R.id.ml_info);
        mComent = (MineLayout) view.findViewById(R.id.ml_comment);
        mPoint = (MineLayout) view.findViewById(R.id.ml_point);
        mRecord = (MineLayout) view.findViewById(R.id.ml_record);

        mCircleImageView = (CircleImageView) view.findViewById(R.id.civ_user_icon);
    }


    /**
     * 添加监听
     */
    private void setListener(){
        mLoginOrRegister.setOnClickListener(this);
        mSign.setOnClickListener(this);

        mMenu.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mAlbum.setOnClickListener(this);

        mSetting.setOnClickListener(this);
        mInfo.setOnClickListener(this);
        mComent.setOnClickListener(this);
        mPoint.setOnClickListener(this);
        mRecord.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String token = null;
        switch (v.getId()){
            case R.id.id_tv_login_or_register:
                token =  GetAuthorizationUtil.getAuth(getActivity());
                if(token == null){//未登录，跳转到登录界面
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{//用户信息显示界面
                    // TODO: 2017/9/30 用户信息详情界面
                    ToastUtils.toast(getActivity(),"已登录");
                }
                break;
            case R.id.id_sign://签到
                if(sign.equals(NOT_SIGN)){
                    //调用签到接口
                    if(mJudgeUserSignAsyncTask!=null){
                        return;
                    }
                    mJudgeUserSignAsyncTask = new JudgeUserSignAsyncTask();
                    // 2 表示是进行签到
                    mJudgeUserSignAsyncTask.execute(2);
                }else{
                    return;
                }
                break;
            case R.id.id_ll_menu:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(this.getActivity(), UserMenuActivity.class);
                }
                break;
            //收藏
            case R.id.id_ll_collect:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(this.getActivity(), UserCollectMenuActivity.class);
                }
                break;
            case R.id.id_ll_album:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(this.getActivity(), UserShaiActivity.class);
                }
                break;
            //我的资料
            case R.id.ml_info:
                IntentUtils.jump(this.getActivity(), UserInfoActivity.class);
                break;
            case R.id.ml_comment:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(this.getActivity(), UserCommentMenuShaiActivity.class);
                }
                break;
            case R.id.ml_point:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    Intent intent = new Intent(getActivity(),MyPointActivity.class);
                    intent.putExtra("userPoint",point);
                    startActivity(intent);
                }
                break;
            case R.id.ml_record:
                token = GetAuthorizationUtil.getAuth(getActivity());
                if(TextUtils.isEmpty(token)){
                    IntentUtils.jump(this.getActivity(), LoginActivity.class);
                }else{
                    IntentUtils.jump(this.getActivity(), MyMenuStepActivity.class);
                }
                break;
            //系统设置
            case R.id.id_iv_setting:
                IntentUtils.jump(this.getActivity(),SystemSettingActivity.class);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {//不是hidden
            getUserInfo();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        SoftInputUtils.hideSoftInput(getActivity());
        getUserInfo();
    }

    /**
     * 根据当前状态判断是不是要获取用户信息
     * 若需要则返回
     */
    public void getUserInfo(){
        //判断用户是否登录
        SharedPreferences sp = SpUtils.getSharedPreferences(getActivity());
        String token  = sp.getString(AUTHORIZATION,null);
        if(!TextUtils.isEmpty(token)){
            boolean isShow = sp.getBoolean(SHOW_DATA,false);//未加载数据
            if(!isShow){
                SpUtils.getEditor(getActivity()).putBoolean(SHOW_DATA,true).commit();
                //界面显示数据
                mInfo.setVisibility(View.VISIBLE);//我的资料显示

                //从服务器获取用户的数据
                if(mGetUserInfoAsyncTask != null){
                    return;
                }
                mGetUserInfoAsyncTask = new GetUserInfoAsyncTask();
                mGetUserInfoAsyncTask.execute(token);
            }

            if(mJudgeUserSignAsyncTask!=null){
                return;
            }
            mJudgeUserSignAsyncTask = new JudgeUserSignAsyncTask();
            // 1 表示是判断是否已经签到
            mJudgeUserSignAsyncTask.execute(1);
        }else {//未登录
            mLoginOrRegister.setText(getString(R.string.text_click_login_register));
            mSign.setVisibility(View.GONE);
            mInfo.setVisibility(View.GONE);
            mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_108));//设置默认头像
        }
    }


    /**
     * 获取用户信息
     */
    public class GetUserInfoAsyncTask extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... params) {

            String url =BASE_URL+"/app/user";
            try {
                Response response = OkGo.<String>get(url)
                        .headers(AUTHORIZATION,params[0])
                        .execute();

                String resJson = response.body().string();
                int code = JsonUtils.getCode(resJson);
                if(code == SUCCESS){
                    userInfo = JsonUtils.getContent(resJson);//获取用户信息
                    return SUCCESS;
                }else{
                    return FAILED;//获取用户信息失败
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mGetUserInfoAsyncTask = null;
            if(integer == SUCCESS){
                //设置用户信息
                try {
                    userName = userInfo.getString("name");
                    phone = userInfo.getString("phone");
                    icon = userInfo.getString("icon");
                    point = userInfo.getInt("point");
                    mLoginOrRegister.setText(userName);
                    //加载用户头像
                    Glide.with(MineFragment.this).load(BASE_URL_FILE_ICON+icon).into(mCircleImageView);
                    dao.deleteUserInfo();
                    dao.insertUserInfo(userName,icon,phone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                ToastUtils.toast(getActivity(),getString(R.string.text_failed_msg));
            }
        }
    }


    /**
     * 判断用户是否签到和进行签到
     */
    public class JudgeUserSignAsyncTask extends AsyncTask<Integer,Void,Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            int sign = params[0];
            String url = BASE_URL+"/app/user/sign/"+sign;
            String token = SpUtils.getSharedPreferences(getActivity()).getString(AUTHORIZATION,null);
            try {
                Response response = OkGo.<String>get(url)
                        .headers(AUTHORIZATION, token)
                        .execute();
                    String resJson = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resJson);
                        JSONObject jo = jsonObject.getJSONObject("extend").getJSONObject("content");
                        sign = jo.getInt("sign");
                        point = jo.getInt("point");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                return sign;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mJudgeUserSignAsyncTask = null;
            //1 已经签到
            if(integer == 1){
                mSign.setVisibility(View.VISIBLE);
                sign = SIGN;
                mSign.setText(sign);
                //2 表示还未签到
            }else if(integer == 2){
                mSign.setVisibility(View.VISIBLE);
                sign = NOT_SIGN;
                mSign.setText(sign);
                //3 表示签到成功
            }else if (integer == 3){
                mSign.setVisibility(View.VISIBLE);
                sign = SIGN;
                mSign.setText(sign);
                ToastUtils.toast(getActivity(),getString(R.string.text_mine_sign_success));
                //4 表示签到失败
            }else if(integer == 4){
                mSign.setVisibility(View.VISIBLE);
                sign = NOT_SIGN;
                mSign.setText(sign);
                ToastUtils.toast(getActivity(),getString(R.string.text_mine_sign_failed));
            }else{
                //其他的就是系统异常
                ToastUtils.toast(getActivity(),getString(R.string.text_failed_msg));
            }
        }
    }






}
