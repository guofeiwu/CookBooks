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
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_APP;


public class DownloadApp {
    private String url;
    private Context context;
    private DownloadManager dm;
    private int downloadId;//下载的标识,通过下载标识可以拿到一些下载信息
    private DownloadFinishReceiver receiver;

    //private String testUrl = "http://192.168.56.1:8080/cate1.0.apk";
    public DownloadApp(Context context, String url) {
        this.context = context;
        this.url = url;
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        receiver = new DownloadFinishReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, filter);
    }

    public int downloadApp(float versionCode) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(BASE_URL_FILE_APP + url));
            //设置在什么网络情况下进行下载
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            //设置通知栏标题
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("下载");
            request.setDescription("美食菜谱正在下载");
            //设置是否允许漫游网络 建立请求 默认true
            request.setAllowedOverRoaming(true);

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            //设置请求的Mime
            request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
            //设置文件存放目录
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, File.separator + "cate" + versionCode + ".apk");
            downloadId = (int) dm.enqueue(request);
        return downloadId;
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
                Uri uri = dm.getUriForDownloadedFile(downloadId);
                Intent install = new Intent();
                if (Build.VERSION.SDK_INT >= 24) {
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                install.addCategory("android.intent.category.DEFAULT");
                install.setAction(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(install);
                Constants.DOWNLOADING = false;
                //取消广播
                cancelRecevier();
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
