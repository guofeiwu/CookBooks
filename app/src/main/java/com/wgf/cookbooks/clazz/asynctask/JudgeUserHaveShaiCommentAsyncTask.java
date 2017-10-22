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
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 判断当前用户在此菜谱是否还有评论
 */
public class JudgeUserHaveShaiCommentAsyncTask extends AsyncTask<Integer,Void,Void> {
    private IJudgeUserHaveShaiCommentListener mListener;
    private Context context;
    public JudgeUserHaveShaiCommentAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        int shaiPkId = params[0];
       String url = BASE_URL+"/app/shai/comment/currentUser/"+shaiPkId;
        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            mListener.hasComment(true);
                        }else{
                            mListener.hasComment(false);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.hasComment(false);
                    }
                });
        return null;
    }

    public interface IJudgeUserHaveShaiCommentListener{
        void hasComment(boolean has);
    }

    public void setmListener(IJudgeUserHaveShaiCommentListener mListener) {
        this.mListener = mListener;
    }
}
