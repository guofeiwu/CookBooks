package com.wgf.cookbooks.clazz;

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
 * 获取晒一晒详情
 */
public class GetShaiDetailAsyncTask extends AsyncTask<Integer, Void, Void> {
    private IGetShaiListener listener;
    private Context context;
    public GetShaiDetailAsyncTask(Context context,IGetShaiListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        String url = BASE_URL + "/app/shai/detail/" + params[0];
        try {
            OkGo.<String>get(url)
                    .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String resJosn = response.body().toString();
                            int code = JsonUtils.getCode(resJosn);
                            if(code == SUCCESS){
                                Shai shai = JsonUtils.getShaiDetail(resJosn);
                                listener.getShaiDetail(shai);
                            }else{
                                listener.getShaiDetail(null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface IGetShaiListener{
        void getShaiDetail(Shai shai);
    }





    public void setListener(IGetShaiListener listener) {
        this.listener = listener;
    }
}

