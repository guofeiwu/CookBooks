package com.wgf.cookbooks.util;

import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.bean.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class JsonUtils {
    /**
     * 获取返回的总code
     * @param response 返回的字符串
     * @return
     */
    public static int getCode(String response){
        int code = 100;
        try {
            JSONObject jsonObject = new JSONObject(response);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 返回附加的内容（单个JsonObject）
     * @param response
     * @return
     */
    public static JSONObject getContent(String response){
        JSONObject content = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            content = jsonObject.getJSONObject("extend").getJSONObject("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 返回附加的内容（String）
     * @param response
     * @return
     */
    public static String getStringContent(String response){
        String content = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            content = jsonObject.getJSONObject("extend").getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }



    /**
     * 返回附加的内容（点赞主键，晒晒主键）
     * @param response
     * @return
     */
    public static Map getMaps(String response){
        Map content = null;
        Map map = new HashMap();
        try {
            JSONObject jsonObject = new JSONObject(response);
            content = (Map) jsonObject.getJSONObject("extend").get("content");
            L.e("co"+content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }




    /**
     * 返回附加的内容（JsonArray）
     * @param response
     * @return
     */
    public static JSONArray getJsonArray(String response){
        JSONArray content = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            content = jsonObject.getJSONObject("extend").getJSONArray("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * 返回附加的内容（list）
     * @param response
     * @return
     */
    public static String getList(String response){
        String content = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            content = jsonObject.getJSONObject("extend").getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 返回附加的内容（Shai对象的集合）
     * @param response
     * @return
     */
    public static List<Shai> getShaiList(String response){
        List<Shai>  shais = null;
        try {
            shais = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONObject("extend").getJSONArray("content");
            for (int i = 0;i<jsonArray.length();i++){
                Shai shai = new Shai();
                JSONObject jo = jsonArray.getJSONObject(i);
                int pkId = jo.getInt("pkid");
                String userName = jo.getString("userName");
                String icon = jo.getString("icon");
                String descr = jo.getString("descr");
                String address = jo.getString("address");//晒一晒图片地址
                String time  =jo.getString("cTime");//创建时间
                int likes = jo.getInt("shaiLike");
                int commentTotal = jo.getInt("commentTotal");//评论的总数
                int lookTotal = jo.getInt("lookTotal");

                shai.setShaiPkId(pkId);
                shai.setUserName(userName);
                shai.setAddress(address);
                shai.setIcon(icon);
                shai.setDescr(descr);
                shai.setTime(time);
                shai.setLikes(likes);
                shai.setCommentTotal(commentTotal);
                shai.setLookTotal(lookTotal);
                shais.add(shai);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shais;
    }



    /**
     * 返回附加的内容（用户信息UserInfo）
     * @param response
     * @return
     */
    public static UserInfo getUserInfo(String response){
        UserInfo userInfo = null;
        userInfo = new UserInfo();
        JSONObject jsonUserInfo = getContent(response);
        try{
            userInfo.setUserName(jsonUserInfo.getString("name"));
            userInfo.setSex(jsonUserInfo.getInt("sex")==0?"女":"男");
            userInfo.setBirthday(jsonUserInfo.getString("birthday"));
            userInfo.setPoint(jsonUserInfo.getInt("point"));
            userInfo.setLevel(jsonUserInfo.getString("level"));
            userInfo.setIcon(BASE_URL_FILE_ICON+jsonUserInfo.getString("icon"));

        }catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }


    /**
     * 返回附加的内容（Comment集合）
     * @param response
     * @return
     */
    public static List<Comment> getCommentsList(String response){
        List<Comment> comments = null;
        try {
            comments = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONObject("extend").getJSONArray("content");
            for (int i = 0;i<jsonArray.length();i++){
                Comment comment = new Comment();
                JSONObject jo = jsonArray.getJSONObject(i);
                String content = jo.getString("content");
                String commentTime = jo.getString("commentTime");
                String userIconUrl = jo.getString("userIconUrl");
                String username = jo.getString("username");
                int currentUser = jo.getInt("currentUser");
                int commentPkId = jo.getInt("commentPkId");
                comment.setContent(content);
                comment.setCommentTime(commentTime);
                comment.setUserIconUrl(userIconUrl);
                comment.setUserName(username);
                comment.setCurrentUser(currentUser);
                comment.setCommnetPkId(commentPkId);
                comments.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comments;
    }


    /**
     * 返回附加的内容（单个Shai）
     * @param response
     * @return
     */
    public static Shai getShaiDetail(String response){
        Shai shai = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jo = jsonObject.getJSONObject("extend").getJSONObject("content");
            shai = new Shai();
            int pkId = jo.getInt("pkid");
            String userName = jo.getString("userName");
            String icon = jo.getString("icon");
            String descr = jo.getString("descr");
            String address = jo.getString("address");//晒一晒图片地址
            String time  =jo.getString("cTime");//创建时间
            int likes = jo.getInt("shaiLike");
            int commentTotal = jo.getInt("commentTotal");//评论的总数
            int lookTotal = jo.getInt("lookTotal");
            int currentUser = jo.getInt("currentUser");

            shai.setShaiPkId(pkId);
            shai.setUserName(userName);
            shai.setAddress(address);
            shai.setIcon(icon);
            shai.setDescr(descr);
            shai.setTime(time);
            shai.setLikes(likes);
            shai.setCommentTotal(commentTotal);
            shai.setLookTotal(lookTotal);
            shai.setCurrentUser(currentUser);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shai;
    }

}
