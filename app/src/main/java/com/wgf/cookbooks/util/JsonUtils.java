package com.wgf.cookbooks.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

}
