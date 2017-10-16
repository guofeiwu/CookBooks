package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 上传菜谱步骤图片
 */
public class UpMenuStepPictureAsyncTask extends AsyncTask<List<String>,Void,Void> {
    private Context context;
    private IUpMenuStepPictureListener mListener;
    public UpMenuStepPictureAsyncTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(List<String>... params) {

        List<String> urls = params[0];
        List<File> files = new ArrayList<>();
        for(String url:urls){
            File file = new File(url);
            files.add(file);
        }
        String url = BASE_URL+"/app/menu/upStepPicture";
        //String menuName,String menuDesc,Integer menuType,Integer menuTypeSun,
        OkGo.<String>post(url)
                .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                .addFileParams("stepPicture",files)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String resJson = response.body().toString();
                        int code = JsonUtils.getCode(resJson);
                        L.e("resJosn:"+resJson);
                        if(code == SUCCESS){
                            List<String> urls = JsonUtils.getMenuStepUrl(resJson);
                            mListener.result(urls);
                        }else{
                            //失败就返回null
                            mListener.result(null);
                        }
                    }
                });
        return null;
    }

    public interface IUpMenuStepPictureListener{
        void result(List<String> urls);
    }

    public void setmListener(IUpMenuStepPictureListener mListener) {
        this.mListener = mListener;
    }
}
