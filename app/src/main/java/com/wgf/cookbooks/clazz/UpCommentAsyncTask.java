package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;


import java.io.IOException;

import okhttp3.Response;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 评论晒晒，菜谱的异步任务
 *
 */
public class UpCommentAsyncTask extends AsyncTask<String,Void,Void> {
    private Context context;
    private IUpCommentListener mListener;
    public UpCommentAsyncTask(Context context,IUpCommentListener listener){
        this.context = context;
        this.mListener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
        String jsonObject = params[0];
        String url = params[1];
        try {
            OkGo.<String>post(url)
                    .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                    .upJson(jsonObject)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                            String resJson = response.body().toString();
                            int code = JsonUtils.getCode(resJson);
                            if(code == SUCCESS){
                                mListener.commentSuccess();
                            }else {
                                mListener.commentFailed();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface IUpCommentListener{
        void commentSuccess();
        void commentFailed();
    }

    public void setmListener(IUpCommentListener mListener) {
        this.mListener = mListener;
    }
}
