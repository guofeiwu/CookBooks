package com.wgf.cookbooks.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.CommentRecycleViewAdapter;
import com.wgf.cookbooks.adapter.MenuStepAdapter;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Materials;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.clazz.asynctask.CollectMenuAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.DeleteCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.GetCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.JudgeUserHaveMenuCommentAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.LikeMenuAsyncTask;
import com.wgf.cookbooks.clazz.asynctask.MenuDetailAsyncTask;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.RecycleDivider;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.util.WxUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.CustomToolbar;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 菜谱详细界面
 */
public class MenuDetailActivity extends AppCompatActivity implements MenuDetailAsyncTask.IMenuDetailListener, GetCommentAsyncTask.ICommentListener,
        CommentRecycleViewAdapter.ICommentDeleteListener, View.OnClickListener, LikeMenuAsyncTask.ILikeMenuListener,
        CollectMenuAsyncTask.ICollectMenuListener,JudgeUserHaveMenuCommentAsyncTask.IUserMenuHasCommentMenuListener {

    private LinearLayout mMaterialsDose;//食材及用量
    private MenuDetailAsyncTask mMenuDetailAsyncTask;
    private CustomToolbar mCustomToolbar;
    private ImageView mMainIcon;
    private CircleImageView muserIcon;
    private TextView mUserName;
    private TextView mIntroduce;
    private RecyclerView mRecyclerView;
    private MenuStepAdapter mMenuStepAdapter;
    private RecycleDivider mRecycleDivider;
    private TextView commentTotal;//评论总数
    private RecyclerView mRecyclerViewComment;
    private TextView noComment;//无评论
    private LinearLayout readMoreComment;//查看更多评论
    private LinearLayout likeLayout, collectLayout, commentLayout;
    private ImageView likeImageView, colloectImageView;
    private TextView likeNumber, collectNumber;
    private GetCommentAsyncTask mGetCommentAsyncTask;
    private List<Comment> comments;//菜谱对应的评论
    private CommentRecycleViewAdapter mAdapter;
    private int commentSize;//评论总数
    private DeleteCommentAsyncTask mDeleteCommentAsyncTask;
    private LikeMenuAsyncTask mLikeMenuAsyncTask;
    private CollectMenuAsyncTask mCollectMenuAsyncTask;
    private int menuPkid = -1;//菜谱主键
    private int currentLike = -1;//当前用户点赞
    private int likeSize;//点赞总数
    private int likePkId = -1;//点赞主键
    private int collectPkId = -1;//收藏主键

    private int currentCollect = -1;//当前用户收藏
    private int collectSize;//收藏总数
    private ImageView mShare;

    // TODO: 2017/10/17 添加微信分享功能
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    //菜谱在列表中的位置
    private int pos = -1;

    private JudgeUserHaveMenuCommentAsyncTask mJudgeUserHaveMenuCommentAsyncTask;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        api = WxUtils.register(this);
        Intent intent = getIntent();
        menuPkId = intent.getIntExtra("menuPkId", 0);
        pos = intent.getIntExtra("menuPos", -1);
        initView();
        initData();
        setListener();
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        readMoreComment.setOnClickListener(this);
        likeLayout.setOnClickListener(this);
        collectLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        mShare.setOnClickListener(this);
    }

    private int menuPkId = 0;


    /**
     * 初始化数据
     */
    private void initData() {
        if (mMenuDetailAsyncTask != null) {
            return;
        }
        mMenuDetailAsyncTask = new MenuDetailAsyncTask(this);
        mMenuDetailAsyncTask.setmListener(this);
        mMenuDetailAsyncTask.execute(menuPkId);
    }

    /**
     * 绑定控件
     */
    private void initView() {

        mMaterialsDose = (LinearLayout) findViewById(R.id.id_ll_materials_dose);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.ct_back);
        mMainIcon = (ImageView) findViewById(R.id.id_iv_main_icon);
        muserIcon = (CircleImageView) findViewById(R.id.id_civ_icon_item);
        mUserName = (TextView) findViewById(R.id.id_tv_user_name_item);
        mIntroduce = (TextView) findViewById(R.id.id_tv_menu_introduce);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_rv_step);


        commentTotal = (TextView) findViewById(R.id.id_comment_total);
        mRecyclerViewComment = (RecyclerView) findViewById(R.id.id_rv_comment);
        noComment = (TextView) findViewById(R.id.id_tv_no_comment);
        readMoreComment = (LinearLayout) findViewById(R.id.id_ll_more);
        likeLayout = (LinearLayout) findViewById(R.id.id_ll_like);
        collectLayout = (LinearLayout) findViewById(R.id.id_ll_collect);
        commentLayout = (LinearLayout) findViewById(R.id.id_ll_comment);
        likeImageView = (ImageView) findViewById(R.id.id_menu_like);
        colloectImageView = (ImageView) findViewById(R.id.id_menu_collect);


        likeNumber = (TextView) findViewById(R.id.id_tv_menu_like);
        collectNumber = (TextView) findViewById(R.id.id_tv_menu_collect);

        //share
        mShare = (ImageView) findViewById(R.id.id_iv_share);
    }


    private String mainIcon;
    private String menuName;
    private String menuDesc;

    @Override
    public void result(Menu menu) {
        if (menu == null) {
            ToastUtils.toast(this, "系统或网络出错");
            return;
        }
        menuPkid = menu.getMenuPkId();
        mainIcon = menu.getMainIcon();
        Glide.with(this).load(BASE_URL_FILE_MENUS + mainIcon).into(mMainIcon);
        String userIconUrl = menu.getUserIconUrl();
        Glide.with(this).load(BASE_URL_FILE_ICON + userIconUrl).into(muserIcon);
        menuName = menu.getMenuName();
        menuDesc = menu.getIntroduce();
        mUserName.setText(menu.getUserName());
        mIntroduce.setText(menuDesc);

        //食材
        List<Materials> materialses = menu.getMaterials();
        for (Materials materials : materialses) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 15;
            layoutParams.rightMargin = 15;
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(layoutParams);

            //textView
            TextView dose = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 75);
            dose.setGravity(Gravity.CENTER);
            dose.setLayoutParams(textParams);
            dose.setText(materials.getMaterialsName());

            //view
            View view = new View(this);
            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
            viewParams.topMargin = 10;
            viewParams.bottomMargin = 10;
            view.setBackgroundColor(getResources().getColor(R.color.item_gray));
            view.setLayoutParams(viewParams);
            layout.addView(dose);
            layout.addView(view);

            mMaterialsDose.addView(layout);
        }

        mRecycleDivider = new RecycleDivider(this, RecycleDivider.VERITCAL_LIST);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        //评论列表recycleView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerViewComment.setLayoutManager(layoutManager);
        mRecyclerViewComment.setHasFixedSize(true);
        mRecyclerViewComment.setNestedScrollingEnabled(false);
        mRecyclerViewComment.addItemDecoration(mRecycleDivider);


        //步骤
        if (mMenuStepAdapter == null) {
            mMenuStepAdapter = new MenuStepAdapter(this, menu.getSteps());
            mRecyclerView.setAdapter(mMenuStepAdapter);
        } else {
            mRecyclerView.setAdapter(mMenuStepAdapter);
        }


        //评论总数
        commentSize = menu.getCommentTotal();
        commentTotal.setText("评论(" + commentSize + ")");
        if (commentSize == 0) {
            noComment.setVisibility(View.VISIBLE);
        } else {
            //有评论 加载评论
            noComment.setVisibility(View.GONE);
            loadComments();
            if (commentSize > 6) {
                readMoreComment.setVisibility(View.VISIBLE);
            } else {
                readMoreComment.setVisibility(View.GONE);
            }
        }

        currentLike = menu.getCurrentLike();
        if (currentLike == 0) {//是当前用户收藏
            likeImageView.setImageResource(R.drawable.like_32);
        } else {
            likeImageView.setImageResource(R.drawable.not_like_32);
        }


        currentCollect = menu.getCurrentCollect();
        if (currentCollect == 0) {
            colloectImageView.setImageResource(R.drawable.collect_red_32);
        } else {
            colloectImageView.setImageResource(R.drawable.collect_gray_32);
        }


        collectSize = menu.getCollectTotal();
        if (collectSize == 0) {
            collectNumber.setText("收藏");
        } else {
            collectNumber.setText(collectSize + "");
        }

        likeSize = menu.getLikeTotal();
        if (likeSize == 0) {
            likeNumber.setText("赞");
        } else {
            likeNumber.setText(likeSize + "");
        }

        likePkId = menu.getLikePkId();
        collectPkId = menu.getCollectPkId();


        if (mMenuDetailAsyncTask != null) {
            mMenuDetailAsyncTask = null;
        }
    }


    /**
     * 初始化评论
     */
    private void loadComments() {
        if (comments != null) {
            comments.clear();
            comments = null;
        }
        comments = new ArrayList<>();
        if (mGetCommentAsyncTask != null) {
            return;
        }
        mGetCommentAsyncTask = new GetCommentAsyncTask(this, null);
        mGetCommentAsyncTask.setmListener(this);
        mGetCommentAsyncTask.execute(menuPkid, 1, 100);//只显示第一页,100标识是获取菜谱评论
    }


    //获取评论的回调
    @Override
    public void success(List<Comment> commentList) {
        if (mAdapter != null) {
            mAdapter = null;//之前的adapter置为空
        }
        //成功了就是有数据，没数据的一律回调 fail() 方法
        comments.addAll(commentList);
        mAdapter = new CommentRecycleViewAdapter(this, commentList);
        mAdapter.setmListener(this);
        mRecyclerViewComment.setAdapter(mAdapter);

        setVisibility();

        if (mGetCommentAsyncTask != null) {
            mGetCommentAsyncTask = null;
        }

    }

    @Override
    public void fail(int result) {
        if (result == 0) {
            ToastUtils.toast(this, "暂无更多评论");
        } else {
            ToastUtils.toast(this, "获取评论失败");
        }
        if (mGetCommentAsyncTask != null) {
            mGetCommentAsyncTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMenuStepAdapter != null) {
            mMenuStepAdapter = null;
        }

        if (mMenuDetailAsyncTask != null) {
            mMenuDetailAsyncTask = null;
        }

        if (mGetCommentAsyncTask != null) {
            mGetCommentAsyncTask = null;
        }
    }

    //删除评论回调
    @Override
    public void delete(int position) {

        Comment comment = comments.get(position);
        comments.remove(position);//移除

        int commnetPkId = comment.getCommnetPkId();
        commentSize -= 1;
        commentTotal.setText("评论(" + commentSize + ")");
        if (commentSize > 6) {
            readMoreComment.setVisibility(View.VISIBLE);
        } else {
            readMoreComment.setVisibility(View.GONE);
        }

        //无评论
        if (comments.size() == 0) {
            noComment.setVisibility(View.VISIBLE);
            mRecyclerViewComment.setVisibility(View.GONE);
        } else {
            noComment.setVisibility(View.GONE);
            mRecyclerViewComment.setVisibility(View.VISIBLE);
        }

        //删除评论
        if (mDeleteCommentAsyncTask != null) {
            return;
        }
        mDeleteCommentAsyncTask = new DeleteCommentAsyncTask(this, new DeleteCommentAsyncTask.IDeleteShaiCommentListener() {
            @Override
            public void result(int code) {
                if (code == SUCCESS) {
                    ToastUtils.toast(MenuDetailActivity.this, getString(R.string.text_delete_success));
                    loadComments();//重新加载数据
                    isCurrentUserHaveComment();

                } else {
                    ToastUtils.toast(MenuDetailActivity.this, getString(R.string.text_delete_failed));
                }

                if (mDeleteCommentAsyncTask != null) {
                    mDeleteCommentAsyncTask = null;
                }
            }
        });
        mDeleteCommentAsyncTask.execute(commnetPkId, 100);
    }

    //判断当前用户在此菜谱中是否还有评论
    private void isCurrentUserHaveComment() {
        if(mJudgeUserHaveMenuCommentAsyncTask !=null){
            return;
        }
        mJudgeUserHaveMenuCommentAsyncTask = new JudgeUserHaveMenuCommentAsyncTask(this);
        mJudgeUserHaveMenuCommentAsyncTask.setmListener(this);
        mJudgeUserHaveMenuCommentAsyncTask.execute(menuPkid);
    }

    //当前用户在此菜谱中是否还有评论
    @Override
    public void hasComment(boolean has) {
        //没有了，需要移除
        if(!has){
            SpUtils.getEditor(this).putInt("userMenuNoComment",pos).commit();
        }
        if(mJudgeUserHaveMenuCommentAsyncTask !=null){
            mJudgeUserHaveMenuCommentAsyncTask = null;
        }
    }



    @Override
    public void onClick(View v) {
        String url = null;
        String token = null;
        switch (v.getId()) {
            case R.id.id_ll_more://查看更多评论
                intentCommnetList();
                break;
            case R.id.id_ll_like://点赞
                //用户未登录，则进行登录
                token = GetAuthorizationUtil.getAuth(this);
                if (TextUtils.isEmpty(token)) {
                    //IntentUtils.jump(this,LoginActivity.class);
                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.putExtra("menuDetail", "menuDetail");
                    startActivity(intent1);
                    return;
                }
                if (currentLike == 0) {//是当前用户点赞
                    //则取消点赞
                    url = BASE_URL + "/app/menu/dislike/" + likePkId;
                } else {
                    //进行点赞
                    url = BASE_URL + "/app/menu/like/" + menuPkId;
                }
                if (mLikeMenuAsyncTask != null) {
                    return;
                }
                mLikeMenuAsyncTask = new LikeMenuAsyncTask(this);
                mLikeMenuAsyncTask.setmListener(this);
                mLikeMenuAsyncTask.execute(url);

                break;
            case R.id.id_ll_collect://收藏
                //用户未登录，则进行登录
                token = GetAuthorizationUtil.getAuth(this);
                if (TextUtils.isEmpty(token)) {
                    Intent intent2 = new Intent(this, LoginActivity.class);
                    intent2.putExtra("menuDetail", "menuDetail");
                    startActivity(intent2);
                    return;
                }
                if (currentCollect == 0) {//是当前用户收藏
                    //则取消收藏
                    url = BASE_URL + "/app/menu/notCollect/" + collectPkId;
                } else {
                    //进行收藏
                    url = BASE_URL + "/app/menu/collect/" + menuPkId;
                }

                if (mCollectMenuAsyncTask != null) {
                    return;
                }
                mCollectMenuAsyncTask = new CollectMenuAsyncTask(this);
                mCollectMenuAsyncTask.setmListener(this);
                mCollectMenuAsyncTask.execute(url);
                break;
            case R.id.id_ll_comment://评论
                token = GetAuthorizationUtil.getAuth(this);
                if (TextUtils.isEmpty(token)) {
                    IntentUtils.jump(this, LoginActivity.class);
                } else {
                    intentCommnetList();
                }
                break;
            case R.id.id_iv_share:
                wxShare();
                break;
        }
    }

    private PopupWindow pw;

    //weChat share
    private void wxShare() {
        View view = View.inflate(this, R.layout.share_layout, null);

        pw = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);

        //显示popupwindow
        if (pw != null && !pw.isShowing()) {
            //L.e("showing");
            pw.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
        }
        LinearLayout layoutCancel = (LinearLayout) view.findViewById(R.id.id_ll_share_cancel);
        ImageView weChat = (ImageView) view.findViewById(R.id.id_iv_we_chat);
        ImageView moments = (ImageView) view.findViewById(R.id.id_iv_moments);
        //设置监听事件
        ShareClickListener listener = new ShareClickListener();
        layoutCancel.setOnClickListener(listener);
        weChat.setOnClickListener(listener);
        moments.setOnClickListener(listener);
    }

    /**
     * 跳转到评论列表界面
     */
    private void intentCommnetList() {
        Intent intent = new Intent(this, CommentListActivity.class);
        intent.putExtra("flag", "menu");
        intent.putExtra("menuPkId", menuPkid);
        intent.putExtra("commentTotal", commentSize);
        intent.putExtra("menuPos",pos);
        startActivity(intent);
    }

    /**
     * 设置可见性
     */
    private void setVisibility() {
        int visibility = mRecyclerViewComment.getVisibility();
        if (visibility == View.GONE) {
            mRecyclerViewComment.setVisibility(View.VISIBLE);
            noComment.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从评论列表返回时候
        int result = SpUtils.getSharedPreferences(this).getInt("menuCommentChange", -1);//返回剩下的评论数量
        if (result > 0) {//改变了，需要刷新列表
            commentTotal.setText("评论(" + result + ")");
            commentSize = result;
            noComment.setVisibility(View.GONE);
            setVisibility();
            if (result > 6) {
                readMoreComment.setVisibility(View.VISIBLE);
            } else {
                readMoreComment.setVisibility(View.GONE);
            }
            loadComments();
            SpUtils.getEditor(this).putInt("menuCommentChange", -1).commit();
        } else if (result == 0) {
            //删除完了
            commentTotal.setText("评论(0)");
            readMoreComment.setVisibility(View.GONE);
            noComment.setVisibility(View.VISIBLE);
            mRecyclerViewComment.setVisibility(View.GONE);
        }

        //登录之后恢复显示界面
        boolean menuDetail = SpUtils.getSharedPreferences(this).getBoolean("menuDetail", false);
        if (menuDetail) {
            mMaterialsDose.removeAllViews();
            initData();
            SpUtils.getEditor(this).putBoolean("menuDetail", false).commit();
        }
    }

    //点赞操作的回调
    @Override
    public void onSuccess(int code, int pkId) {
        if (mLikeMenuAsyncTask != null) {
            mLikeMenuAsyncTask = null;
        }
        if (code == SUCCESS) {
            ToastUtils.toast(this, getString(R.string.text_operate_success));
            if (currentLike == 0) {//是当前用户点赞，则进行取消点赞
                currentLike = -1;//设置当前用户没点赞此菜谱
                likeImageView.setImageResource(R.drawable.not_like_32);
                --likeSize;
                if (likeSize == 0) {
                    likeNumber.setText("赞");
                } else {
                    likeNumber.setText(likeSize + "");
                }
            } else {
                likeImageView.setImageResource(R.drawable.like_32);
                ++likeSize;
                if (likeSize == 0) {
                    likeNumber.setText("赞");
                } else {
                    likeNumber.setText(likeSize + "");
                }
                if (pkId != -1) {
                    likePkId = pkId;
                }
                currentLike = 0;//点赞此菜谱
            }
        } else {
            ToastUtils.toast(this, getString(R.string.text_operate_failed));
        }
    }


    /**
     * 收藏操作的回调
     *
     * @param code 总code
     * @param pkId 收藏成功后返回的主键
     */
    @Override
    public void collectResult(int code, int pkId) {
        if (mCollectMenuAsyncTask != null) {
            mCollectMenuAsyncTask = null;
        }
        if (code == SUCCESS) {
            ToastUtils.toast(this, getString(R.string.text_operate_success));
            if (currentCollect == 0) {//是当前用户收藏，则进行取消收藏
                currentCollect = -1;//设置当前用户没收藏此菜谱
                colloectImageView.setImageResource(R.drawable.collect_gray_32);
                --collectSize;
                if (collectSize == 0) {
                    collectNumber.setText("收藏");
                } else {
                    collectNumber.setText(collectSize + "");
                }
                //取消收藏，返回到用户收藏列表
                SpUtils.getEditor(this).putInt("cancelCollectPosition",pos).commit();


            } else {
                colloectImageView.setImageResource(R.drawable.collect_red_32);
                ++collectSize;
                if (collectSize == 0) {
                    collectNumber.setText("收藏");
                } else {
                    collectNumber.setText(collectSize + "");
                }
                if (pkId != -1) {
                    collectPkId = pkId;
                }
                currentCollect = 0;//收藏此菜谱
            }
        } else {
            ToastUtils.toast(this, getString(R.string.text_operate_failed));
        }
    }


    class ShareClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final SendMessageToWX.Req req;
            switch (v.getId()) {
                case R.id.id_ll_share_cancel:
                    //显示popupwindow
                    if (pw != null && pw.isShowing()) {
                        pw.dismiss();
                    }
                    break;
                case R.id.id_iv_we_chat:
                    req = WxUtils.getReq(MenuDetailActivity.this, mainIcon, menuName, menuDesc, false);
                    api.sendReq(req);
                    dismiss();
                    break;
                case R.id.id_iv_moments:
                    req = WxUtils.getReq(MenuDetailActivity.this, mainIcon, menuName, menuDesc, true);
                    api.sendReq(req);
                    dismiss();
                    break;
            }
        }
    }


    //关闭popupwindow
    private void dismiss() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
    }

}
