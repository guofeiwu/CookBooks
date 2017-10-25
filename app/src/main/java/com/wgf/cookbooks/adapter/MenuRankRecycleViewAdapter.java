package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haozhang.lib.SlantedTextView;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.view.CircleImageView;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 菜谱排行列表
 */
public class MenuRankRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private List<Menu> menus;
    private int status = 0;
    private Context context;
    private LayoutInflater mInflater;

    private IMenuDetailListener mListener;

    public MenuRankRecycleViewAdapter(Context context, List<Menu> menus) {
        this.context = context;
        this.menus = menus;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = mInflater.inflate(R.layout.menu_rank_layout_item, parent, false);
        return new MenuViewHolder(itemView);
    }

    private MenuViewHolder mHolder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        mHolder = (MenuViewHolder) holder;
        Menu menu = menus.get(position);
        Glide.with(context)
                .load(BASE_URL_FILE_MENUS + menu.getMainIcon())
                .into(mHolder.mMenuIcon);
        mHolder.mMenuTitle.setText(menu.getMenuName());
        mHolder.mMenuIntroduce.setText("简介:" + menu.getIntroduce());
        Glide.with(context).load(BASE_URL_FILE_ICON + menu.getUserIconUrl()).into(mHolder.mUserIcon);
        mHolder.mUserName.setText(menu.getUserName());
        //mHolder.slantedTextView.setText(position+"");
        mHolder.mTextViewRank.setText((position+1)+"");
        if(position ==0){
            mHolder.slantedTextView.setSlantedBackgroundColor(context.getResources().getColor(R.color.color_rank_red));
        }else if(position == 1){
            mHolder.slantedTextView.setSlantedBackgroundColor(context.getResources().getColor(R.color.color_rank_orange));
        }else if(position == 2){
            mHolder.slantedTextView.setSlantedBackgroundColor(context.getResources().getColor(R.color.color_rank_yellow));
        }else{
            mHolder.slantedTextView.setSlantedBackgroundColor(context.getResources().getColor(R.color.color_rank_gray));
        }
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.detail(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return menus.size();
    }

    //上拉加载更多
    public void addMoreItem(List<Menu> list) {
        menus.addAll(list);
        setLoadStatus(status);
    }

    //删除
    public void removeItem(int index) {
        menus.remove(index);
        notifyItemRemoved(index);
        //notifyDataSetChanged();
    }


    //设置footerview的状态
    public void setLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    protected class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView mMenuIcon;
        private TextView mMenuTitle, mMenuIntroduce, mUserName;
        private CircleImageView mUserIcon;
        private SlantedTextView slantedTextView;
        private TextView mTextViewRank;

        public MenuViewHolder(View menu) {
            super(menu);
            mMenuIcon = (ImageView) menu.findViewById(R.id.id_iv_menu_icon);
            mMenuTitle = (TextView) menu.findViewById(R.id.id_tv_menu_name);
            mMenuIntroduce = (TextView) menu.findViewById(R.id.id_tv_menu_introduce);
            mUserName = (TextView) menu.findViewById(R.id.id_user_name);
            mUserIcon = (CircleImageView) menu.findViewById(R.id.id_iv_user_icon);
            slantedTextView = (SlantedTextView) menu.findViewById(R.id.id_stv_rank);
            mTextViewRank = (TextView) menu.findViewById(R.id.id_tv_rank);
        }
    }


    public interface IMenuDetailListener {
        void detail(int position);
    }

    public void setmListener(IMenuDetailListener mListener) {
        this.mListener = mListener;
    }
}
