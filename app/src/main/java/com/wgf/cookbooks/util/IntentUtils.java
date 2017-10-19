package com.wgf.cookbooks.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class IntentUtils {
    /**
     * 跳转界面
     * @param context
     * @param cla
     */
    public static void jump(Context context,Class<?> cla){
        Intent intent = new Intent(context,cla);
        context.startActivity(intent);
    }

    /**
     * 跳转界面
     * @param activity
     * @param cla
     */
    public static void jump(Activity activity, Class<?> cla){
        Intent intent = new Intent(activity,cla);
        //用这个动画返回会白屏
//        activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle());
        activity.startActivity(intent);
    }

    /**
     * 跳转界面
     * @param context
     * @param cla
     * @param flag 这个是用来判断用户登录时候需要返回的界面
     */
    public static void jump(Context context,Class<?> cla,String flag){
        Intent intent = new Intent(context,cla);
        intent.putExtra("tag",flag);
        context.startActivity(intent);

    }


    /**
     * fragment 跳转界面
     * @param context
     * @param cla
     * @param requestCode
     */
    public static void jump(Fragment context,Class<?> cla ,int requestCode){
        Intent intent = new Intent(context.getActivity(),cla);
        intent.putExtra("requestCode",requestCode);
        context.startActivityForResult(intent,requestCode);
    }

}
