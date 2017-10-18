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
 * 菜谱收藏，取消收藏的异步任务
 */
public class CollectMenuAsyncTask extends AsyncTask<String,Void,Void> {
    private ICollectMenuListener mListener;
    private Context context;
    public CollectMenuAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];
        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJosn = response.body().toString();
                        int code = JsonUtils.getCode(resJosn);
                        if(code == SUCCESS){
                            int pkId = JsonUtils.getPkId(resJosn);
                            mListener.collectResult(code,pkId);
                        }else{
                            mListener.collectResult(code,-1);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.collectResult(FAILED,-1);
                    }
                });
        return null;
    }

    public interface ICollectMenuListener{
        /**
         * @param code 总code
         * @param pkId 收藏成功后返回的主键
         */
        void collectResult(int code, int pkId);
    }

    public void setmListener(ICollectMenuListener mListener) {
        this.mListener = mListener;
    }
}
