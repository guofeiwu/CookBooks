package com.wgf.cookbooks.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 软键盘工具类
 */
public class SoftInputUtils {

    /**
     * 此方法只是关闭软键盘
     * @param context
     */
    public static void hideSoftInput(Activity context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&context.getCurrentFocus()!=null){
            if (context.getCurrentFocus().getWindowToken()!=null) {//InputMethodManager.HIDE_NOT_ALWAYS
                imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 显示软键盘
     * @param context
     */
    public static void showSoftInput(Activity context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

//
//    public static void hintInput(Activity context) {
//        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm.isActive()&&context.getCurrentFocus()!=null){
//            if (context.getCurrentFocus().getWindowToken()!=null) {
//                imm.hideSoftInputFromWindow(context.getWindowToken(), 0);
//            }
//        }
//    }



}
