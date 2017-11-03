package com.wgf.cookbooks.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.lzy.okgo.OkGo;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wgf.cookbooks.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import top.zibin.luban.Luban;

import static com.wgf.cookbooks.util.Constants.APP_ID;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_MENUS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class WxUtils {
    private static IWXAPI api;

    /**
     * 注册到微信
     */
    public static IWXAPI register(Context context) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, true);
        api.registerApp(APP_ID);
        return api;
    }

//    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
//        if (needRecycle) {
//            bmp.recycle();
//        }
//
//        byte[] result = output.toByteArray();
//        try {
//            output.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }


    /**
     * 获取SendMessageToWX.Req
     *
     * @param context
     * @param menuName
     * @param menuDesc
     * @param moments
     * @return
     */
    public static SendMessageToWX.Req getReq(Context context, String url, String menuName, String menuDesc, boolean moments) {
        //初始化一个WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
//            webpageObject.webpageUrl = "http://www.jxnu.edu.cn/";
        webpageObject.webpageUrl = BASE_URL_FILE_MENUS + url;

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = menuName;
        msg.description = menuDesc;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_108);
//            Bitmap thumb = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon);
        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //是朋友圈
        if (moments) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return req;
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 分享app
     *
     * @param context
     */
    public static SendMessageToWX.Req shareApp(Context context, boolean moments) {
        //初始化一个WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = BASE_URL + "/app/download";//app下载链接

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = "美食菜谱";
        msg.description = "美食菜谱的作用的描述";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //是朋友圈
        if (moments) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return req;
    }


}
