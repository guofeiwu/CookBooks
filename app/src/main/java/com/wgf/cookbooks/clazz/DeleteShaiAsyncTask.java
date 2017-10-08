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
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 删除晒一晒
 */
public class DeleteShaiAsyncTask extends AsyncTask<Integer,Void,Void> {

    private IDeleteShaiListener mListener;
    private Context context;
    public DeleteShaiAsyncTask(Context context,IDeleteShaiListener mListener){
        this.context = context;
        this.mListener = mListener;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        Integer shaiPkId = params[0];
        String url = BASE_URL+"/app/shai/"+shaiPkId;

        OkGo.<String>delete(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
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

    public interface IDeleteShaiListener{
        void result(int code);
    }
}
