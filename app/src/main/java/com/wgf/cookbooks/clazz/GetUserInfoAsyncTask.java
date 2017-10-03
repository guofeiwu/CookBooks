package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取用户信息
 */
public class GetUserInfoAsyncTask extends AsyncTask<Void,Void,JSONObject> {
    private JSONObject userInfo;//用户信息
    private Context context;
    private IGetUserInfoListener listener;
    public GetUserInfoAsyncTask(Context context){
        this.context = context;
    }
    public GetUserInfoAsyncTask(Context context,IGetUserInfoListener listener){
        this.context = context;
        this.listener = listener;
    }
    @Override
    protected JSONObject doInBackground(Void... params) {
        String url =BASE_URL+"/app/user";
        try {
            Response response = OkGo.<String>get(url)
                    .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                    .execute();

            String resJson = response.body().string();
            int code = JsonUtils.getCode(resJson);
            if(code == SUCCESS){
                userInfo = JsonUtils.getContent(resJson);//获取用户信息
                return userInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if(jsonObject !=null){
            listener.getUserInfo(jsonObject);
        }
    }

    public interface IGetUserInfoListener{
        void getUserInfo(JSONObject jsonUserInfo);
    }

    public void setListener(IGetUserInfoListener listener) {
        this.listener = listener;
    }
}
