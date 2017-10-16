package com.wgf.cookbooks.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.InsertMenu;
import com.wgf.cookbooks.clazz.UpMenuCoverAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.ToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_ALBUM;
import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_CAMERA;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 添加菜谱界面
 */
public class AddMenuActivity extends AppCompatActivity implements View.OnClickListener, UpMenuCoverAsyncTask.IUpMenuCoverListener {
    private ImageView mImageViewCover;
    private TextView mNextStep;
    private RelativeLayout mCoverLayout;
    private String coverPath = null;
    private EditText mMenuName;//菜谱名称
    private EditText mMenuDesc;//菜谱描述
    private TextView menuNameLength;
    private TextView menuDescLength;
    private TextView menuType;
    private PopupWindow pw;
    private PopupWindow pw2;
    private TextView menuSunType;
    private UpMenuCoverAsyncTask mUpMenuCoverAsyncTask;
    private SqliteDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        initView();
        setListener();

        addWatch();

        dao = new SqliteDao(this);

    }

    /**
     * EditText添加监听事件
     */
    private void addWatch() {
        mMenuName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                menuNameLength.setText(length + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mMenuDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                menuDescLength.setText(length + "/2000");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 绑定控件
     */
    private void initView() {
        mImageViewCover = (ImageView) findViewById(R.id.id_iv_cover);
        mNextStep = (TextView) findViewById(R.id.id_tv_next_step);
        mCoverLayout = (RelativeLayout) findViewById(R.id.id_rl_cover);
        mMenuName = (EditText) findViewById(R.id.id_et_menu_name);
        mMenuDesc = (EditText) findViewById(R.id.id_et_menu_desc);
        menuNameLength = (TextView) findViewById(R.id.id_tv_menu_length);
        menuDescLength = (TextView) findViewById(R.id.id_tv_menu_desc_length);
        menuType = (TextView) findViewById(R.id.id_tv_type);
        menuSunType = (TextView) findViewById(R.id.id_tv_sun_type);
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mNextStep.setOnClickListener(this);
        mCoverLayout.setOnClickListener(this);
        menuType.setOnClickListener(this);
    }



    /**
     * 请求权限
     * @param permission
     */
    private void  requestPermission(String [][] permission,int requestCode){

        AndPermission.with(AddMenuActivity.this)
                .requestCode(requestCode)
                .permission(permission)
                .callback(listener)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        new AlertDialog.Builder(AddMenuActivity.this)
                                .setTitle("友好提醒")
                                .setMessage("为了更换您满意的封面，请把读写权限赐给我吧！")
                                .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        rationale.resume();// 用户同意继续申请。
                                    }
                                })
                                .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        rationale.cancel(); // 用户拒绝申请。
                                    }
                                }).show();
                    }
                })
                .start();
    }

    /**
     * 读写权限回调
     */
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // Successfully.
            if(requestCode == REQUEST_CODE_ALBUM){
                //从相册获取图片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // Failure.
            if(requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_ALBUM) {
                if(AndPermission.hasAlwaysDeniedPermission(AddMenuActivity.this, deniedPermissions)){
                    AndPermission.defaultSettingDialog(AddMenuActivity.this, REQUEST_CODE_CAMERA)
                            .setTitle("权限申请失败")
                            .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                            .setPositiveButton("好，去设置")
                            .show();
//                    ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_permission_failed));
//                    SettingService settingService = AndPermission.defineSettingDialog(UserInfoActivity.this, REQUEST_CODE_CAMERA);
////                    你的dialog点击了确定调用：
//                    settingService.execute();
////                    你的dialog点击了取消调用：
//                    settingService.cancel();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_ALBUM) {

                Uri uriSelected = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = this.getContentResolver().query(uriSelected, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                coverPath = cursor.getString(columnIndex);
                cursor.close();
                compressIcon(coverPath);
                uploadDialog();
            }

        }
    }

    //鲁班压缩图片
    private void compressIcon(final String iconPath) {
        final File currentFile = new File(iconPath);
        Luban.with(AddMenuActivity.this)
                .load(currentFile)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Glide.with(AddMenuActivity.this)
                                .load(file)
                                .fitCenter()
                                .into(mImageViewCover);
                        if (mUpMenuCoverAsyncTask != null) {
                            return;
                        }
                        mUpMenuCoverAsyncTask = new UpMenuCoverAsyncTask(AddMenuActivity.this);
                        mUpMenuCoverAsyncTask.setmListener(AddMenuActivity.this);
                        mUpMenuCoverAsyncTask.execute(iconPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();//启动压缩

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_next_step:
                nextStep();
                break;
            case R.id.id_rl_cover:
                String [][] permission = new String[][]{Permission.STORAGE};
                requestPermission(permission,REQUEST_CODE_ALBUM);
                //从相册获取图片
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            case R.id.id_tv_type:
                choseType();
                break;
        }
    }

    /**
     * 下一步
     */
    private void nextStep() {

        if (TextUtils.isEmpty(coverPath)) {
            ToastUtils.toast(this, getString(R.string.text_menu_cover));
            return;
        }

        String menuName = mMenuName.getText().toString().trim();
        if (TextUtils.isEmpty(menuName)) {
            ToastUtils.toast(this, getString(R.string.text_menu_name));
            return;
        }

        String menuT = menuType.getText().toString();
        String menuST = menuSunType.getText().toString();
        if (menuT.equals("请选择") || menuST.equals("请选择")) {
            ToastUtils.toast(this, getString(R.string.text_menu_type));
            return;
        }
        String desc = mMenuDesc.getText().toString().trim();
        if (TextUtils.isEmpty(desc)) {
            ToastUtils.toast(this, getString(R.string.text_menu_desc));
            return;
        }


        int menuType = choosePType(menuT);
        int menuSunType = chooseSType(menuT, menuST);

        L.e("content:" + coverPath + "," + menuName + "," + menuT + "," + desc + "," + menuType + "," + menuSunType);

        dao.deleteMenuInfo();//清空表
        InsertMenu insertMenu = new InsertMenu(1,mainIcon,menuName, desc, menuType, menuSunType);
        int result = dao.insertMenuInfo(insertMenu);
        if (result == 1) {
            InsertMenu menu = dao.queryMenuInfo();
            L.e("menu:" + menu.getMenuName());
            IntentUtils.jump(AddMenuActivity.this, AddMenuMaterialActivity.class);
        }
    }


    private Dialog dialog;

    //上传对话框
    private void uploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.upload_dialog);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 选择父类
     *
     * @param type
     * @return
     */
    private int choosePType(String type) {
        int t = 0;
        if (type.equals("一日三餐")) {
            t = 0;
        } else if (type.equals("菜式")) {
            t = 1;
        } else if (type.equals("菜系")) {
            t = 2;
        } else if (type.equals("烘焙甜点")) {
            t = 3;
        } else {//主食
            t = 4;
        }
        return t;
    }

    /**
     * 选择子类
     *
     * @param type
     * @param sunType
     * @return
     */
    private int chooseSType(String type, String sunType) {
        int t = 0;
        if (type.equals("一日三餐")) {
            if (sunType.equals("早餐")) {
                t = 0;
            } else if (sunType.equals("中餐")) {
                t = 1;
            } else if (sunType.equals("晚餐")) {
                t = 2;
            } else if (sunType.equals("夜宵")) {
                t = 3;
            }
        } else if (type.equals("菜式")) {
            if (sunType.equals("家常菜")) {
                t = 0;
            } else if (sunType.equals("素菜")) {
                t = 1;
            } else if (sunType.equals("汤")) {
                t = 2;
            } else if (sunType.equals("凉菜")) {
                t = 3;
            } else if (sunType.equals("私房菜")) {
                t = 4;
            } else if (sunType.equals("荤菜")) {
                t = 5;
            }
        } else if (type.equals("菜系")) {
            if (sunType.equals("川菜")) {
                t = 0;
            } else if (sunType.equals("粤菜")) {
                t = 1;
            } else if (sunType.equals("东北菜")) {
                t = 2;
            } else if (sunType.equals("湘菜")) {
                t = 4;
            } else if (sunType.equals("鲁菜")) {
                t = 5;
            } else if (sunType.equals("清真")) {
                t = 6;
            }

        } else if (type.equals("烘焙甜点")) {
            if (sunType.equals("蛋糕")) {
                t = 0;
            } else if (sunType.equals("饼干")) {
                t = 1;
            } else if (sunType.equals("蛋挞")) {
                t = 2;
            } else if (sunType.equals("饮品")) {
                t = 3;
            }
        } else {//主食
            if (sunType.equals("饭")) {
                t = 0;
            } else if (sunType.equals("面")) {
                t = 1;
            } else if (sunType.equals("糕点")) {
                t = 2;
            } else if (sunType.equals("粥")) {
                t = 3;
            } else if (sunType.equals("米粉")) {
                t = 4;
            }
        }
        return t;
    }


    /**
     * 选择类型
     */
    private void choseType() {
        View pwView = View.inflate(this, R.layout.type_layout, null);
        pw = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //触摸空白处会窗体消失
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);

        LinearLayout layout = (LinearLayout) pwView;
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextView) {
                final TextView textView = (TextView) view;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = textView.getText().toString();
                        //ToastUtils.toast(AddMenuActivity.this, text);
                        sunTypePopupWindow(text);
                        pw.dismiss();
                    }
                });
            }
        }

        //显示popupwindow
        if (pw != null && !pw.isShowing()) {
            L.e("showing");
            pw.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
        }

    }

    /**
     * 子类选择
     *
     * @param type
     */
    private void sunTypePopupWindow(String type) {
        if (type.equals("一日三餐")) {
            String[] threeMealsTitles = {"早餐", "中餐", "晚餐", "夜宵"};
            generateItem(threeMealsTitles);
        } else if (type.equals("菜式")) {
            String[] caiShiTitles = {"家常菜", "素菜", "汤", "凉菜", "私房菜", "荤菜"};
            generateItem(caiShiTitles);
        } else if (type.equals("菜系")) {
            String[] caiXiTitles = {"川菜", "粤菜", "东北菜", "湘菜", "鲁菜", "清真"};
            generateItem(caiXiTitles);
        } else if (type.equals("烘焙甜点")) {
            String[] tianDianTitles = {"蛋糕", "饼干", "蛋挞", "饮品"};
            generateItem(tianDianTitles);
        } else {//主食
            String[] zhuShiTitles = {"饭", "面", "糕点", "粥", "米粉"};
            generateItem(zhuShiTitles);
        }
        menuType.setText(type);
        menuSunType.setVisibility(View.VISIBLE);
        menuSunType.setText("请选择");
    }


    /**
     * 生成子类项
     *
     * @param types
     */
    public void generateItem(final String[] types) {
        View view = View.inflate(this, R.layout.sun_type_layout, null);
        GridView gridView = (GridView) view.findViewById(R.id.id_gv_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.type_layout_item, R.id.id_tv_type, types);

        gridView.setAdapter(adapter);
        pw2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        if (pw2 != null && !pw2.isShowing()) {
            pw2.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuSunType.setText(types[position]);
                pw2.dismiss();
            }
        });
    }

    private String mainIcon;
    @Override
    public void result(String mainIconUrl) {
        if (dialog.isShowing() && dialog != null) {
            dialog.dismiss();
        }
        if (mainIconUrl != null) {
            mainIcon = mainIconUrl;
//            Intent intent = new Intent(AddMenuActivity.this, AddMenuMaterialActivity.class);
//            intent.putExtra("menuPkId",mainIcon);
//            startActivity(intent);
            //IntentUtils.jump(AddMenuActivity.this, AddMenuMaterialActivity.class);
            ToastUtils.toast(this, mainIconUrl);
        } else {
            ToastUtils.toast(this, getString(R.string.text_failed_msg));
        }

        if (mUpMenuCoverAsyncTask != null) {
            mUpMenuCoverAsyncTask = null;
        }
    }
}
