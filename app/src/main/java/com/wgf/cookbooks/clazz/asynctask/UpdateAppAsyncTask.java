package com.wgf.cookbooks.clazz.asynctask;

import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.AppVer;
import com.wgf.cookbooks.util.JsonUtils;

import java.io.File;

import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 下载app
 */
public class UpdateAppAsyncTask extends AsyncTask<Void,Void,Void> {

    private IUpdateAppListener mListener;
    public UpdateAppAsyncTask(IUpdateAppListener mListener){
        this.mListener = mListener;
    }
    @Override
    protected Void doInBackground(Void... params) {
        String url = "";
        OkGo.<File>get(url)
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        File file = response.body();
                        mListener.download(file);
                    }
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        mListener.download(null);
                    }
                });
        return null;
    }

    public interface IUpdateAppListener{
        void download(File file);
    }

    public void setmListener(IUpdateAppListener mListener) {
        this.mListener = mListener;
    }
}
