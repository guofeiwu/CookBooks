package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;


import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class MenuDetailAsyncTask extends AsyncTask<Integer,Void,Void> {

    private IMenuDetailListener mListener;
    private Context context;
    public MenuDetailAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int menuPkId = params[0];
        String url = BASE_URL+"/app/menu/detail/"+menuPkId;

        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            Menu menu = JsonUtils.getMenuDetail(resJson);
                            mListener.result(menu);
                        }else {
                            mListener.result(null);
                        }

                    }
                });
        return null;
    }


    public interface IMenuDetailListener{
        void result(Menu menu);
    }


    public void setmListener(IMenuDetailListener mListener) {
        this.mListener = mListener;
    }
}
