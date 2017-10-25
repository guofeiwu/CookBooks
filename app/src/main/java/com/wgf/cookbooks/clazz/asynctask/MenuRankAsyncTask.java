package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取菜谱排行
 */
public class MenuRankAsyncTask extends AsyncTask<String, Void, Void> {
    private Context context;
    private IGetMenuRankListener mListener;
    public MenuRankAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(String... params) {
        //第几页,类型，子类型，关键字
        String url = params[0];
        OkGo.<String>get(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            List<Menu> list = JsonUtils.getMenusList(resJson);
                            mListener.result(list);
                        }else{
                            mListener.result(null);
                        }
                    }
                });
        return null;
    }
    public interface IGetMenuRankListener{
        void result(List<Menu> list);
    }
    public void setmListener(IGetMenuRankListener mListener) {
        this.mListener = mListener;
    }
}
