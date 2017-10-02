package com.wgf.cookbooks.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * author WuGuofei on 2017/4/18.
 * e-mail：guofei_wu@163.com
 */

public class DownloadApp {
    private String url;
    private Context context;
    private DownloadManager dm;
    private int id;
    private DownloadFinishReceiver receiver;

    //private String testUrl = "http://192.168.56.1:8080/E-Magazine/base.apk";
    public DownloadApp(Context context, String url) {
        this.context = context;
        this.url = url;
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        receiver = new DownloadFinishReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, filter);
    }

    public int downloadApp() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置在什么网络情况下进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("下载");
        request.setDescription("E-Magazine正在下载");
        //设置是否允许漫游网络 建立请求 默认true
        request.setAllowedOverRoaming(true);

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //设置请求的Mime
        request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, File.separator+"e-magazine.apk");
        id = (int) dm.enqueue(request);
        return id;
    }


    public void queryApp(int id) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
        Cursor cursor = dm.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String bytesDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                String descrition = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                String rellId = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }
        }

    }

    //下载完成后接收的广播
    public class DownloadFinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) |
                    intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Uri uri = dm.getUriForDownloadedFile(id);
                if(Build.VERSION.SDK_INT < 23){
                    L.e("sdk<23");
                    Intent intents = new Intent();
                    intents.setAction("android.intent.action.VIEW");
                    intents.addCategory("android.intent.category.DEFAULT");
//                    intents.setType("application/vnd.android.package-archive");
//                    intents.setData(uri);
                    intents.setDataAndType(uri, "application/vnd.android.package-archive");
                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intents);
                }else{
                    //TODO 这里如果取消安装的情况
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                }
            }
        }
    }


    public void cancelRecevier() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }


}
