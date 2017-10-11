package com.wgf.cookbooks.clazz;

import android.content.Context;
import android.os.AsyncTask;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.util.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class GetCommentAsyncTask extends AsyncTask<Integer,Void,Void> {
    private ICommentListener mListener;
    private Context context;
    public GetCommentAsyncTask(Context context,ICommentListener mListener){
        this.context = context;
        this.mListener = mListener;
    }
    @Override
    protected Void doInBackground(Integer... params) {
        int pkId = params[0];
        int pageNo = params[1];
        Integer flag = null;
        int length = params.length;
        if(length>2){
            flag = params[2];//判断是获取晒晒评论还是菜谱评论
        }
        String url;
        if(flag !=null){//菜谱评论
            url = BASE_URL+"/app/menu/comment/"+pkId+"/page/"+pageNo;
        }else{
            url = BASE_URL +"/app/shai/comment/"+pkId+"/"+pageNo;
        }
        try {
            OkGo.<String>get(url)
                    .headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(context))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String resJson = response.body().toString();
                            int code = JsonUtils.getCode(resJson);
                            if(code == SUCCESS){
                                List<Comment> comments = JsonUtils.getCommentsList(resJson);
                                mListener.success(comments);
                            }else if (code == FAILED){
                                try {
                                    int result = JsonUtils.getContent(resJson).getInt("comment");
                                    mListener.fail(result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ICommentListener{
        void success(List<Comment> comments);
        void fail(int result);
    }

    public void setmListener(ICommentListener mListener) {
        this.mListener = mListener;
    }
}
