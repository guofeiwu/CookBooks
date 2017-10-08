package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class UpdateLookTotalAsyncTask extends AsyncTask<Integer,Void,Void> {

    private IUpdateLookListener mListener;
    public UpdateLookTotalAsyncTask(){
    }
    @Override
    protected Void doInBackground(Integer... params) {
        Integer shaiPkId = params[0];
        Integer lookTotal = params[1];
        String url = BASE_URL+"/app/shai/look/"+lookTotal+"/shai/"+shaiPkId;

        OkGo.<String>get(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        L.e("code:"+code);
                    }
                });
        return null;
    }

    public interface IUpdateLookListener{
        void result(int code);
    }
}
