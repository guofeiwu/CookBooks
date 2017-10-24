package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 删除用户菜谱记录
 */
public class DeleteUserMenuRecordAsyncTask extends AsyncTask<String,Void,Void> {
    private IDeleteUserMenuRecordListener mListener;
    private Context context;
    public DeleteUserMenuRecordAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];
        OkGo.<String>delete(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        mListener.deleteRecord(code);
                    }
                });
        return null;
    }


    public interface IDeleteUserMenuRecordListener{
        void deleteRecord(int code);
    }

    public void setmListener(IDeleteUserMenuRecordListener mListener) {
        this.mListener = mListener;
    }
}
