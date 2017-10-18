package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;

import java.io.File;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 上传菜谱内容
 */
public class UpMenuContentAsyncTask extends AsyncTask<String,Void,Void> {
    private Context context;
    private IUpMenuContentListener mListener;
    public UpMenuContentAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String jsonObject = params[0];
        String url = BASE_URL+"/app/menu/upContent";
        //String menuName,String menuDesc,Integer menuType,Integer menuTypeSun,
        OkGo.<String>post(url)
               //.isMultipart(true)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        L.e("resJosn:"+resJson);
                        mListener.success(code);
                    }
                });
        return null;
    }

    public interface IUpMenuContentListener{
        void success(int code);
    }

    public void setmListener(IUpMenuContentListener mListener) {
        this.mListener = mListener;
    }
}
