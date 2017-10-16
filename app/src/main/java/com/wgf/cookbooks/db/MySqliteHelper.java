package com.wgf.cookbooks.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 *
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "menu.db";
    public static final String MENU_INFO = "menuInfo";
    public static final String MENU_MATERIALS = "menuMaterials";
    public static final String MENU_STEP = "menuStep";


    private static MySqliteHelper helper;
    public static MySqliteHelper getHelper(Context context){
        if(helper == null)
            helper = new MySqliteHelper(context);
        return helper;
    }



    public MySqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "create table if not exists " + MENU_INFO + " (Id integer primary key, mainIcon text ,menuName text,menuDesc text, menuType integer, menuTypeSun integer)";
        String sql2= "create table if not exists " + MENU_MATERIALS + " (Id integer primary key, materialsName text,materialsDose text)";
        String sql3 = "create table if not exists " + MENU_STEP + " (Id integer primary key, stepUrl text,stepDesc text)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
