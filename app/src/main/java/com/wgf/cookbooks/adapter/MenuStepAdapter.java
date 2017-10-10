package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.Step;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 菜谱步骤的适配器
 */
public class MenuStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private List<Step> steps;
    public MenuStepAdapter(Context context,List<Step> steps){
        this.context = context;
        this.steps = steps;
        mInflater = LayoutInflater.from(context);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menu_detail_layout_item,parent,false);
        return new MenuStepViewHolder(view);
    }
    private MenuStepViewHolder mHolder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mHolder = (MenuStepViewHolder) holder;
        Step step = steps.get(position);
        Glide.with(context).load(BASE_URL_FILE_MENUS+step.getStepIcon()).into(mHolder.stepImage);
        mHolder.stepTextView.setText(step.getStepDesc());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    private class MenuStepViewHolder extends RecyclerView.ViewHolder{
        private ImageView stepImage;
        private TextView stepTextView;
        public MenuStepViewHolder(View itemView) {
            super(itemView);
            stepImage = (ImageView) itemView.findViewById(R.id.id_iv_menu_detail_item);
            stepTextView = (TextView) itemView.findViewById(R.id.id_tv_detail_step);
        }
    }



}
