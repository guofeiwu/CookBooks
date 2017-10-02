package com.wgf.cookbooks.util;

import android.content.Context;
import android.widget.Toast;

/**
 * author WuGuofei on 2017/4/19.
 * e-mail：guofei_wu@163.com
 */

public class ToastUtils {

    private static Toast mToast;

    private ToastUtils(){
    }
    public static void toast(Context context ,String msg){
        //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        if(mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
