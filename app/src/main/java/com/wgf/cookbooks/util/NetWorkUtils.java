package com.wgf.cookbooks.util;

/**
 * author WuGuofei on 2017/5/22.
 * e-mail：guofei_wu@163.com
 */

public class NetWorkUtils {

//    private static final String SERVER_URL =  Constant.MAGAZINE_BASEURL+"appVersion/Info.json";
//    // private static final String APP_URL = "http://192.168.56.1:8080/E-Magazine/appVersion/e-magazine.apk";
//    private static final int RESULT_CODE = 0x001;
//    int id;//下载的标识,通过下载标识可以拿到一些下载信息
//    private Activity activity;
//    private String TAG;
//    /**
//     * 更新对话框
//     *
//     * @param appInfo
//     */
//    private AlertDialog dialog = null;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == RESULT_CODE) {
//                ByteArrayOutputStream bos = (ByteArrayOutputStream) msg.obj;
//
//                try {
//                    JSONObject object = new JSONObject(bos.toString());
//                    AppInfo appInfo = new AppInfo();
//                    int serverCode = object.getInt("versionCode");
//                    appInfo.version = serverCode;
//                    String downloadUrl = object.getString("downloadurl");
//                    appInfo.downloadUrl = downloadUrl;
//                    String desc = object.getString("desc");
//                    appInfo.desc = desc;
//                    try {
//                        PackageManager pm = activity.getPackageManager();
//                        PackageInfo packageInfo = pm.getPackageInfo(activity.getPackageName(), 0);
//                        int currentVersionCode = packageInfo.versionCode;//当前版本信息
//                        L.e("code:" + serverCode + "," + currentVersionCode + "," + downloadUrl + "," + desc);
//                        if (serverCode > currentVersionCode) {
//                            //服务器端的版本号大于当前的版本号
//                            // L.e("serverCode:"+serverCode+",currentVersionCode:"+currentVersionCode);
//                            updateDialog(appInfo);
//                        } else {
//
//                            if (TAG.equals(Constant.TAG_CHECK)) {
//                                ToastUtils.toast(activity, "当前已为最新版本");
//                            } else if (TAG.equals(Constant.TAG_SPLASH)) {
//                                new Thread() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            sleep(3 * 1000);
//                                            //显示3秒  //先跳转在停留显示
//                                            Intent intent = new Intent(activity, MainActivity.class);
//                                            activity.startActivity(intent);
//                                            activity.finish();
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }.start();
//                            }
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    ToastUtils.toast(activity, "网络错误，请检查你的网络");
//                }
//            }
//        }
//    };
//
//    public  NetWorkUtils(Activity activity,String TAG) {
//        this.activity = activity;
//        this.TAG = TAG;
//    }
//
//    /**
//     * 查看网络是否连接
//     */
//    public static void isConnectNetwork(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//
//        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
//            int type = networkInfo.getType();
//            if (type == ConnectivityManager.TYPE_WIFI) {
//                ToastUtils.toast(context, "你现在使用是的WiFi网络");
//            } else if (type == ConnectivityManager.TYPE_MOBILE) {
//                ToastUtils.toast(context, "你现在使用的是移动数据网络");
//            }
//        } else {
//            ToastUtils.toast(context, "你现在无网络连接");
//        }
//    }
//
//    public static int isConnectNetworkState(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//        int result = 0;
//        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
//            int type = networkInfo.getType();
//            if (type == ConnectivityManager.TYPE_WIFI) {
//                result = 1;
//            } else if (type == ConnectivityManager.TYPE_MOBILE) {
//                result = 2;
//            }
//        }
//        return result;
//    }
//
//
//    /**
//     * 查看当前版本信息，如果当前版本小于服务器端的版本
//     * 则进行版本更新
//     */
//    public void checkVersion() {
//        OkHttpClient client = new OkHttpClient();
//        Request.Builder requestBuilder = new Request.Builder();
//
//        Request request = requestBuilder.get().url(SERVER_URL).build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                L.e("onFailure");
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtils.toast(activity,"检查更新失败");
//                        Intent  intent= new Intent(activity,MainActivity.class);
//                        activity.startActivity(intent);
//                        activity.finish();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                L.e("onResponse");
//                InputStream is = response.body().byteStream();
//
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                BufferedInputStream bis = new BufferedInputStream(is);
//                int len;
//                while ((len = bis.read()) != -1) {
//                    bos.write(len);
//                }
//                bos.close();
//                bis.close();
//                Message msg = Message.obtain();
//                msg.what = RESULT_CODE;
//                msg.obj = bos;
//                mHandler.sendMessage(msg);
//            }
//        });
//
//    }
//
//    private void updateDialog(final AppInfo appInfo) {
//
//        TextView tv_title, tv_content;
//        Button btn_unUpdate, btn_update;
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_update, null);
//        builder.setView(view);
//
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_content = (TextView) view.findViewById(R.id.tv_content);
//
//        btn_unUpdate = (Button) view.findViewById(R.id.btn_unUpdate);
//        btn_update = (Button) view.findViewById(R.id.btn_update);
//
//
//        tv_title.setText("发现新版本:" + appInfo.version);
//        tv_content.setText(appInfo.desc);
//        btn_unUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TAG.equals(Constant.TAG_SPLASH)) {
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    activity.startActivity(intent);
//                    activity.finish();
//                }
//                dialog.dismiss();
//            }
//        });
//        //立即更新
//        btn_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(TAG.equals(Constant.TAG_SPLASH)){
//                    //立即更新,然后跳转
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    activity.startActivity(intent);
//                    activity.finish();
//                }
//                L.e("appInfo: " + appInfo.downloadUrl);
//                //下载
//                DownloadApp da = new DownloadApp(activity, appInfo.downloadUrl);
//                id = da.downloadApp();
//                L.e("id:" + id);
//                dialog.dismiss();
//            }
//        });
//        dialog = builder.show();
//    }
//
//
//    class AppInfo {
//        int version;
//        String downloadUrl;
//        String desc;
//    }


}
