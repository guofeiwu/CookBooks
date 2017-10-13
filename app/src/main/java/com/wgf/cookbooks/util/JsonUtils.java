package com.wgf.cookbooks.util;

import com.wgf.cookbooks.bean.Banner;
import com.wgf.cookbooks.bean.Comment;
import com.wgf.cookbooks.bean.Materials;
import com.wgf.cookbooks.bean.Menu;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.bean.Step;
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
     * 返回附加的内容（返回一个pkId,点赞和收藏成功）
     * @param response
     * @return
     */
    public static int getPkId(String response){
        int pkId = 0;
        Map map = new HashMap();
        try {
            JSONObject jsonObject = new JSONObject(response);
            pkId = jsonObject.getJSONObject("extend").getJSONObject("content").getInt("pkId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pkId;
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
     * 返回附加的内容（单个Comment）
     * @param response
     * @return
     */
    public static Comment getComment(String response){
        Comment comment = null;
        try {
                comment = new Comment();
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jo = jsonObject.getJSONObject("extend").getJSONObject("content");
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }





    /**
     * 返回附加的内容（菜单集合）
     * @param response
     * @return
     */
    public static List<Menu> getMenusList(String response){
        List<Menu> menus = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONObject("extend").getJSONArray("content");
            menus = new ArrayList<>();

            for (int i = 0;i<jsonArray.length();i++){
                Menu menu  = new Menu();
                JSONObject jo = jsonArray.getJSONObject(i);
                menu.setMenuPkId(jo.getInt("menuPkId"));
                menu.setMenuName(jo.getString("menuName"));
                menu.setMainIcon(jo.getString("mainIcon"));
                menu.setIntroduce(jo.getString("introduce"));
                menu.setUserName(jo.getString("userName"));
                menu.setUserIconUrl(jo.getString("userIconUrl"));
                menu.setCurrentUser(jo.getInt("currentUser"));
                menus.add(menu);
            }

            return menus;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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

    /**
     * 返回附加的内容 （获取菜谱详情）
     * @param response
     * @return
     */
    public static Menu getMenuDetail(String response){
        Menu menu = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jo = jsonObject.getJSONObject("extend").getJSONObject("content");
            menu = new Menu();

            int menuPkId = jo.getInt("menuPkId");
            String menuName = jo.getString("menuName");
            String mainIcon = jo.getString("mainIcon");
            String introduce = jo.getString("introduce");
            String userName = jo.getString("userName");
            String userIconUrl = jo.getString("userIconUrl");
            int currentUser = jo.getInt("currentUser");

            int likeTotal = jo.getInt("likeTotal");
            int commentTotal = jo.getInt("commentTotal");
            int collectTotal = jo.getInt("collectTotal");
            int currentLike = jo.getInt("currentLike");
            int currentCollect = jo.getInt("currentCollect");
            int likePkId = jo.getInt("likePkId");
            int collectPkId = jo.getInt("collectPkId");



            JSONArray steps = jo.getJSONArray("steps");
            List<Step> stepList = new ArrayList<>();
            for(int i = 0;i<steps.length();i++){
                Step step = new Step();
                JSONObject jsonObject1 = steps.getJSONObject(i);
                String stepDesc = jsonObject1.getString("stepDesc");
                String stepIcon = jsonObject1.getString("stepIcon");
                step.setStepDesc(stepDesc);
                step.setStepIcon(stepIcon);
                stepList.add(step);
            }

            JSONArray materials = jo.getJSONArray("materials");
            List<Materials> materialsList = new ArrayList<>();
            for(int i = 0;i<materials.length();i++){
                Materials materials1 = new Materials();
                JSONObject jsonObject1 = materials.getJSONObject(i);
                String materialName = jsonObject1.getString("materialName");
                materials1.setMaterialsName(materialName);
                materialsList.add(materials1);
            }
            menu.setMenuPkId(menuPkId);
            menu.setMainIcon(mainIcon);
            menu.setMenuName(menuName);
            menu.setUserName(userName);
            menu.setIntroduce(introduce);
            menu.setUserIconUrl(userIconUrl);
            menu.setCurrentUser(currentUser);

            menu.setSteps(stepList);
            menu.setMaterials(materialsList);

            menu.setLikeTotal(likeTotal);
            menu.setCommentTotal(commentTotal);
            menu.setCollectTotal(collectTotal);
            menu.setCurrentCollect(currentCollect);
            menu.setCurrentLike(currentLike);
            menu.setLikePkId(likePkId);
            menu.setCollectPkId(collectPkId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return menu;
    }



    /**
     * 返回附加的内容 （Banner列表）
     * @param response
     * @return
     */
    public static List<Banner> getBanners(String response){
        List<Banner> banners = null;

        try {
            JSONObject jsonObject  = new JSONObject(response);
            JSONArray jo = jsonObject.getJSONObject("extend").getJSONArray("content");
            banners = new ArrayList<>();
            for (int i = 0;i<jo.length();i++){
                Banner banner = new Banner();
                JSONObject j = jo.getJSONObject(i);
                int menuPkId = j.getInt("menuPkId");
                String menuDesc = j.getString("menuDesc");
                String mainIcon = j.getString("mainIcon");
                banner.setMenuPkId(menuPkId);
                banner.setMenuDesc(menuDesc);
                banner.setMainIcon(mainIcon);
                banners.add(banner);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return banners;
    }

}
