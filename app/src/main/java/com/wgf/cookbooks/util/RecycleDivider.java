package com.wgf.cookbooks.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;


/**
 * author WuGuofei on 2017/4/27.
 * e-mail：guofei_wu@163.com
 */
public class RecycleDivider extends RecyclerView.ItemDecoration {
    private int ATTRS[] = {android.R.attr.listDivider};
    private Drawable mDivider;
    private int mOrientation;

    public static final int HORIZONTAL_LIST = LinearLayout.HORIZONTAL;
    public static final int VERITCAL_LIST = LinearLayout.VERTICAL;

    public RecycleDivider(Context context, int orientation) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        setOrientation(orientation);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERITCAL_LIST) {
            throw new IllegalArgumentException("invalid orientation" + orientation);
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_LIST) {
            drawHorizontal(c, parent);
        } else if (mOrientation == VERITCAL_LIST) {
            drawVeritcal(c, parent);
        }
    }

    //竖直分割线的绘制
    private void drawVeritcal(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();//子view的个数
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int top = childView.getBottom() + lp.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //水平分割线的绘制
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int left = childView.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mOrientation == VERITCAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else if (mOrientation == HORIZONTAL_LIST) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
