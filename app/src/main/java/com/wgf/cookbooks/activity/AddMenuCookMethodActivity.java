package com.wgf.cookbooks.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.InsertMaterials;
import com.wgf.cookbooks.bean.InsertMenu;
import com.wgf.cookbooks.bean.InsertStep;
import com.wgf.cookbooks.clazz.UpMenuContentAsyncTask;
import com.wgf.cookbooks.clazz.UpMenuCoverAsyncTask;
import com.wgf.cookbooks.clazz.UpMenuStepPictureAsyncTask;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MultipartBody;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.wgf.cookbooks.util.Constants.REQUEST_CODE_ALBUM;
import static com.wgf.cookbooks.util.Constants.SUCCESS;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class AddMenuCookMethodActivity extends AppCompatActivity implements UpMenuStepPictureAsyncTask.IUpMenuStepPictureListener ,UpMenuContentAsyncTask.IUpMenuContentListener{
    private CustomToolbar mCustomToolbar;
    private TextView mReleaseMenu;
    private TextView mStepNumber;

    private EditText mStepDesc;
    private TextView mStepDescNumber;
    private LinearLayout mStepLayout;
    private LayoutInflater mInflater;
    private LinearLayout mAdddStep;
    private int position = 1;
    private SqliteDao dao;
    private UpMenuStepPictureAsyncTask mUpMenuStepPictureAsyncTask;

    private UpMenuContentAsyncTask  mUpMenuContentAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_cook_method);
        initView();
        mInflater = LayoutInflater.from(this);
        addStepItem();//显示第一个

        setListener();
        dao = new SqliteDao(this);
    }





    /**
     * 设置监听
     */
    private void setListener() {
        mAdddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStepItem();
            }
        });
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {

            @Override
            public void onClick() {
                finish();
            }
        });


        //发布菜谱
        mReleaseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_back);
        mReleaseMenu = (TextView) findViewById(R.id.id_tv_release);
        mStepNumber = (TextView) findViewById(R.id.id_tv_step_number);
        mStepDesc = (EditText) findViewById(R.id.id_et_step_desc);
        mStepDescNumber = (TextView) findViewById(R.id.id_tv_step_desc);
        mStepLayout = (LinearLayout) findViewById(R.id.id_ll_step_layout);
        mAdddStep = (LinearLayout) findViewById(R.id.id_ll_add_step);
    }

    //菜谱步骤图片
    private ImageView imageView;

    /**
     * 添加step的步骤view
     */
    private void addStepItem(){
        final View view  = mInflater.inflate(R.layout.add_menu_step_item,null,false);
        view.setTag(position);
        TextView StepNumber = (TextView) view.findViewById(R.id.id_tv_step_cover);
        StepNumber.setText(""+position);
        imageView = (ImageView) view.findViewById(R.id.id_iv_step_cover);
        initDelete();

        view.findViewById(R.id.id_iv_step_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStepLayout.getChildCount()>1){
                    mStepLayout.removeView(view);

                    //删除图片路径
//                    String path = (String) imageView.getTag(R.id.imageTag);
//                    if(!TextUtils.isEmpty(path)){
//                        paths.remove(path);
//                    }
                    //重新设置步骤几
                    initStep();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册获取图片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
            }
        });
        mStepLayout.addView(view);
        view.requestFocus();//步骤框获得焦点
        position++;
        initStep();
    }


    private String coverPath;

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
            }

        }
    }

    //鲁班压缩图片
    private void compressIcon(final String iconPath) {
        final File currentFile = new File(iconPath);
        Luban.with(AddMenuCookMethodActivity.this)
                .load(currentFile)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        imageView.setTag(R.id.imageTag,coverPath);
                    }

                    @Override
                    public void onSuccess(File file) {
                        Glide.with(AddMenuCookMethodActivity.this)
                                .load(file)
                                .fitCenter()
                                .into(imageView);
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();//启动压缩

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

    private boolean noDesc = false;//表示有无描述的标志

    /**
     * 获取步骤的内容（图片链接和步骤描述)
     * @return
     */
    private List<InsertStep> getItemContent(){
        noDesc = false;
        int childCount = mStepLayout.getChildCount();
        List<InsertStep> steps = new ArrayList<>();

        for(int i= 0 ;i<childCount;i++){
            InsertStep step = new InsertStep();
            View view = mStepLayout.getChildAt(i);
            ImageView cover = (ImageView) view.findViewById(R.id.id_iv_step_cover);
            EditText etDesc = (EditText) view.findViewById(R.id.id_et_step_desc);
            String url = (String) cover.getTag(R.id.imageTag);
            String desc = etDesc.getText().toString().trim();
            if(TextUtils.isEmpty(desc)){
                noDesc = true;
                break;
            }
            step.setId(i+1);
            step.setStepDesc(desc);
            step.setStepUrl(url);
            steps.add(step);
        }
        return steps;
    }





    /**
     * 初始化步骤
     */
    private void initStep(){
        int childCount = mStepLayout.getChildCount();
        for(int i= 0 ;i<childCount;i++){
            View view = mStepLayout.getChildAt(i);
            TextView StepNumber = (TextView) view.findViewById(R.id.id_tv_step_cover);
            StepNumber.setText(""+(i+1));
        }
        mStepNumber.setText("全部步骤("+childCount+"）");
        initDelete();
    }

    /**
     * 初始化删除的位置
     */
    private void initDelete(){
        int childCount = mStepLayout.getChildCount();
        for (int i= 0;i<childCount;i++){
            View view = mStepLayout.getChildAt(i);
            ImageView stepDelete = (ImageView) view.findViewById(R.id.id_iv_step_delete);
            if(childCount == 1){
                stepDelete.setVisibility(View.GONE);
            }else{
                stepDelete.setVisibility(View.VISIBLE);
            }
        }
    }



    private  List<InsertStep> steps;
    private List<String> descs;

    /**
     * 发布
     */
    private void release(){
        steps = getItemContent();
        if(noDesc){
            ToastUtils.toast(AddMenuCookMethodActivity.this,"请输入描述内容");
            return;
        }
        dao.deleteMenuStep();//清空表
        int result = dao.insertMenuStep(steps);
        if(result!=-1){
            uploadDialog();
            //上传到服务器
            //先上传图片信息，返回图片的地址
            List<String> urls = new ArrayList<String>();
            descs = new ArrayList<>();
            for (InsertStep step:steps){
                urls.add(step.getStepUrl());
                descs.add(step.getStepDesc());
            }

            if(mUpMenuStepPictureAsyncTask!=null){
                return;
            }
            mUpMenuStepPictureAsyncTask = new UpMenuStepPictureAsyncTask(AddMenuCookMethodActivity.this);
            mUpMenuStepPictureAsyncTask.setmListener(AddMenuCookMethodActivity.this);
            mUpMenuStepPictureAsyncTask.execute(urls);

        }
    }


    /**
     * 上传图片的回调，返回图片的地址
     * @param urls
     */
    @Override
    public void result(List<String> urls) {

        if(mUpMenuStepPictureAsyncTask!=null){
            //L.e("mUpMenuStepPictureAsyncTask null");
            mUpMenuStepPictureAsyncTask = null;
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if(urls !=null && urls.size()>0){
            List<Map<String,Object>> stepUrlDesc = new ArrayList<>();//上传的菜谱图片名称，和对应的步骤的描述
            for (int i = 0;i<urls.size();i++){
                Map<String,Object> map = new HashMap<>();
                map.put("url",urls.get(i));
                map.put("desc",descs.get(i));
                stepUrlDesc.add(map);
            }
//            L.e("urls:"+urls.size());
            //1、菜谱信息
            //2、食材信息
            //3、步骤信息
            InsertMenu insertMenu = dao.queryMenuInfo();
            //转化为map
            Map<String,Object> menuMap = new HashMap<>();
            menuMap.put("mainIcon",insertMenu.getMainIcon());
            menuMap.put("menuName",insertMenu.getMenuName());
            menuMap.put("menuDesc",insertMenu.getMenuDesc());
            menuMap.put("menuType",insertMenu.getMenuType());
            menuMap.put("menuTypeSun",insertMenu.getMenuTypeSun());


            L.e("insertMenu:"+insertMenu.getMenuName()+insertMenu.getMainIcon()+insertMenu.getMenuDesc()+insertMenu.getMenuType());
            List<InsertMaterials> materialses = dao.queryMenuMaterials();
            List<Map<String,Object>> materialsesMap = new ArrayList<>();
            for (InsertMaterials materials:materialses){
                Map<String,Object> map = new HashMap<>();
                map.put("materialsName",materials.getMaterialsName());
                map.put("materialsDose",materials.getMaterialsDose());
                materialsesMap.add(map);
            }


            Map<String,Object> map = new HashMap<String, Object>();
            map.put("menuInfo",menuMap);
            map.put("materialses",materialsesMap);
            map.put("steps",stepUrlDesc);
            JSONObject jsonObject = new JSONObject(map);

            if(mUpMenuContentAsyncTask != null){
                return;
            }
            mUpMenuContentAsyncTask = new UpMenuContentAsyncTask(this);
            mUpMenuContentAsyncTask.setmListener(this);
            mUpMenuContentAsyncTask.execute(jsonObject.toString());
        }

    }

    @Override
    public void success(int code) {
        if(mUpMenuContentAsyncTask!=null){
            //L.e("mUpMenuContentAsyncTask null");
            mUpMenuContentAsyncTask = null;
        }
        if(code == SUCCESS){
            ToastUtils.toast(this,"上传成功,快去查看我的菜谱吧");
            Intent intent = new Intent(AddMenuCookMethodActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else{
            ToastUtils.toast(this,"上传失败");
        }
    }
}
