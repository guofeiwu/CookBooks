package com.wgf.cookbooks.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wgf.cookbooks.R;
import com.wgf.cookbooks.adapter.ShaiRecycleViewAdapter;
import com.wgf.cookbooks.clazz.GetShaiAsyncTask;
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
public class DiscoverFragment extends Fragment {

    private RecyclerView mRecyclerViewShai;
    private ShaiRecycleViewAdapter mShaiRecycleViewAdapter;
    private List<String> urls;
    private RecycleDivider mRecycleDivider;
    private GetShaiAsyncTask mGetShaiAsyncTask;
    private RelativeLayout mShaiYiShai;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        initData();

        initView(view);


        setListener();
        return view;
    }

    private void initView(View view) {
        mRecyclerViewShai = (RecyclerView) view.findViewById(R.id.id_rv_shaiyishai);
        mShaiYiShai = (RelativeLayout) view.findViewById(R.id.rl_shai_yi_shai);
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mShaiYiShai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 获取初始化数据
     */
    void initData() {
        urls = new ArrayList<>();
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663400&di=3424fd25a07287cb53e711e93646422c&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fbaike%2Fpic%2Fitem%2Fca1349540923dd547964fba5db09b3de9d8248e3.jpg");
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663498&di=43f9457a0907a905fd3b61efb1bec62e&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fc9fcc3cec3fdfc03b0a84995df3f8794a5c226ce.jpg");
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663497&di=7d9bfebd8ea73ce881f38447050ad380&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F37d12f2eb9389b50fb6112868c35e5dde6116e4c.jpg");
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663496&di=601b9c094b4529dd22a638b1063380a2&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F14ce36d3d539b60097b9999ae250352ac65cb7ac.jpg");
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663497&di=7d9bfebd8ea73ce881f38447050ad380&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F37d12f2eb9389b50fb6112868c35e5dde6116e4c.jpg");
//        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1506961663496&di=601b9c094b4529dd22a638b1063380a2&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F14ce36d3d539b60097b9999ae250352ac65cb7ac.jpg");
        if (mGetShaiAsyncTask != null) {
            return;
        }
        mGetShaiAsyncTask = new GetShaiAsyncTask(new GetShaiAsyncTask.IGetShaiListener() {
            @Override
            public void getShaiList(JSONArray shais) {
                if (shais != null) {
                    try {
                        for (int i = 0; i < shais.length(); i++) {
                            urls.add(BASE_URL_FILE_SHAI + shais.getJSONObject(i).getString("address"));
                        }
                        mRecycleDivider = new RecycleDivider(getContext(), RecycleDivider.HORIZONTAL_LIST);
                        mShaiRecycleViewAdapter = new ShaiRecycleViewAdapter(getActivity(), urls);
                        mRecyclerViewShai.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
                        mRecyclerViewShai.addItemDecoration(mRecycleDivider);
                        mRecyclerViewShai.setAdapter(mShaiRecycleViewAdapter);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        mGetShaiAsyncTask.execute(1);//开始加载第一页数据
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
