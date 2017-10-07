package com.wgf.cookbooks.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.wgf.cookbooks.R;


/**
 * author guofei_wu
 * email guofei_wu@163.com
 * 自定义SwipeRefreshLayout
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private int mTouchSlop;//滑动的最小距离
    private boolean isLoading;
    private ListView mListView;
    private View mFooterView;
    private OnLoadDataListener mListener;


    public CustomSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFooterView = View.inflate(context, R.layout.footview, null);
        //获取滑动的最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mListView == null) {
            if (getChildCount() > 0) {
                if (getChildAt(0) instanceof ListView) {
                    mListView = (ListView) getChildAt(0);
                    setListViewOnScroll();
                }
            }
        }
    }

    //设置listView的滑动事件
    private void setListViewOnScroll() {
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //移动过程中判断能否进行下拉刷新
                    if (canLoadMore()) {
                        loadData();//加载数据
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
    }

    private float mDownY, mUpY;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(canLoadMore()){
                    loadData();
                }
                break;
            case MotionEvent.ACTION_UP:
                mUpY = ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoadMore() {
        boolean conditions1 = (mDownY - mUpY) >= mTouchSlop;//大于或等于最小滑动的距离
        boolean conditions2 = false;
        //最后可见的位置是最后一个数据
        if (mListView != null && mListView.getAdapter() != null) {
            conditions2 = mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1;
        }
        boolean conditions3 = false;
        conditions3 = !isLoading;//不是在正在加载状态
        return conditions1 && conditions2 && conditions3;
    }


    private void loadData(){
        if(mListener !=null){
            //设置布局为可加载数据
            setLoading(true);
            mListener.onLoad();//实现加载数据的回调
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        //如果正在加载数据，显示mFooterView
        if(isLoading){
            mListView.addFooterView(mFooterView);
        }else{
            mListView.removeFooterView(mFooterView);
            //移除mFooterView,并且重置Y的值
            mDownY = 0;
            mUpY = 0;
        }
    }

    public interface OnLoadDataListener{
        void onLoad();
    }

    public void setOnLoadDataListener(OnLoadDataListener mListener) {
        this.mListener = mListener;
    }
}

