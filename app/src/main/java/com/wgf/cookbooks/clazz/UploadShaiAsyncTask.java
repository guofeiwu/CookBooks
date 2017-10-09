package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import java.io.File;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 上传晒晒
 */
public class UploadShaiAsyncTask extends AsyncTask<File,Void,Void> {
    private String desc;
    private Context context;
    private IUploadShaiListener mListener;
    public UploadShaiAsyncTask(Context context,String desc){
        this.context = context;
        this.desc = desc;
    }
    @Override
    protected Void doInBackground(File... params) {
        String url = BASE_URL+"/app/shai/upload";
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .params("desc",desc)
                .params("shaiPicture",params[0])
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        mListener.result(code);
                    }
                });

        return null;
    }


    public interface IUploadShaiListener{
        void result(int code);
    }

    public void setmListener(IUploadShaiListener mListener) {
        this.mListener = mListener;
    }
}
