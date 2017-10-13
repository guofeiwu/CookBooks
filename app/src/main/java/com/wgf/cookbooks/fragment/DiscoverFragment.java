package com.wgf.cookbooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.activity.SearchActivity;
import com.wgf.cookbooks.activity.ShaiActivity;
import com.wgf.cookbooks.activity.ShaiDetailActivity;
import com.wgf.cookbooks.adapter.ShaiDetailRecycleViewAdapter;
import com.wgf.cookbooks.adapter.ShaiRecycleViewAdapter;
import com.wgf.cookbooks.bean.Shai;
import com.wgf.cookbooks.clazz.GetShaiAsyncTask;
import com.wgf.cookbooks.util.IntentUtils;
import com.wgf.cookbooks.util.L;
import com.wgf.cookbooks.util.RecycleDivider;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.wgf.cookbooks.util.Constants.BASE_URL_FILE_SHAI;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 发现主页
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerViewShai;
    private ShaiRecycleViewAdapter mShaiRecycleViewAdapter;
    private List<String> urls;
    private RecycleDivider mRecycleDivider;
    private GetShaiAsyncTask mGetShaiAsyncTask;
    private RelativeLayout mShaiYiShai;

    private List<Shai> shais;
    private EditText mEditTextSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        //initData();

        initView(view);

        setListener();
        return view;
    }

    private void initView(View view) {
        mRecyclerViewShai = (RecyclerView) view.findViewById(R.id.id_rv_shaiyishai);
        mShaiYiShai = (RelativeLayout) view.findViewById(R.id.rl_shai_yi_shai);
        mEditTextSearch = (EditText) view.findViewById(R.id.id_et_search);
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mShaiYiShai.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume");
        initData();
    }

    /**
     * 获取初始化数据
     */
    void initData() {
        shais = new ArrayList<>();
        urls = new ArrayList<>();
        if (mGetShaiAsyncTask != null) {
            return;
        }
        mGetShaiAsyncTask = new GetShaiAsyncTask(new GetShaiAsyncTask.IGetShaiListener() {
            @Override
            public void getShaiList(List<Shai> list) {
                if (list != null) {
                    shais.addAll(list);
                    try {
                        for (int i = 0; i < list.size(); i++) {
                            urls.add(BASE_URL_FILE_SHAI + list.get(i).getAddress());
                        }
                        mRecycleDivider = new RecycleDivider(getContext(), RecycleDivider.HORIZONTAL_LIST);
                        mShaiRecycleViewAdapter = new ShaiRecycleViewAdapter(getActivity(), urls, new ShaiRecycleViewAdapter.IShaiImageClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(getActivity(),ShaiDetailActivity.class);
                                Shai shai = shais.get(position);
                                intent.putExtra("shaiPkId",shai.getShaiPkId());
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });
                        mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
                        mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                        mRecyclerViewShai.setAdapter(mShaiRecycleViewAdapter);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(mGetShaiAsyncTask!=null)
                        {
                            mGetShaiAsyncTask=null;
                        }
                    }
                }
            }
        });
        mGetShaiAsyncTask.execute(1);//开始加载第一页数据
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_shai_yi_shai:
                //跳转，查看更多的晒一晒
                IntentUtils.jump(getActivity(), ShaiActivity.class);
                break;
            case R.id.id_et_search:
                IntentUtils.jump(getActivity(), SearchActivity.class);
                break;
        }
    }

    /**
     * 获取晒一晒
     */
//    private class GetShaiAsyncTask extends AsyncTask<Integer,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Integer... params) {
//            String url = BASE_URL+"/app/shai/all/"+params[0];
//            OkGo.<String>get(url)
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onSuccess(Response<String> response) {
//                            String resJson = response.body().toString();
//                            int code = JsonUtils.getCode(resJson);
//                            if (code == SUCCESS) {
//                                try {
//                                    JSONArray array = JsonUtils.getJsonArray(resJson);
//                                    for(int i = 0;i<array.length();i++){
//                                        urls.add(BASE_URL_FILE_SHAI+array.getJSONObject(i).getString("address"));
//                                    }
//                                    mRecycleDivider = new RecycleDivider(getContext(), RecycleDivider.HORIZONTAL_LIST);
//                                    mShaiRecycleViewAdapter = new ShaiRecycleViewAdapter(getActivity(),urls);
//                                    mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
//                                    mRecyclerViewShai.addItemDecoration(mRecycleDivider);
//                                    mRecyclerViewShai.setAdapter(mShaiRecycleViewAdapter);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//            return null;
//        }
//    }
}
