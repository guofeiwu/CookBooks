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
 * 获取专题菜谱
 */
public class GetThematicMenuAsyncTask extends AsyncTask<Map,Void,Void> {
    private IGetThematicMenuListener mListener;
    private Context context;
    public GetThematicMenuAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Map... params) {
        Map map = params[0];
        JSONObject jsonObject = new JSONObject(map);
        String url = BASE_URL+"/app/menu/thematic";
        OkGo.<String>post(url)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            List<Menu> list = JsonUtils.getMenusList(resJson);
                            mListener.thematicMenus(list);
                        }else{
                            mListener.thematicMenus(null);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.thematicMenus(null);
                    }
                });
        return null;
    }

    public interface IGetThematicMenuListener{
        void thematicMenus(List<Menu> list);
    }

    public void setmListener(IGetThematicMenuListener mListener) {
        this.mListener = mListener;
    }
}
