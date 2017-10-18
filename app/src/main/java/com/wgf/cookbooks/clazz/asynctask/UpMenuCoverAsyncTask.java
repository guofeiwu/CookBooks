package com.wgf.cookbooks.clazz.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import java.io.File;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 用户上传封面，等总的描述信息
 */
public class UpMenuCoverAsyncTask extends AsyncTask<String,Void,Void> {
    private Context context;
    private IUpMenuCoverListener mListener;
    public UpMenuCoverAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        String filePath = params[0];
        File file = new File(filePath);
        String url = BASE_URL+"/app/menu/upCoverTwo";
        //String menuName,String menuDesc,Integer menuType,Integer menuTypeSun,
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .params("cover",file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        if(code == SUCCESS){
                            String mainIcon = JsonUtils.getMenuMainIcon(resJson);
                            mListener.result(mainIcon);
                        }else{
                            //失败就返回null
                            mListener.result(null);
                        }
                    }
                });
        return null;
    }

    public interface IUpMenuCoverListener{
        void result(String mainIcon);
    }

    public void setmListener(IUpMenuCoverListener mListener) {
        this.mListener = mListener;
    }
}
