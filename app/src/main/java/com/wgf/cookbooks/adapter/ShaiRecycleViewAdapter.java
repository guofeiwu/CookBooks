package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;

import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒的适配器类
 */
public class ShaiRecycleViewAdapter extends RecyclerView.Adapter<ShaiRecycleViewAdapter.ShaiViewHolder> {
    private List<String> urls;
    private Context context;
    private LayoutInflater mInflater;
    public ShaiRecycleViewAdapter(Context context,List<String> urls){
        this.context = context;
        this.urls = urls;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public ShaiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.shai_layout_item, parent, false);
        return new ShaiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShaiViewHolder holder, int position) {
        Glide.with(context).load(urls.get(position)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    protected class ShaiViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public ShaiViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.id_iv_shai);
        }
    }

}
