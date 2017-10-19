package com.wgf.cookbooks.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.AppVer;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 更新app
 */
public class UpdateAppVerUtils {
    public static int updateApp(Context context,AppVer appVer){
        float versionCode = appVer.getVer();
        String downloadUrl = appVer.getDownloadUrl();
        String updateDesc = appVer.getVerDesc();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),0);
            float currentVersionCode = pi.versionCode;
            //服务器版本大于当前版本，进行版本更新
            if(versionCode>currentVersionCode){
                confirmDialog(context,downloadUrl,updateDesc,versionCode);
            }else{
                ToastUtils.toast(context,"已经是最新版本");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return downloadId;
    }


    private static Dialog dialog;

    private static int downloadId = 0;
    /**
     * 更新提示框提示框
     */
    private static void confirmDialog(final Context context, final String downloadUrl, String updateDesc, final float versionCode){
            //弹出是否要退出的对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("提示");
//            builder.setMessage(R.string.text_confirm_msg);
        View view = View.inflate(context,R.layout.update_app_dialog,null);
        TextView updateMsg = (TextView) view.findViewById(R.id.id_tv_update_msg);
        Button downloadOk = (Button) view.findViewById(R.id.id_download_ok);
        Button downloadCancel = (Button) view.findViewById(R.id.id_download_cancel);
        updateMsg.setText(updateDesc);
        builder.setView(view);
        downloadOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadApp downloadApp = new DownloadApp(context,downloadUrl);
                downloadId = downloadApp.downloadApp(versionCode);
                Constants.DOWNLOADING = true;
                dialog.dismiss();
            }
        });
        downloadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

}
