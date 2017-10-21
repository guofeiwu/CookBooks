package com.wgf.cookbooks.clazz.asynctask;


import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取用户收藏的菜谱
 */
public class GetUserCollectMenuAsyncTask extends AsyncTask<Integer,Void,Void> {
    private IGetUserCollectMenuListener mListener;
    private Context context;
    public GetUserCollectMenuAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        int pageNo = params[0];
        String url = BASE_URL+"/app/menu/collect/user/"+pageNo;
        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            List<Menu> list = JsonUtils.getMenusList(resJson);
                            mListener.userCollectMenus(list);
                        }else{
                            mListener.userCollectMenus(null);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.userCollectMenus(null);
                    }
                });
        return null;
    }

    public interface IGetUserCollectMenuListener{
        void userCollectMenus(List<Menu> list);
    }

    public void setmListener(IGetUserCollectMenuListener mListener) {
        this.mListener = mListener;
    }
}
