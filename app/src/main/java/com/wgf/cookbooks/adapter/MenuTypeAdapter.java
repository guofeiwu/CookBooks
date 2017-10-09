package com.wgf.cookbooks.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wgf.cookbooks.R;

import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MenuTypeAdapter extends BaseAdapter {
    //private Context context;
    private List<Map<String,Object>> titles;
    public MenuTypeAdapter(List<Map<String,Object>> titles){
        //this.context = context;
        this.titles = titles;
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_type_item,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_iv_item);
        TextView  textView = (TextView) view.findViewById(R.id.id_tv_item);
        Map<String,Object> map = titles.get(position);
        imageView.setImageResource((Integer) map.get("imageId"));
        textView.setText(map.get("title")+"");
        return view;
    }


    private class ViewHolder{

    }



}
