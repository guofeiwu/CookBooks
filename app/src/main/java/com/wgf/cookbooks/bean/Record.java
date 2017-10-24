package com.wgf.cookbooks.bean;

import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 菜谱记录
 */
public class Record {
    private int recordPkId;
    private Menu menus;

    public int getRecordPkId() {
        return recordPkId;
    }

    public void setRecordPkId(int recordPkId) {
        this.recordPkId = recordPkId;
    }

    public Menu getMenus() {
        return menus;
    }

    public void setMenus(Menu menus) {
        this.menus = menus;
    }
}
