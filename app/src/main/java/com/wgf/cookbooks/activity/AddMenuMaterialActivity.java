package com.wgf.cookbooks.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.bean.InsertMaterials;
import com.wgf.cookbooks.db.SqliteDao;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.SoftInputUtils;
import com.wgf.cookbooks.util.ToastUtils;
import com.wgf.cookbooks.view.CustomToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 */
public class AddMenuMaterialActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mMaterialsLayout, mAddMaterials;
    private CustomToolbar mCustomToolbar;
    private TextView mNextStep;
    private LayoutInflater mInflater;
    private boolean isEmpty = false;//不为空
    private SqliteDao dao;
    private int position = 1;//View的下标

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_material_add);
        mInflater = LayoutInflater.from(this);
        initView();

        setListener();

        addMaterialItem();//开始显示一条

        dao = new SqliteDao(this);
    }

    /**
     * 绑定控件
     */
    private void setListener() {
        mCustomToolbar.setBtnOnBackOnClickListener(new CustomToolbar.BtnOnBackOnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mNextStep.setOnClickListener(this);
        mAddMaterials.setOnClickListener(this);
    }

    /**
     * 绑定控价
     */
    private void initView() {
        mMaterialsLayout = (LinearLayout) findViewById(R.id.id_ll_materials);
        mCustomToolbar = (CustomToolbar) findViewById(R.id.id_ct_back);
        mNextStep = (TextView) findViewById(R.id.id_tv_next_step);
        mAddMaterials = (LinearLayout) findViewById(R.id.id_ll_add_materials);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_next_step:
                nextStep();
                break;
            case R.id.id_ll_add_materials:
                addMaterialItem();
                break;
        }
    }

    /**
     * 下一步
     */
    private void nextStep() {
        List<InsertMaterials> materialsContent = getMaterialsContent();//测试，这里需要修改
        if (isEmpty) {
            ToastUtils.toast(AddMenuMaterialActivity.this, "请完善食材内容");
            isEmpty = false;
        } else {
            //清空表
            dao.deleteMenuMaterials();
            //插入数据库
            int result = dao.insertMenuMaterial(materialsContent);
            if (result != -1) {
                //跳转到上传步骤界面
                IntentUtils.jump(this, AddMenuCookMethodActivity.class);
                //如果输入法显示则隐藏
                SoftInputUtils.hideSoftInput(this);
            }
        }
    }

    //动态的添加view
    private void addMaterialItem() {
        final View view = mInflater.inflate(R.layout.material_layout_item, null, false);

        view.setTag(position);
        final EditText materialName = (EditText) view.findViewById(R.id.id_et_material_name);
        final EditText materialDose = (EditText) view.findViewById(R.id.id_et_material_dose);
        //添加删除点击事件
        view.findViewById(R.id.id_iv_text_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //内容置为空
                materialName.setText("");
                materialDose.setText("");
            }
        });
        view.findViewById(R.id.id_iv_material_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除view
//                int tag = (int) view.getTag();
//                if (tag != 1){
//                    mMaterialsLayout.removeView(view);
//                }
                if (mMaterialsLayout.getChildCount() > 1) {
                    mMaterialsLayout.removeView(view);
                }
                initDelete();
            }
        });
        mMaterialsLayout.addView(view);
        position++;
        initDelete();
    }

    /**
     * 获取材料的内容
     */
    private List<InsertMaterials> getMaterialsContent() {
        int childCount = mMaterialsLayout.getChildCount();
        List<InsertMaterials> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View view = mMaterialsLayout.getChildAt(i);
            EditText materialName = (EditText) view.findViewById(R.id.id_et_material_name);
            EditText materialDose = (EditText) view.findViewById(R.id.id_et_material_dose);
            String name = materialName.getText().toString().trim();
            String dose = materialDose.getText().toString().trim();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dose)) {
                isEmpty = true;//有内容为空
            }
            if (isEmpty) {
                //有空直接跳出循环
                break;
            }
            InsertMaterials materials = new InsertMaterials();
            materials.setId(i + 1);
            materials.setMaterialsName(name);
            materials.setMaterialsDose(dose);
            list.add(materials);
            //L.e("name:"+name+",dose:"+dose);
        }
        return list;
    }


    /**
     * 初始化删除的位置
     */
    private void initDelete() {
        int childCount = mMaterialsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMaterialsLayout.getChildAt(i);
            ImageView stepDelete = (ImageView) view.findViewById(R.id.id_iv_material_delete);
            if (childCount == 1) {
                stepDelete.setVisibility(View.GONE);
            } else {
                stepDelete.setVisibility(View.VISIBLE);
            }
        }
    }

}
