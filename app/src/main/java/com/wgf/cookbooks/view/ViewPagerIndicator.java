package com.wgf.cookbooks.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wgf.cookbooks.R;

import java.util.List;

/**
 * ViewPager指示器
 * author WuGuofei on 2017/4/24.
 * e-mail：guofei_wu@163.com
 */

public class ViewPagerIndicator extends LinearLayout {
    private static float RADIO_TRIANGLE = 1 / 6F;
    private static int DEFAULT_COUNT = 4;
    private static int COLOR_TEXT_NORMAL = 0xFFFFFFFF;
    private static int COLOR_TEXT_HIGHTLIGHT = 0xFF1C1B1B;
    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;//三角形宽度
    private int mTriangleHeight;//三角形高度
    private int mInitTranslationX;//初始化位置
    private int mTranslationX;//移动的距离
    private int mTabVisibleCount;//可见的tab的数量
    private int screenWidth;
    private final int DEFAULT_TRIANGLE_WIDTH_MAX = (int) ((getScreenWidth() / 3) * RADIO_TRIANGLE);
    private List<String> mTitles;
    private int mLineWidth;//直线的宽度
    private int mLineWidthX;//偏移的距离
    private int mPaintStrokeWidth = 16;
    private int mShape;//默认是直线
    private OnPageChangeListener mListener;
    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);

        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_tabVisibleCount, DEFAULT_COUNT);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = DEFAULT_COUNT;
        }
        a.recycle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (mShape == 0) {
            canvas.translate(mInitTranslationX + mTranslationX, getHeight());
            canvas.drawPath(mPath, mPaint);
        } else if (mShape == 1) {
            mPaint.setColor(Color.parseColor("#FAFAED04"));
            mPaint.setStrokeWidth(mPaintStrokeWidth);
//            L.e("draw line:"+getHeight()+"--:"+mPaintStrokeWidth);
            canvas.drawLine(10 + mLineWidthX, getHeight() + 2, mLineWidth - 10 + mLineWidthX, getHeight() + 2, mPaint);
        }
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mShape == 0) {
            mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE);
            mTriangleWidth = Math.min(mTriangleWidth, DEFAULT_TRIANGLE_WIDTH_MAX);
            mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
            initTriangle();
        } else if (mShape == 1) {
            mLineWidth = w / mTabVisibleCount;
        }
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {
        mTriangleHeight = mTriangleWidth / 2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * 设置指示器的形状
     *
     * @param shape 0表示三角形，1表示直线
     */
    public void setIndicatorShape(int shape) {
        if (shape == 0 || shape == 1) {
            mShape = shape;
        } else {
            mShape = 1;
        }
    }

    //viewpager滑动时，三角(直线)指示器的变化
    public void scroll(int position, float positionOffset) {
        //偏移的位置 tabWdith * positionOffset + tabWidth* position;
//        L.e("position;" + position + "---positionOffset:" + positionOffset);
        int tabWidth = getWidth() / mTabVisibleCount;
        mTranslationX = (int) (tabWidth * position + tabWidth * positionOffset);
        mLineWidthX = (int) (tabWidth * position + tabWidth * positionOffset);
        //移动父容器，tab导航的变化
        if (position >= (mTabVisibleCount - 2) && positionOffset > 0 && getChildCount() > mTabVisibleCount && position != getChildCount() - 2) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (positionOffset * tabWidth), 0);
            } else {
                this.scrollTo(position * tabWidth + (int) (positionOffset * tabWidth), 0);
            }
        }
        invalidate();
    }

    /**
     * 布局绘制完成时调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();//拿到子View的个数
        if (cCount == 0) return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;//获得每个子View的宽度
            view.setLayoutParams(lp);
        }
        setOnItemClickListener();
    }

    /**
     * 设置tab文本的点击事件
     */
    private void setOnItemClickListener() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        return screenWidth;
    }

    /**
     * 设置可见tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
    }

    public void setTabTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            mTitles = titles;
            this.removeAllViews();
            for (int i = 0; i < mTitles.size(); i++) {
                this.addView(generateTextView(mTitles.get(i)));
            }
            setOnItemClickListener();
        }
    }

    /**
     * 生成TextView
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    /**
     * viewpager设置OnPageChangeListener
     *
     * @param vp
     * @param position
     */
    public void setOnPageChangeListener(ViewPager vp, int position) {
        if (vp != null) {
            mViewPager = vp;
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    scroll(position, positionOffset);
                    if (mListener != null) {
                        mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                    //设置文字的颜色为高亮
                    setColorTextHightlight(position);
                }

                @Override
                public void onPageSelected(int position) {
                    if (mListener != null) {
                        mListener.onPageSelected(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (mListener != null) {
                        mListener.onPageScrollStateChanged(state);
                    }
                }
            });
            mViewPager.setCurrentItem(position);
            setColorTextHightlight(position);
        }
    }

    /**
     * 重置所有tab文本的颜色
     */
    private void resetAllTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 设置颜色为高亮
     *
     * @param position
     */
    private void setColorTextHightlight(int position) {
        resetAllTextViewColor();//重置所有的字体的颜色
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHTLIGHT);
        }
    }

    public interface OnPageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }
}
