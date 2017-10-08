package com.wgf.cookbooks.bean;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 评论实体类
 */
public class Comment {
    private int commnetPkId;
    private String userIconUrl;
    private String userName;
    private String content;
    private String commentTime;
    private int currentUser;//是否是当前用户，0表示是，-1表示不是

    public int getCommnetPkId() {
        return commnetPkId;
    }

    public void setCommnetPkId(int commnetPkId) {
        this.commnetPkId = commnetPkId;
    }

    public int getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
