package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.view.CircleImageView;


import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class ShaiCommentRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Comment> comments;
    private LayoutInflater mInflater;
    public ShaiCommentRecycleViewAdapter(Context context,List<Comment> comments){
        this.context = context;
        this.comments = comments;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  mInflater.inflate(R.layout.comment_layout_item,null);
        return new CommentViewHolder(view);
    }

    private CommentViewHolder mHolder;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CommentViewHolder){
            mHolder = (CommentViewHolder) holder;
            Comment comment = comments.get(position);
            Glide.with(context).load(BASE_URL_FILE_ICON +comment.getUserIconUrl()).into(mHolder.mCircleImageView);
            mHolder.mUserName.setText(comment.getUserName());
            mHolder.mCommentTime.setText(comment.getCommentTime());
            mHolder.mCommentContent.setText(comment.getContent());
        }
    }
    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mCircleImageView;
        private TextView mUserName;
        private TextView mCommentTime;
        private TextView mCommentContent;
        public CommentViewHolder(View itemView) {
            super(itemView);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.id_civ);
            mUserName = (TextView) itemView.findViewById(R.id.id_tv_username);
            mCommentTime = (TextView) itemView.findViewById(R.id.id_comment_time);
            mCommentContent = (TextView) itemView.findViewById(R.id.id_comment_content);
        }
    }

}
