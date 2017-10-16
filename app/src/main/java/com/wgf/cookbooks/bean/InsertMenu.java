package com.wgf.cookbooks.bean;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 插入到sqlite的菜谱
 */
public class InsertMenu {
    private int id;
    private String mainIcon;
    private String menuName;
    private String menuDesc;
    private Integer menuType;
    private Integer menuTypeSun;

    public InsertMenu(int id,String mainIcon, String menuName, String menuDesc, Integer menuType, Integer menuTypeSun) {
        this.id = id;
        this.mainIcon = mainIcon;
        this.menuName = menuName;
        this.menuDesc = menuDesc;
        this.menuType = menuType;
        this.menuTypeSun = menuTypeSun;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDesc() {
        return menuDesc;
    }

    public void setMenuDesc(String menuDesc) {
        this.menuDesc = menuDesc;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public Integer getMenuTypeSun() {
        return menuTypeSun;
    }

    public void setMenuTypeSun(Integer menuTypeSun) {
        this.menuTypeSun = menuTypeSun;
    }
}
