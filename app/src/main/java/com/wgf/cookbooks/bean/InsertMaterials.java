package com.wgf.cookbooks.bean;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 需要插入到sqlite数据中
 */
public class InsertMaterials {

    private int id;
    private String materialsName;
    private String materialsDose;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialsName() {
        return materialsName;
    }

    public void setMaterialsName(String materialsName) {
        this.materialsName = materialsName;
    }

    public String getMaterialsDose() {
        return materialsDose;
    }

    public void setMaterialsDose(String materialsDose) {
        this.materialsDose = materialsDose;
    }
}
