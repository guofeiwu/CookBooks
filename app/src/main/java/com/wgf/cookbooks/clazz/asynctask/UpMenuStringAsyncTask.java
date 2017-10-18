package com.wgf.cookbooks.clazz.asynctask;


import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 上传菜谱
 */
public class UpMenuStringAsyncTask extends AsyncTask<String,Void,Void> {
    private IUpMenuListener mListener;
    private Context context;
    public UpMenuStringAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String url = "";
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .upJson(params[0])
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJosn = response.body().toString();
                        int code = JsonUtils.getCode(resJosn);
                        if(code == SUCCESS){
                            mListener.result(SUCCESS);
                        }else{
                            mListener.result(FAILED);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.result(FAILED);
                    }
                });
        return null;
    }

    public interface IUpMenuListener{
        void result(int code);
    }

    public void setmListener(IUpMenuListener mListener) {
        this.mListener = mListener;
    }
}
