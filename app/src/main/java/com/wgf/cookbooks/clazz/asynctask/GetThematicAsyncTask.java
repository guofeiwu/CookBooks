package com.wgf.cookbooks.clazz.asynctask;


import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.bean.Thematic;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取专题内容
 */
public class GetThematicAsyncTask extends AsyncTask<Integer,Void,Void> {
    private IGetThematicListener mListener;
    private Context context;
    public GetThematicAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        int pageNo = params[0];
        String url = BASE_URL+"/app/menu/thematic/"+pageNo;
        OkGo.<String>get(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                             List<Thematic> list = JsonUtils.getThematics(resJson);
                            mListener.thematic(list);
                        }else{
                            mListener.thematic(null);
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mListener.thematic(null);
                    }
                });
        return null;
    }

    public interface IGetThematicListener{
        void thematic(List<Thematic> list);
    }

    public void setmListener(IGetThematicListener mListener) {
        this.mListener = mListener;
    }
}
