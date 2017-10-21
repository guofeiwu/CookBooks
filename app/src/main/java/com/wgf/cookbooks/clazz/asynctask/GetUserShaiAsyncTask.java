package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import java.util.List;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取用户的晒一晒
 */
public class GetUserShaiAsyncTask extends AsyncTask<Integer, Void, Void> {
    private IGetUserShaiListener listener;

    private Context context;
    public GetUserShaiAsyncTask(Context context,IGetUserShaiListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        String url = BASE_URL + "/app/shai/user/" + params[0];
        try {
            OkGo.<String>get(url)
                    .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String resJson = response.body().toString();
                            int code = JsonUtils.getCode(resJson);
                            if(code == SUCCESS){
//                                JSONArray jsonArray = JsonUtils.getJsonArray(resJosn);
                                List<Shai> list = JsonUtils.getShaiList(resJson);
                                listener.getShaiList(list);
                            }else{
                                listener.getShaiList(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface IGetUserShaiListener{
        void getShaiList(List<Shai> lists);
    }





    public void setListener(IGetUserShaiListener listener) {
        this.listener = listener;
    }
}

