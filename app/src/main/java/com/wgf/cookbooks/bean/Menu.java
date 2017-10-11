package com.wgf.cookbooks.bean;

import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class Menu {
    private int menuPkId;
    private String menuName;
    private String mainIcon;//菜谱主图
    private String introduce;
    private String userName;
    private String userIconUrl;
    private int currentUser;
    private List<Step> steps;//步骤
    private List<Materials> materials ;//材料

    private int likeTotal;
    private int commentTotal;
    private int collectTotal;
    private int currentLike;
    private int currentCollect;

    private int likePkId;
    private int collectPkId;

    public int getLikePkId() {
        return likePkId;
    }

    public void setLikePkId(int likePkId) {
        this.likePkId = likePkId;
    }

    public int getCollectPkId() {
        return collectPkId;
    }

    public void setCollectPkId(int collectPkId) {
        this.collectPkId = collectPkId;
    }

    public int getLikeTotal() {
        return likeTotal;
    }

    public void setLikeTotal(int likeTotal) {
        this.likeTotal = likeTotal;
    }

    public int getCommentTotal() {
        return commentTotal;
    }

    public void setCommentTotal(int commentTotal) {
        this.commentTotal = commentTotal;
    }

    public int getCollectTotal() {
        return collectTotal;
    }

    public void setCollectTotal(int collectTotal) {
        this.collectTotal = collectTotal;
    }

    public int getCurrentLike() {
        return currentLike;
    }

    public void setCurrentLike(int currentLike) {
        this.currentLike = currentLike;
    }

    public int getCurrentCollect() {
        return currentCollect;
    }

    public void setCurrentCollect(int currentCollect) {
        this.currentCollect = currentCollect;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public int getMenuPkId() {
        return menuPkId;
    }

    public void setMenuPkId(int menuPkId) {
        this.menuPkId = menuPkId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public int getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }


    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Materials> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Materials> materials) {
        this.materials = materials;
    }
}
