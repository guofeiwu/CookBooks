package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.view.CircleImageView;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 加载所有的评论
 */
public class ShaiCommentListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private ICommentDeleteListener mListener;

    private int status;
    private Context context;
    private List<Comment> comments;
    private LayoutInflater mInflater;
    public ShaiCommentListRecycleViewAdapter(Context context, List<Comment> comments){
        this.context = context;
        this.comments = comments;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
         if (viewType == TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.comment_layout_item, parent, false);
            return new CommentViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = mInflater.inflate(R.layout.footview, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    private FooterViewHolder fHolder;

    private CommentViewHolder mHolder;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof CommentViewHolder){
            mHolder = (CommentViewHolder) holder;
            Comment comment = comments.get(position);
            Glide.with(context).load(BASE_URL_FILE_ICON +comment.getUserIconUrl()).into(mHolder.mCircleImageView);
            mHolder.mUserName.setText(comment.getUserName());
            mHolder.mCommentTime.setText(comment.getCommentTime());
            mHolder.mCommentContent.setText(comment.getContent());
            int currentUser = comment.getCurrentUser();
            if(currentUser == 0){
                mHolder.mCommentDelete.setVisibility(View.VISIBLE);
            }else{
                mHolder.mCommentDelete.setVisibility(View.GONE);
            }
            mHolder.mCommentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.delete(position);
                }
            });
        }else if(holder instanceof FooterViewHolder){
            fHolder = (FooterViewHolder) holder;
            switch (status) {
                case 0:
                    fHolder.tv_foot.setText("我也是有底线的...");
                    fHolder.pb.setVisibility(View.GONE);
                    break;
                case 1:
                    fHolder.tv_foot.setText("上拉获取更多评论...");
                    fHolder.pb.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    fHolder.tv_foot.setText("正在加载更多评论...");
                    fHolder.pb.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return comments.size()+1;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mCircleImageView;
        private TextView mUserName;
        private TextView mCommentTime;
        private TextView mCommentContent;
        private TextView mCommentDelete;
        public CommentViewHolder(View itemView) {
            super(itemView);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.id_civ);
            mUserName = (TextView) itemView.findViewById(R.id.id_tv_username);
            mCommentTime = (TextView) itemView.findViewById(R.id.id_comment_time);
            mCommentContent = (TextView) itemView.findViewById(R.id.id_comment_content);
            mCommentDelete = (TextView) itemView.findViewById(R.id.id_comment_delete);
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
    public void addMoreItem(List<Comment> list) {
        comments.addAll(list);
        setLoadStatus(status);
    }

    //删除
    public void removeItem(int index) {
        comments.remove(index);
        notifyItemRemoved(index);
        //notifyDataSetChanged();
    }


    //设置footerview的状态
    public void setLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }



    /**
     * 删除评论时，刷新列表
     * @param position
     */
    public void deleteComment(int position){
        comments.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
       if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }



    public interface ICommentDeleteListener{
        void delete(int position);
    }

    public void setmListener(ICommentDeleteListener mListener) {
        this.mListener = mListener;
    }





}
