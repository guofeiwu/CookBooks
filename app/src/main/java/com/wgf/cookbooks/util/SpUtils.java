package com.wgf.cookbooks.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 这个是获取SharedPreferences
 * author WuGuofei on 2017/5/23.
 * e-mail：guofei_wu@163.com
 */

public class SpUtils {
    //获取SharedPreferences
    public static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences sp = context.getSharedPreferences("config",context.MODE_PRIVATE);
        return sp;
    }

    //获取SharedPreferences.Editor
    public static SharedPreferences.Editor getEditor(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        return editor;
    }
}
