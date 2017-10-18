package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 更换手机号码
 */
public class ModifyPhoneAsyncTask extends AsyncTask<String,Void,Void> {
    private Context context;
    private IModifyPhoneListener mListener;
    public ModifyPhoneAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String phone = params[0];
        Map<String,Object> map = new HashMap<>();
        map.put("newPhone",phone);
        JSONObject jsonObject  = new JSONObject(map);
        String url = BASE_URL+"/app/user/modifyPhone";
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        //int code = JsonUtils.getCode(resJson);
                        int sonCode = JsonUtils.getSonCode(resJson);
                        mListener.result(sonCode);
                    }
                });
        return null;
    }

    public interface IModifyPhoneListener{
        void result(int code);
    }

    public void setmListener(IModifyPhoneListener mListener) {
        this.mListener = mListener;
    }
}
