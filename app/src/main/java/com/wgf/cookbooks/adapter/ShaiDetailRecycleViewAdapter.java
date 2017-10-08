package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.UserInfoActivity;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import okhttp3.Response;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_SHAI;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒的适配器类
 */
public class ShaiDetailRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Shai> shaiLists;
    private Context context;
    private LayoutInflater mInflater;
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private int status;
    private IShaiClickListener mIShaiClickListener;
    private String token;
    private JSONObject likeContent;
    private LikeAsyncTask mLikeAsyncTask;

    public ShaiDetailRecycleViewAdapter(Context context, List<Shai> shaiLists) {
        this.context = context;
        this.shaiLists = shaiLists;
        mInflater = LayoutInflater.from(context);
        getLikes();
    }

    /**
     * 获取当前用户所有的like晒晒点赞
     * @return
     */
    private void getLikes() {
        token = SpUtils.getSharedPreferences(context).getString(AUTHORIZATION, null);
        if (token != null) {
            new Thread() {
                @Override
                public void run() {
                    String url = BASE_URL + "/app/shai/like";
                    try {
                        Response response = OkGo.<String>get(url)
                                .headers(AUTHORIZATION, token)
                                .execute();
                        String resJson = response.body().string();
                        int code = JsonUtils.getCode(resJson);
                        if (code == SUCCESS) {
                            likeContent = JsonUtils.getContent(resJson);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_HEAD) {
            itemView = mInflater.inflate(R.layout.shai_layout_head, parent, false);
            return new HeadViewHolder(itemView);
        } else if (viewType == TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.shai_detail_layout_item, parent, false);
            return new ShaiDetailViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = mInflater.inflate(R.layout.footview, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    private ShaiDetailViewHolder holder;
    private FooterViewHolder fholder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof FooterViewHolder) {
            fholder = (FooterViewHolder) viewHolder;
            switch (status) {
                case 0:
                    fholder.tv_foot.setText("我也是有底线的...");
                    fholder.pb.setVisibility(View.GONE);
                    break;
                case 1:
                    fholder.tv_foot.setText("上拉获取更多晒晒...");
                    fholder.pb.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    fholder.tv_foot.setText("正在加载更多晒晒...");
                    fholder.pb.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (viewHolder instanceof ShaiDetailViewHolder) {
            holder = (ShaiDetailViewHolder) viewHolder;
            Shai shai = shaiLists.get(position - 1);
            int shaiPkId = shai.getShaiPkId();
            String userName = shai.getUserName();
            String icon = shai.getIcon();
            String descr = shai.getDescr();
            String address = shai.getAddress();//晒一晒图片地址
            String time = shai.getTime();//创建时间
            int likes = shai.getLikes();
            holder.mUserName.setText(userName);
            Glide.with(context).load(BASE_URL_FILE_ICON + icon).into(holder.mImageIcon);
            holder.mShaiDesc.setText(descr);
            Glide.with(context).load(BASE_URL_FILE_SHAI + address).into(holder.mImageViewShai);
            holder.mShaiTime.setText(time);
            // TODO: 2017/10/5 这里有问题
            if (likeContent != null) {
                try {
                    Integer value = likeContent.optInt(String.valueOf(shaiPkId));//这里是like的主键，这里处理问题

                    if (value == null || value == 0 || value.equals("")) {
                        // TODO: 2017/10/5 设置图片这边貌似还有点问题，待处理,,调试一步一步走没问题。。但是直接就有问题
                        holder.mImageViewLike.setImageResource(R.drawable.not_like_32);
                    } else {
                        holder.mImageViewLike.setImageResource(R.drawable.like_32);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (likes == 0) {
                holder.mLikeNumber.setText("赞");
            } else if (likes != 0) {
                holder.mLikeNumber.setText(likes + "");
            }

            holder.mlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIShaiClickListener.like(position - 1);
                }
            });

            holder.mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIShaiClickListener.comment(position - 1);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIShaiClickListener.detail(position - 1);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return shaiLists.size() + 2;
    }

    protected class ShaiDetailViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageIcon, mImageViewShai, mImageViewLike;
        private TextView mUserName, mShaiDesc, mLikeNumber, mShaiTime;
        private LinearLayout mlike, mComment;

        public ShaiDetailViewHolder(View itemView) {
            super(itemView);
            mImageIcon = (ImageView) itemView.findViewById(R.id.id_civ_icon_item);
            mUserName = (TextView) itemView.findViewById(R.id.id_tv_user_name_item);

            mImageViewShai = (ImageView) itemView.findViewById(R.id.id_iv_shai_item);
            mShaiDesc = (TextView) itemView.findViewById(R.id.id_tv_desc_item);


            mShaiTime = (TextView) itemView.findViewById(R.id.id_tv_shai_time_item);

            mImageViewLike = (ImageView) itemView.findViewById(R.id.id_iv_like_item);
            mlike = (LinearLayout) itemView.findViewById(R.id.id_ll_like_item);
            mLikeNumber = (TextView) itemView.findViewById(R.id.id_tv_like_count_item);

            mComment = (LinearLayout) itemView.findViewById(R.id.id_ll_comment_item);
        }
    }

    protected class HeadViewHolder extends RecyclerView.ViewHolder {
        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_foot;
        private ProgressBar pb;

        public FooterViewHolder(View itemView) {
            super(itemView);
            tv_foot = (TextView) itemView.findViewById(R.id.tv_foot);
            pb = (ProgressBar) itemView.findViewById(R.id.pb);
        }
    }

    //上拉加载更多
    public void addMoreItem(List<Shai> list) {
        shaiLists.addAll(list);
        setLoadStatus(status);
    }

    //删除
    public void removeItem(int index) {
        shaiLists.remove(index);
        notifyItemRemoved(index);
        //notifyDataSetChanged();
    }


    //设置footerview的状态
    public void setLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public interface IShaiClickListener {
        void like(int position);

        void comment(int position);

        void detail(int position);
    }

    public void setmIShaiClickListener(IShaiClickListener mIShaiClickListener) {
        this.mIShaiClickListener = mIShaiClickListener;
    }

    /**
     * 刷新likeContent 获取当前用户的晒晒的所以的点赞
     */
    public void flashLikeContent(){
        getLikes();
        notifyDataSetChanged();
    }




    /**
     * 某一项数据更新时
     * @param position
     */
    private int shaiPkId = 0;
    public void flashItem(int position){
        Shai shai = shaiLists.get(position);
        shaiPkId = shai.getShaiPkId();
        int likes = shai.getLikes();
        int likePkId = likeContent.optInt(String.valueOf(shaiPkId));
        if(likePkId>0){//取消收藏
            if(likes >= 1){
                shai.setLikes(likes-1);
                likeContent.remove(String.valueOf(shaiPkId));
               String url = BASE_URL+"/app/shai/dislike/"+likePkId;
                if(mLikeAsyncTask != null){
                    return;
                }
                mLikeAsyncTask = new LikeAsyncTask();
                mLikeAsyncTask.execute(url,"dislike");
            }
        }else{//收藏
              shai.setLikes(likes+1);
                if(mLikeAsyncTask != null){
                    return;
                }
                String url = BASE_URL+"/app/shai/like/"+shaiPkId;
                mLikeAsyncTask = new LikeAsyncTask();
                mLikeAsyncTask.execute(url,"like");
        }
    }

    /**
     * 用户点赞，取消点赞
     */
    private class LikeAsyncTask extends AsyncTask<String,Void,Integer>{
        String flag = null;
        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            flag = params[1];
            try {
                Response response = OkGo.<String>get(url)
                        .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                        .execute();
                String resJson = response.body().string();
                int code = JsonUtils.getCode(resJson);
                if(code == SUCCESS){
                    if(flag.equals("like")){
                        int likePkId = JsonUtils.getContent(resJson).getInt("likePkId");
                        return likePkId;
                    }else {
                        return SUCCESS;
                    }
                }else{
                    return FAILED;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mLikeAsyncTask = null;
            if(integer == SUCCESS){
                notifyDataSetChanged();
                ToastUtils.toast(context,"操作成功");
            }else if(integer == FAILED){
                ToastUtils.toast(context,"操作失败");
            }else{
                try {
                    likeContent.put(String.valueOf(shaiPkId),integer);
                    notifyDataSetChanged();
                    ToastUtils.toast(context,"操作成功");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
