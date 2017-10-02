package com.wgf.cookbooks.util;

import android.content.Context;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class GetAuthorizationUtil {

    public static String getAuth(Context context){
        return SpUtils.getSharedPreferences(context).getString(AUTHORIZATION,null);
    }
}
