package com.wgf.cookbooks.clazz;

import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Banner;
import com.wgf.cookbooks.util.JsonUtils;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class GetBannerAsyncTask extends AsyncTask<Void,Void,Void> {
    private IGetBannerListener mListener;
    @Override
    protected Void doInBackground(Void... params) {
        String url = BASE_URL+"/app/menu/banner";
        OkGo.<String>get(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            List<Banner> banners = JsonUtils.getBanners(resJson);
                            mListener.getBanner(banners);
                        }else {
                            mListener.getBanner(null);
                        }
                    }
                });
        return null;
    }


    public interface IGetBannerListener{
        void getBanner(List<Banner> banners);
    }

    public void setmListener(IGetBannerListener mListener) {
        this.mListener = mListener;
    }
}
