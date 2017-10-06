package com.wgf.cookbooks.util;

import android.app.Activity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;

/**
 * author guofei_wu
 * email guofei_wu@163.com
 * activity 之间切换的动画
 */
public class SwitchAnimationUtils {

    /**
     * 进入activity的方式 fade 平滑的
     * @param activity
     */
    public static void enterActivityFade(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition fade = TransitionInflater.from(activity).inflateTransition(android.R.transition.fade);
        activity.getWindow().setEnterTransition(fade);
    }

    /**
     * 进入activity的方式 explode 爆炸，爆发；激增
     * @param activity
     */
    public static void enterActivityExplode(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(activity).inflateTransition(android.R.transition.explode);
        activity.getWindow().setEnterTransition(explode);
    }

    /**
     * 进入activity的方式 move 移动
     * @param activity
     */
    public static void enterActivityMove(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition move = TransitionInflater.from(activity).inflateTransition(android.R.transition.move);
        activity.getWindow().setEnterTransition(move);
    }


    /**
     * 进入activity的方式 slide 滑动的
     * @param activity
     */
    public static void enterActivitySlideRight(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide = TransitionInflater.from(activity).inflateTransition(android.R.transition.slide_right);
        activity.getWindow().setEnterTransition(slide);
    }
    public static void enterActivitySlideLeft(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide = TransitionInflater.from(activity).inflateTransition(android.R.transition.slide_left);
        activity.getWindow().setEnterTransition(slide);
    }



    /**
     * 退出activity的方式 fade 平滑的
     * @param activity
     */
    public static void exitActivityFade(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition fade = TransitionInflater.from(activity).inflateTransition(android.R.transition.fade);
        activity.getWindow().setExitTransition(fade);
    }

    /**
     * 退出activity的方式 explode 爆炸，爆发；激增
     * @param activity
     */
    public static void exitActivityExplode(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(activity).inflateTransition(android.R.transition.explode);
        activity.getWindow().setExitTransition(explode);
    }



    /**
     * 退出activity的方式 move 移动
     * @param activity
     */
    public static void exitActivityMove(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition move = TransitionInflater.from(activity).inflateTransition(android.R.transition.move);
        activity.getWindow().setExitTransition(move);
    }



    public static void exitActivitySlideLeft(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide = TransitionInflater.from(activity).inflateTransition(android.R.transition.slide_left);
        activity.getWindow().setExitTransition(slide);
    }


    public static void exitActivitySlideRight(Activity activity){
        activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide = TransitionInflater.from(activity).inflateTransition(android.R.transition.slide_right);
        activity.getWindow().setExitTransition(slide);
    }

}
