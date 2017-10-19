package com.wgf.cookbooks.clazz.asynctask;

import android.os.AsyncTask;
import android.view.SubMenu;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.AppVer;
import com.wgf.cookbooks.util.JsonUtils;


import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取最新的app版本信息
 */
public class GetAppVerAsyncTask extends AsyncTask<Void,Void,Void> {

    private IGetAppVerListener mListener;
    @Override
    protected Void doInBackground(Void... params) {
        String url = BASE_URL+"/app/system/checkUpdate";
        OkGo.<String>get(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String renJson = response.body().toString();
                        int code = JsonUtils.getCode(renJson);
                        if(code == SUCCESS){
                            AppVer appVer = JsonUtils.getAppVer(renJson);
                            mListener.appVer(appVer);
                        }else{
                            mListener.appVer(null);
                        }
                    }
                });
        return null;
    }


    public interface IGetAppVerListener{
        void appVer(AppVer appVer);
    }

    public void setmListener(IGetAppVerListener mListener) {
        this.mListener = mListener;
    }
}
