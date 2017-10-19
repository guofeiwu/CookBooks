package com.wgf.cookbooks.bean;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class AppVer {
    private float ver;//版本号
    private String downloadUrl;//下载地址
    private String verDesc;//版本的描述

    public float getVer() {
        return ver;
    }

    public void setVer(float ver) {
        this.ver = ver;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVerDesc() {
        return verDesc;
    }

    public void setVerDesc(String verDesc) {
        this.verDesc = verDesc;
    }
}
