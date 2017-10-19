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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.clazz.asynctask.UploadShaiAsyncTask;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_ALBUM;
import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_CAMERA;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 用户发布晒晒
 */
public class AddShaiActivity extends AppCompatActivity implements View.OnClickListener,UploadShaiAsyncTask.IUploadShaiListener{
    private CustomToolbar mCustomToolbar;
    private ImageView mImageViewShai;
    private EditText mEditTextIntroduce;
    private TextView mTextViewRelease;
    private PopupWindow pw;
    private String iconPath;//图片路径
    private boolean havaImage = false;//是否有图片
    private UploadShaiAsyncTask mUploadShaiAsyncTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shai);

        initView();

        initListener();
        initData();

    }

    /**
     * 初始化设置
     */
    private void initData() {
        mEditTextIntroduce.setFocusableInTouchMode(true);
        mEditTextIntroduce.setFocusable(false);
        SoftInputUtils.hideSoftInput(this);//隐藏软键盘
    }

    /**
     * 添加监听事件
     */
    private void initListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                confirmDialog();
                SoftInputUtils.hideSoftInput(AddShaiActivity.this);
            }
        });
        mImageViewShai.setOnClickListener(this);
        mTextViewRelease.setOnClickListener(this);
        mEditTextIntroduce.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_add_shai_back);
        mImageViewShai = (ImageView) findViewById(R.id.id_iv_shai_content);
        mEditTextIntroduce = (EditText) findViewById(R.id.id_et_introduce);
        mTextViewRelease = (TextView) findViewById(R.id.id_tv_release);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_et_introduce:
                mEditTextIntroduce.setFocusable(true);
                mEditTextIntroduce.setFocusableInTouchMode(true);
                mEditTextIntroduce.requestFocus();
                mEditTextIntroduce.findFocus();
                SoftInputUtils.showSoftInput1(this);
                break;
            case R.id.id_iv_shai_content:
                addshai();
                break;
            case R.id.id_tv_release:

                if(!havaImage){
                    ToastUtils.toast(AddShaiActivity.this,"请添加晒晒图片");
                    return;
                }

                String introduce = mEditTextIntroduce.getText().toString();
                if(TextUtils.isEmpty(introduce)){
                    ToastUtils.toast(AddShaiActivity.this,"说几句介绍一下你的晒晒吧");
                    return;
                }

                if(mUploadShaiAsyncTask!=null){
                    return;
                }

                //显示上传对话框
                uploadDialog();


                File file = new File(iconPath);
                mUploadShaiAsyncTask = new UploadShaiAsyncTask(AddShaiActivity.this,introduce);
                mUploadShaiAsyncTask.setmListener(AddShaiActivity.this);
                mUploadShaiAsyncTask.execute(file);

                break;
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
     * 添加晒一晒图片
     */
    private void addshai(){
        //晒一晒图片
        View addShaiView = getLayoutInflater().inflate(R.layout.change_icon, null);
        pw = new PopupWindow(addShaiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //触摸空白处会窗体消失
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);

        TextView tv_from_album = (TextView) addShaiView.findViewById(R.id.tv_from_album);
        TextView tv_take_photo = (TextView) addShaiView.findViewById(R.id.tv_take_photo);
        TextView tv_icon_cancel = (TextView) addShaiView.findViewById(R.id.tv_cancel);

        tv_from_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [][] permission = new String[][]{Permission.STORAGE};
                requestPermission(permission,REQUEST_CODE_ALBUM);
            }
        });
        tv_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [][] permission = new String[][]{Permission.CAMERA,Permission.STORAGE};
                requestPermission(permission,REQUEST_CODE_CAMERA);

            }
        });
        tv_icon_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //显示popupwindow
        if (pw != null && !pw.isShowing()) {
            L.e("showing");
            pw.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
           // pw.showAsDropDown(findViewById(R.id.layout));
        }
    }


    /**
     * 请求权限
     * @param permission
     */
    private void  requestPermission(String [][] permission,int requestCode){

        AndPermission.with(AddShaiActivity.this)
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
                        new AlertDialog.Builder(AddShaiActivity.this)
                                .setTitle("友好提醒")
                                .setMessage("为了更换发布您满意的晒晒，请把读写权限赐给我吧！")
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
            if(requestCode == REQUEST_CODE_CAMERA) {
                //拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoUri;
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    ToastUtils.toast(AddShaiActivity.this, "请插入内存卡");
                } else {
                    iconPath = Environment.getExternalStorageDirectory().getPath();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = sdf.format(new Date());
                    iconPath = iconPath + File.separator + date + ".jpg";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, new File(iconPath).getAbsolutePath());
                    photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                } else {
                    photoUri = Uri.fromFile(new File(iconPath));
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                dismiss();
            }else if(requestCode == REQUEST_CODE_ALBUM){
                //从相册获取图片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                dismiss();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // Failure.
            if(requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_ALBUM) {
                if(AndPermission.hasAlwaysDeniedPermission(AddShaiActivity.this, deniedPermissions)){
                    AndPermission.defaultSettingDialog(AddShaiActivity.this, REQUEST_CODE_CAMERA)
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
                dismiss();
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
                iconPath = cursor.getString(columnIndex);
                cursor.close();
                compressIcon(iconPath);
            }
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            compressIcon(iconPath);
        }
    }

    //鲁班压缩图片
    private void compressIcon(final String iconPath) {
        final File currentFile = new File(iconPath);
        Luban.with(AddShaiActivity.this)
                .load(currentFile)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                            Glide.with(AddShaiActivity.this)
                                    .load(file)
                                    .fitCenter()
                                    .into(mImageViewShai);
                        havaImage = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.toast(AddShaiActivity.this, "图片太大，请更换图片");
                        e.printStackTrace();
                    }

                }).launch();//启动压缩

    }

    /**
     * 关闭 popupWindow
     */
    private void dismiss() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
    }


    @Override
    public void result(int code) {
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        if(code == SUCCESS){
            ToastUtils.toast(AddShaiActivity.this,getString(R.string.text_release_success));
            mEditTextIntroduce.setText("");

            SpUtils.getEditor(this).putInt("addShai",1).commit();

            finish();
        }else{
            ToastUtils.toast(AddShaiActivity.this,getString(R.string.text_release_failed));
        }

        if(mUploadShaiAsyncTask!=null){
            mUploadShaiAsyncTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUploadShaiAsyncTask!=null){
            mUploadShaiAsyncTask = null;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        confirmDialog();
    }


    /**
     * 退出发布晒晒提示框
     */
    private void confirmDialog(){
        String content = mEditTextIntroduce.getText().toString();
        if(!TextUtils.isEmpty(content) || havaImage){
            //弹出是否要退出的对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("还有晒晒未上传，是否确定离开呢？");

            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else{
            finish();
        }

    }


}
