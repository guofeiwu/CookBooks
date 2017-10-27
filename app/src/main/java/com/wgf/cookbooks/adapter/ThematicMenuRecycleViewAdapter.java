package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 菜谱列表
 */
public class ThematicMenuRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEAD = 0;
    public static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private List<Menu> menus;
    private int status = 0;
    private Context context;
    private LayoutInflater mInflater;

    private IMenuDetailListener mListener;
    private Thematic thematic;

    public ThematicMenuRecycleViewAdapter(Context context , Thematic thematic, List<Menu> menus){
        this.context = context;
        this.menus = menus;
        this.thematic = thematic;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_HEAD) {
            itemView = mInflater.inflate(R.layout.shai_layout_head, parent, false);
            return new HeadViewHolder(itemView);
        } else if (viewType == TYPE_NORMAL) {
            itemView = mInflater.inflate(R.layout.user_menu_layout_item, parent, false);
            return new MenuViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            itemView = mInflater.inflate(R.layout.footview, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    private HeadViewHolder hHolder;
    private MenuViewHolder mHolder;
    private FooterViewHolder fHolder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder){
            hHolder = (HeadViewHolder) holder;
            //设置图片
            Glide.with(context)
                    .load(BASE_URL_FILE_THEMATIC+thematic.getThematicPictureUrl())
                    .placeholder(R.drawable.icon_108)
                    .into(hHolder.mCover);
            //设置标题
            hHolder.mTitle.setText(thematic.getThematicName()+" 专题");
        }else if(holder instanceof MenuViewHolder){
            mHolder = (MenuViewHolder) holder;
            Menu menu = menus.get(position-1);
            Glide.with(context)
                    .load(BASE_URL_FILE_MENUS+menu.getMainIcon())
                    .into(mHolder.mMenuIcon);
            mHolder.mMenuTitle.setText(menu.getMenuName());
            mHolder.mMenuIntroduce.setText("简介:"+menu.getIntroduce());
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.detail(position-1);
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
        return menus.size()+2;
    }

    //上拉加载更多
    public void addMoreItem(List<Menu> list) {
        menus.addAll(list);
        setLoadStatus(status);
    }

    //删除
    public void removeItem(int index) {
        menus.remove(index);
        //notifyItemRemoved(index);
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

    protected class HeadViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCover;
        private TextView mTitle;
        public HeadViewHolder(View itemView) {
            super(itemView);
            mCover = (CircleImageView) itemView.findViewById(R.id.id_civ_shai_cover);
            mTitle = (TextView) itemView.findViewById(R.id.id_tv_shai_desc);
        }
    }


    protected class MenuViewHolder extends RecyclerView.ViewHolder{
        private ImageView mMenuIcon;
        private TextView mMenuTitle,mMenuIntroduce;
        public MenuViewHolder(View menu) {
            super(menu);
            mMenuIcon = (ImageView) menu.findViewById(R.id.id_iv_menu_icon);
            mMenuTitle = (TextView) menu.findViewById(R.id.id_tv_menu_name);
            mMenuIntroduce = (TextView) menu.findViewById(R.id.id_tv_menu_introduce);
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



    public interface IMenuDetailListener{
        void detail(int position);
    }

    public void setmListener(IMenuDetailListener mListener) {
        this.mListener = mListener;
    }
}
