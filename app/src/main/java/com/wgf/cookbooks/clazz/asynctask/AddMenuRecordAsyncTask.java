package com.wgf.cookbooks.clazz.asynctask;

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
public class AddMenuRecordAsyncTask extends AsyncTask<Integer,Void,Void> {

    private IAddMenuRecordListener mListener;
    private Context context;
    public AddMenuRecordAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        Integer menuPkId = params[0];
        String url = BASE_URL+"/app/menu/record/"+menuPkId;

        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
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

    public interface IAddMenuRecordListener{
        void result(int code);
    }
}
