package com.wgf.cookbooks.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wgf.cookbooks.bean.InsertMaterials;
import com.wgf.cookbooks.bean.InsertMenu;
import com.wgf.cookbooks.bean.InsertStep;

import java.util.ArrayList;
import java.util.List;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class SqliteDao {
    private MySqliteHelper helper;
    private Context context;

    public SqliteDao(Context context) {
        this.context = context;
        helper = MySqliteHelper.getHelper(context);
    }


    // TODO: 2017/10/15  点击下一步，清空表，插入数据
    public int  insertMenuInfo(InsertMenu insertMenu){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("Id",insertMenu.getId());
        values.put("mainIcon",insertMenu.getMainIcon());
        values.put("menuName",insertMenu.getMenuName());
        values.put("menuDesc",insertMenu.getMenuDesc());
        values.put("menuType",insertMenu.getMenuType());
        values.put("menuTypeSun",insertMenu.getMenuTypeSun());
        int result = (int) db.insert(helper.MENU_INFO,null,values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return result;
    }


    /**
     * 获取菜谱信息
     * @return
     */
    public InsertMenu queryMenuInfo(){
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(helper.MENU_INFO,null,null,null,null,null,null,null);
        InsertMenu insertMenu = null;
        if (cursor.moveToNext()){ // TODO: 2017/10/16 这里有问题
            int id = cursor.getInt(cursor.getColumnIndex("Id"));
            String mainIcon = cursor.getString(cursor.getColumnIndex("mainIcon"));
            String menuName = cursor.getString(cursor.getColumnIndex("menuName"));
            String menuDesc = cursor.getString(cursor.getColumnIndex("menuDesc"));
            int menuType = cursor.getInt(cursor.getColumnIndex("menuType"));
            int menuTypeSun = cursor.getInt(cursor.getColumnIndex("menuTypeSun"));
            insertMenu = new InsertMenu(id,mainIcon,menuName,menuDesc,menuType,menuTypeSun);
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return insertMenu;
    }

    /**
     * 插入材料信息
     * @return
     */
    public int  insertMenuMaterial(List<InsertMaterials> insertMaterials){//这个应该插入一个集合，回来再修改
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = 0;
        db.beginTransaction();
        for(InsertMaterials insertMaterial:insertMaterials){
            ContentValues values = new ContentValues();
            values.put("Id",insertMaterial.getId());
            values.put("materialsName",insertMaterial.getMaterialsName());
            values.put("materialsDose",insertMaterial.getMaterialsDose());
            result = (int) db.insert(helper.MENU_MATERIALS,null,values);
            if(result==-1){
                //有不等于1说明有数据没插入成功
                break;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return result;
    }


    /**
     * 获取步骤信息
     * @return
     */
    public List<InsertMaterials> queryMenuMaterials(){
        List<InsertMaterials> materialses = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.query(helper.MENU_MATERIALS,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            InsertMaterials materials = new InsertMaterials();
            int id = cursor.getInt(cursor.getColumnIndex("Id"));
            String materialsName = cursor.getString(cursor.getColumnIndex("materialsName"));
            String materialsDose = cursor.getString(cursor.getColumnIndex("materialsDose"));

            materials.setId(id);
            materials.setMaterialsDose(materialsDose);
            materials.setMaterialsName(materialsName);
            materialses.add(materials);
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return materialses;
    }


    /**
     * 插入步骤
     * @return
     */
    public int  insertMenuStep(List<InsertStep> steps){
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = 0;
        db.beginTransaction();
        for(InsertStep step:steps){
            ContentValues values = new ContentValues();
            values.put("Id",step.getId());
            values.put("stepUrl",step.getStepUrl());
            values.put("stepDesc",step.getStepDesc());
            result = (int) db.insert(helper.MENU_STEP,null,values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return result;
    }



    /**
     * 清空表
     */
    public void deleteMenuInfo(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " +helper.MENU_INFO);
        db.close();
    }
    public void deleteMenuMaterials(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " +helper.MENU_MATERIALS);
        db.close();
    }
    public void deleteMenuStep(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " +helper.MENU_STEP);
        db.close();
    }
}
