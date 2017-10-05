package com.wgf.cookbooks.util;

import com.wgf.cookbooks.bean.Shai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

                shai.setShaiPkId(pkId);
                shai.setUserName(userName);
                shai.setAddress(address);
                shai.setIcon(icon);
                shai.setDescr(descr);
                shai.setTime(time);
                shai.setLikes(likes);
                shais.add(shai);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shais;
    }


}
