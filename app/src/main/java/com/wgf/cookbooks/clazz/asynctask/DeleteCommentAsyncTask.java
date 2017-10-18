package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 删除晒一晒评论
 */
public class DeleteCommentAsyncTask extends AsyncTask<Integer,Void,Void> {

    private IDeleteShaiCommentListener mListener;
    private Context context;
    public DeleteCommentAsyncTask(Context context, IDeleteShaiCommentListener mListener){
        this.context = context;
        this.mListener = mListener;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        Integer pkId = params[0];
        String url = null;
        if(params.length>=2){
            url = BASE_URL+"/app/menu/comment/"+pkId;
        }else {
            url = BASE_URL+"/app/shai/comment/"+pkId;
        }

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

    public interface IDeleteShaiCommentListener{
        void result(int code);
    }
}
