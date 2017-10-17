package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class CommitFeedbackAsynctask extends AsyncTask<String,Void,Void> {
    private ICommitFeedbackListener mListener;
    private Context context;
    public CommitFeedbackAsynctask (Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String content = params[0];
        String url = BASE_URL+"/app/system/feedback";
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .upJson(content)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson= response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        mListener.success(code);
                    }
                });
        return null;
    }

    public interface ICommitFeedbackListener{
        void success(int code);
    }

    public void setmListener(ICommitFeedbackListener mListener) {
        this.mListener = mListener;
    }
}
