package com.wgf.cookbooks.bean;

import java.io.Serializable;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class Thematic implements Serializable{
    private String thematicPictureUrl;
    private String thematicName;

    public String getThematicPictureUrl() {
        return thematicPictureUrl;
    }

    public void setThematicPictureUrl(String thematicPictureUrl) {
        this.thematicPictureUrl = thematicPictureUrl;
    }

    public String getThematicName() {
        return thematicName;
    }

    public void setThematicName(String thematicName) {
        this.thematicName = thematicName;
    }
}
