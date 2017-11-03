package com.wgf.cookbooks.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.UserInfo;
import com.wgf.cookbooks.util.GetAuthorizationUtil;
import com.wgf.cookbooks.clazz.asynctask.GetUserInfoAsyncTask;
import com.wgf.cookbooks.util.JsonUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.SpUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CircleImageView;
import com.wgf.cookbooks.view.CustomToolbar;
import com.wgf.cookbooks.view.UserInfoView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.wgf.cookbooks.util.Constants.AUTHORIZATION;
import static com.wgf.cookbooks.util.Constants.BASE_URL;
import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_ICON;
import static com.wgf.cookbooks.util.Constants.FAILED;
import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_ALBUM;
import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_CAMERA;
import static com.wgf.cookbooks.util.Constants.SHOW_DATA;
import static com.wgf.cookbooks.util.Constants.SUCCESS;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 个人资料
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private UserInfoView mUserName,mUserSex,mUserBirthday,mUserPoint,mUserLevel ;
    private ModifyUserInfoAsyncTask mModifyUserInfoAsyncTask;
    private String userName;//用户名
    private GetUserInfoAsyncTask mGetUserInfoAsyncTask;
    private CircleImageView mCircleImageView;
    private CustomToolbar mCustomToolbar;
    private PopupWindow pw;
    private TextView mChangeIcon;
    private String iconPath;
    private ModifyUserIconAsyncTask mModifyUserIconAsyncTask;
    private TextView mUploadProgress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_user_info);
        initView();
        initListener();

        initUserInfo();
    }


    /**
     * 初始化用户数据
     */
    private void initUserInfo(){
            if(mGetUserInfoAsyncTask!=null){
                return;
            }
            mGetUserInfoAsyncTask = new GetUserInfoAsyncTask(UserInfoActivity.this, new GetUserInfoAsyncTask.IGetUserInfoListener() {
                @Override
                public void getUserInfo(UserInfo userInfo) {
                    try {
                        //设置用户信息
                        mUserName.setContent(userInfo.getUserName());
                        mUserSex.setContent(userInfo.getSex());
                        mUserBirthday.setContent(userInfo.getBirthday());
                        mUserPoint.setContent(userInfo.getPoint()+"");
                        mUserLevel.setContent(userInfo.getLevel());

                        String icon = userInfo.getIcon();
                        //加载用户头像
                        if(!icon.equals(BASE_URL_FILE_ICON+"null") && !icon.equals(BASE_URL_FILE_ICON) &&!TextUtils.isEmpty(icon) ) {
                            Glide.with(UserInfoActivity.this).load(icon).into(mCircleImageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(mGetUserInfoAsyncTask!=null){
                            L.e("mGetUserInfo...close");
                            mGetUserInfoAsyncTask = null;
                        }
                    }

                }
            });
            mGetUserInfoAsyncTask.execute();

    }


    /**
     * 初始化监听
     */
    private void initListener() {
        mUserName.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mUserBirthday.setOnClickListener(this);
        mUserPoint.setOnClickListener(this);
        mUserLevel.setOnClickListener(this);

        mChangeIcon.setOnClickListener(this);

        //返回键
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mUserName = (UserInfoView) findViewById(R.id.id_user_name);
        mUserSex = (UserInfoView) findViewById(R.id.id_user_sex);
        mUserBirthday = (UserInfoView) findViewById(R.id.id_user_birthday);
        mUserPoint = (UserInfoView) findViewById(R.id.id_user_point);
        mUserLevel = (UserInfoView) findViewById(R.id.id_user_level);
        mCircleImageView = (CircleImageView) findViewById(R.id.civ_icon);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.customToolbar);
        mChangeIcon = (TextView) findViewById(R.id.id_tv_change_icon);
        mUploadProgress = (TextView) findViewById(R.id.id_tv_upload);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_user_name:
                modifyUserName();
                break;
            case R.id.id_user_sex:
                modifySex();
                break;
            case R.id.id_user_birthday:
                modifyBirthday();
                break;
            case R.id.id_user_point:
                // TODO: 2017/10/1 跳转到任务界面
                break;
            case R.id.id_tv_change_icon:
                changeIcon();
                break;
        }
    }
    /**
     * 更换用户头像
     */
    private void changeIcon(){
        //更换头像
        View changeIconView = getLayoutInflater().inflate(R.layout.change_icon, null);
        pw = new PopupWindow(changeIconView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //触摸空白处会窗体消失
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);

        TextView tv_from_album = (TextView) changeIconView.findViewById(R.id.tv_from_album);
        TextView tv_take_photo = (TextView) changeIconView.findViewById(R.id.tv_take_photo);
        TextView tv_icon_cancel = (TextView) changeIconView.findViewById(R.id.tv_cancel);

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
        }
    }

    /**
     * 请求权限
     * @param permission
     */
    private void  requestPermission(String [][] permission,int requestCode){

        AndPermission.with(UserInfoActivity.this)
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
                        new AlertDialog.Builder(UserInfoActivity.this)
                                .setTitle("友好提醒")
                                .setMessage("为了更换您满意的头像，请把读写权限赐给我吧！")
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
                    ToastUtils.toast(UserInfoActivity.this, "请插入内存卡");
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
                if(AndPermission.hasAlwaysDeniedPermission(UserInfoActivity.this, deniedPermissions)){
                    AndPermission.defaultSettingDialog(UserInfoActivity.this, REQUEST_CODE_CAMERA)
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
        Luban.with(UserInfoActivity.this)
                .load(currentFile)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        try {
                            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            //mIcon.setImageBitmap(bitmap);
                            Glide.with(UserInfoActivity.this)
                                    .load(file)
                                    .fitCenter()
                                    .into(mCircleImageView);

                            //上传图片到服务器
                            if(mModifyUserIconAsyncTask !=null){
                                return;
                            }
                            mModifyUserIconAsyncTask = new ModifyUserIconAsyncTask();
                            mModifyUserIconAsyncTask.execute(currentFile);
                        } catch (Exception e) {
                            ToastUtils.toast(UserInfoActivity.this, "图片太大，请更换图片");
                            e.printStackTrace();
                        }finally {
                            //上传图片到服务器
                            if(mModifyUserIconAsyncTask !=null){
                                mModifyUserIconAsyncTask = null;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

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

    /**
     * 修改生日
     */
    private void modifyBirthday(){
        //弹出一个日期选择器
        final int fromYear;
        final int fromMonth;
        final int fromDay;
        Calendar calendar = Calendar.getInstance();
        fromYear = calendar.get(Calendar.YEAR);
        fromMonth = calendar.get(Calendar.MONTH);
        fromDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String nMonth = null;
                month += 1;
                //一位数，补全成为两位数
                if (String.valueOf(month).length() == 1) {
                    nMonth = "0" + String.valueOf(month);
                }else{
                    nMonth = String.valueOf(month);
                }
                L.e("fromYear:" + fromYear + ",fromMonth:" + fromMonth + ",fromDay:" + fromDay);
                L.e("year:" + year + ",month:" + nMonth + ",day:" + dayOfMonth);

                if ((year > fromYear)|| (year ==fromYear && month >fromMonth+1) ||(year ==fromYear && month == fromMonth+1 && dayOfMonth > fromDay) ) {
                    ToastUtils.toast(UserInfoActivity.this, getString(R.string.text_birthday));
                    return;
                }

                String nDayOfMonth = null;
                //一位数，补全成为两位数
                if(dayOfMonth<10){
                    nDayOfMonth = "0"+String.valueOf(dayOfMonth);
                }else{
                    nDayOfMonth = String.valueOf(dayOfMonth);
                }
                String birth = year + "-" + nMonth + "-" + nDayOfMonth;
                mUserBirthday.setContent(birth);

                //构建JsonObject
                Map<String,String> map = new HashMap<>();
                map.put("birthday",birth);
                JSONObject jsonObject = new JSONObject(map);
                modify(jsonObject);
            }
        }, fromYear, fromMonth, fromDay);


        datePickerDialog.show();
    }


    /**
     * 选择性别popupWindow
     */
    private void modifySex(){
        View pwView = View.inflate(this, R.layout.sex_layout, null);
        pw = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //触摸空白处会窗体消失
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);

        TextView tv_man = (TextView) pwView.findViewById(R.id.tv_man);
        TextView tv_female = (TextView) pwView.findViewById(R.id.tv_female);
        TextView tv_cancel = (TextView) pwView.findViewById(R.id.tv_cancel);

        tv_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserSex.setContent(getString(R.string.text_man));

                //构建JsonObject
                Map<String,String> map = new HashMap<>();
                map.put("sex","1");
                JSONObject jsonObject = new JSONObject(map);

                modify(jsonObject);

                pw.dismiss();
            }
        });
        tv_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserSex.setContent(getString(R.string.text_famale));
                //构建JsonObject
                Map<String,String> map = new HashMap<>();
                map.put("sex","0");
                JSONObject jsonObject = new JSONObject(map);

                modify(jsonObject);

                pw.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        if (pw != null && !pw.isShowing()) {
            pw.showAtLocation(findViewById(R.id.layout), Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 修改用户名对话框
     */
    private void modifyUserName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        View view = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_modify_name,null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText mEtUserName = (EditText) view.findViewById(R.id.user_name);
        mEtUserName.setText(mUserName.getContent());
        Button mCancel = (Button) view.findViewById(R.id.btn_cancel);

        Button mSure = (Button) view.findViewById(R.id.btn_sure);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = mEtUserName.getText().toString().trim();

                if(TextUtils.isEmpty(userName)){
                    ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_input_user_name));
                    return;
                }

                if(userName.length()<3 || userName.length()>30){
                    ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_user_name_length));
                    return;
                }

                //构建JsonObject
                Map<String,String> map = new HashMap<>();
                map.put("name",userName);
                JSONObject jsonObject = new JSONObject(map);

                modify(jsonObject);

                dialog.dismiss();
                mUserName.setContent(userName);

            }
        });
    }

    /**
     * 修改信息
     * @param jsonObject
     */
    private void modify(JSONObject jsonObject){
        if(mModifyUserInfoAsyncTask!=null){
            return;
        }
        mModifyUserInfoAsyncTask = new ModifyUserInfoAsyncTask();
        mModifyUserInfoAsyncTask.execute(jsonObject);
    }

    /**
     * 修改用户信息(单个提交)
     */
    private class ModifyUserInfoAsyncTask extends AsyncTask<JSONObject,Void,Integer>{

        @Override
        protected Integer doInBackground(JSONObject... params) {
            String url = BASE_URL+"/app/user";

            try {
                Response response = OkGo.<String>put(url).headers(AUTHORIZATION, GetAuthorizationUtil.getAuth(UserInfoActivity.this))
                        .upJson(params[0])
                        .execute();
                int code = JsonUtils.getCode(response.body().string());
                return code;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return FAILED;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mModifyUserInfoAsyncTask = null;
            if(integer == SUCCESS){
                ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_success_msg));
                SpUtils.getEditor(UserInfoActivity.this).putBoolean(SHOW_DATA,false).commit();//需要重新获取数据
            }else{
                ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_failed_msg));
            }
        }
    }


    /**
     * 上传用户头像
     */
    private class ModifyUserIconAsyncTask extends AsyncTask<File,Void,Void>{

        @Override
        protected Void doInBackground(File... params) {
            String url = BASE_URL+"/app/user/icon";

            OkGo.<String>put(url)
                    .headers(AUTHORIZATION,GetAuthorizationUtil.getAuth(UserInfoActivity.this))
                    .params("icon",params[0])
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                            mUploadProgress.setVisibility(View.GONE);
                            String resJosn= response.body().toString().toString();
                            int code = JsonUtils.getCode(resJosn);
                            if(code == SUCCESS){
                                ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_change_icon_success));
                                SpUtils.getEditor(UserInfoActivity.this).putBoolean(SHOW_DATA,false).commit();
                            }else {
                                ToastUtils.toast(UserInfoActivity.this,getString(R.string.text_change_icon_failed));
                            }

                        }
                        @Override
                        public void uploadProgress(Progress progress) {
                            super.uploadProgress(progress);
                            long temp = 0;
                            long len = progress.currentSize;
                            long sum = progress.totalSize;
                            temp += len;
                            int pre = (int) (temp/sum)*100;
                            mUploadProgress.setVisibility(View.VISIBLE);
                            mUploadProgress.setText("已上传 "+pre +"%");
                        }
                    });
            return null;
        }
    }
}
