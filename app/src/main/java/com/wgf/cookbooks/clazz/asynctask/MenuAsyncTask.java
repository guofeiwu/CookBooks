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
 * 获取菜谱
 */
public class MenuAsyncTask extends AsyncTask<Map, Void, Void> {
    private Context context;
    private IGetMenuListener mListener;
    public MenuAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(Map... params) {
        //第几页,类型，子类型，关键字
        Map map = params[0];
        JSONObject jsonObject = new JSONObject(map);
        String url = BASE_URL + "/app/menu/all";
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .upJson(jsonObject)
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
    public interface IGetMenuListener{
        void result(List<Menu> list);
    }
    public void setmListener(IGetMenuListener mListener) {
        this.mListener = mListener;
    }
}
