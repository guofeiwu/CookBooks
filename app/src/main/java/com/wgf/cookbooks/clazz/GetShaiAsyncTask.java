package com.wgf.cookbooks.clazz;

import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONArray;


import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 获取晒一晒
 */
public class GetShaiAsyncTask extends AsyncTask<Integer, Void, Void> {
    private IGetShaiListener listener;

    public GetShaiAsyncTask(IGetShaiListener listener){
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        String url = BASE_URL + "/app/shai/all/" + params[0];
        try {
            OkGo.<String>get(url)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String resJosn = response.body().toString();
                            int code = JsonUtils.getCode(resJosn);
                            if(code == SUCCESS){
//                                JSONArray jsonArray = JsonUtils.getJsonArray(resJosn);
                                List<Shai> list = JsonUtils.getShaiList(resJosn);
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


    public interface IGetShaiListener{
        void getShaiList(List<Shai> lists);
    }





    public void setListener(IGetShaiListener listener) {
        this.listener = listener;
    }
}

