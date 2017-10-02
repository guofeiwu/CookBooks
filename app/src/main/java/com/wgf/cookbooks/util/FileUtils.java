package com.wgf.cookbooks.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * author WuGuofei on 2017/5/9.
 * e-mail：guofei_wu@163.com
 */

public class FileUtils {
    //获取缓存地址
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
}
