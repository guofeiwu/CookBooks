package com.wgf.cookbooks.util;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class Constants {
      /**住宿的**/
    public static final String BASE_URL = "http://192.168.11.103:8081";
    public static final String BASE_URL_FILE_MENUS = "http://192.168.11.103:8080/menuFiles/menus/";//Tomcat服务器菜单文件地址
    public static final String BASE_URL_FILE_ICON = "http://192.168.11.103:8080/menuFiles/icon/";//Tomcat服务器用户头像地址
    public static final String BASE_URL_FILE_SHAI = "http://192.168.11.103:8080/menuFiles/shai/";//晒一晒地址



    /**公司的**/
//    public static final String BASE_URL = "http://192.168.1.155:8081";
//    public static final String BASE_URL_FILE_ICON = "http://192.168.1.155:8080/menuFiles/icon/";//Tomcat服务器用户头像地址
//    public static final String BASE_URL_FILE_MENUS = "http://192.168.1.155:8080/menuFiles/menus/";//Tomcat服务器菜单地址
//    public static final String BASE_URL_FILE_SHAI = "http://192.168.1.155:8080/menuFiles/shai/";//晒一晒地址




    public static  int SUCCESS = 200;//成功
    public static  int FAILED = 100;//失败

    public static String TEL_REGEX = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
    public static final int REGISTER_ERROR_CODE = 1003;//该手机号已经被注册
    public static final int LOGIN_ERROR_CODE = 1004;//用户不存在

    public static final int MINE_REQUEST_CODE=101;


    public static final String LOGIN_FLAG_MIME = "mine";//我的

    public static final boolean IS_SHOW = false;//登录，判断有没有加载数据
    public static final String SHOW_DATA = "showUserData";//显示用户数据


    public static final String SIGN = "已签到";
    public static final String NOT_SIGN="签到";

    public static final String AUTHORIZATION = "Authorization";


    public static final int REQUEST_CODE_ALBUM = 0;//从相册选择
    public static final int REQUEST_CODE_CAMERA = 1;//拍照

//    public static final String  BREAKFAST = "早餐";
//    public static final String  LUNCH = "中餐";
//    public static final String  DINNER = "晚餐";
//    public static final String  NIGHT_SNACK = "夜宵";

    public static final String YIRISANCAN = "一日三餐";
    public static final String CAISHI ="菜式";
    public static final String CAIXI="菜系";
    public static final String TIANDIAN ="烘焙甜点";
    public static final String ZHUSHI="主食";





}
