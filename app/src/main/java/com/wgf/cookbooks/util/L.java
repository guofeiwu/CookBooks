package com.wgf.cookbooks.util;

import android.util.Log;

/**
 * author WuGuofei on 2017/4/18.
 * e-mail：guofei_wu@163.com
 */

/**
 * 这是Log的工具类
 */
public class L {
    public static  boolean debug = true;
    public static final String TAG = "TAG";
    public static void e(String msg){
        if(debug)
            Log.e(TAG,msg);
    }
}
