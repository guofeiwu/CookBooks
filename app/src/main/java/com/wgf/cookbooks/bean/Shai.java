package com.wgf.cookbooks.bean;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 晒一晒实体类
 */
public class Shai {
    private int shaiPkId;
    private String userName;
    private String icon;
    private String descr;
    private String address;
    private String time;
    private int likes;//晒晒被点赞的次数
    public int getShaiPkId() {
        return shaiPkId;
    }

    public void setShaiPkId(int shaiPkId) {
        this.shaiPkId = shaiPkId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
