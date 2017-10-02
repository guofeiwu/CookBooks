package com.wgf.cookbooks.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wgf.cookbooks.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author WuGuofei on 2017/5/24.
 * e-mail：guofei_wu@163.com
 */

public class GlideUtils {

    //加载用户头像
    public static void showUserIcon(Activity activity, String url, final ImageView circleImageView) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Glide.with(activity)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.tangy)
//                .signature(new StringSignature(str))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        circleImageView.setImageBitmap(resource);
                    }
                });
    }

    //加载用户头像
    public static void showUserIcon2(Activity activity, String url, final ImageView circleImageView) {
        Glide.with(activity)
                 .load(url)
                 .asBitmap()
                 .centerCrop()
                 .placeholder(R.drawable.tangy)
                 .into(circleImageView);
    }

}
