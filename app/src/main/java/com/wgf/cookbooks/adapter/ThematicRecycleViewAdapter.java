package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.bean.Thematic;
import com.wgf.cookbooks.view.CircleImageView;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_THEMATIC;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 专题列表
 */
public class ThematicRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private List<Thematic> thematics;
    private int status = 0;
    private Context context;
    private LayoutInflater mInflater;

    private IThematicDetailListener mListener;

    public ThematicRecycleViewAdapter(Context context , List<Thematic> thematics){
        this.context = context;
        this.thematics = thematics;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.thematic_layout_item, parent, false);
            return new ThematicViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = mInflater.inflate(R.layout.footview, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    private ThematicViewHolder mHolder;
    private FooterViewHolder fHolder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ThematicViewHolder){
            mHolder = (ThematicViewHolder) holder;
            Thematic thematic = thematics.get(position);
            Glide.with(context)
                    .load(BASE_URL_FILE_THEMATIC+thematic.getThematicPictureUrl())
                    .into(mHolder.mThematicPicture);
            mHolder.mThematicName.setText(thematic.getThematicName());
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.detail(position);
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
                    fHolder.tv_foot.setText("上拉获取更多数据..");
                    fHolder.pb.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    fHolder.tv_foot.setText("正在加载更多数据...");
                    fHolder.pb.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }



    @Override
    public int getItemCount() {
        return thematics.size()+1;
    }

    //上拉加载更多
    public void addMoreItem(List<Thematic> list) {
        thematics.addAll(list);
        setLoadStatus(status);
    }

    //删除
    public void removeItem(int index) {
        thematics.remove(index);
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
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }



    protected class ThematicViewHolder extends RecyclerView.ViewHolder{
        private ImageView mThematicPicture;
        private TextView mThematicName;
        public ThematicViewHolder(View view) {
            super(view);
            mThematicPicture = (ImageView) view.findViewById(R.id.id_iv_thematic_picture_item);
            mThematicName = (TextView) view.findViewById(R.id.id_tv_thematic_title_item);
        }
    }



    protected class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_foot;
        private ProgressBar pb;

        public FooterViewHolder(View foot) {
            super(foot);
            tv_foot = (TextView) foot.findViewById(R.id.tv_foot);
            pb = (ProgressBar) foot.findViewById(R.id.pb);
        }
    }



    public interface IThematicDetailListener{
        void detail(int position);
    }

    public void setmListener(IThematicDetailListener mListener) {
        this.mListener = mListener;
    }
}
