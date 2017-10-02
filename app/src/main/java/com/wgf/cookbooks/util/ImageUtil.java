package com.wgf.cookbooks.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;

import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static int calculateInSampleSize2(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



    public static Bitmap decodeBitmapFromResource(InputStream is){

        //获得图片的宽、高
        BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
        tmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, tmpOptions);
        int width = tmpOptions.outWidth;
        int height = tmpOptions.outHeight;

        //设置显示图片的中心区域
        BitmapRegionDecoder bitmapRegionDecoder = null;
        try {
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = bitmapRegionDecoder.decodeRegion(new Rect(width / 2 - 10, height / 2  - 10, width / 2 +10, height / 2 +10), options);
            L.e("bitmap");
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }










    public static Bitmap getReverseBitmapById(int resId, Context context){
        Bitmap sourceBitmap= BitmapFactory.decodeResource(context.getResources(),resId);
        //绘制原图的下一半图片
        Matrix matrix=new Matrix();
        //倒影翻转
        matrix.setScale(1,-1);

        Bitmap inverseBitmap=Bitmap.createBitmap(sourceBitmap,0,sourceBitmap.getHeight()/2,sourceBitmap.getWidth(),sourceBitmap.getHeight()/3,matrix,false);
        //合成图片
        Bitmap groupbBitmap=Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight()+sourceBitmap.getHeight()/3+60,sourceBitmap.getConfig());
        //以合成图片为画布
        Canvas gCanvas=new Canvas(groupbBitmap);
        //将原图和倒影图片画在合成图片上
        gCanvas.drawBitmap(sourceBitmap,0,0,null);
        gCanvas.drawBitmap(inverseBitmap,0,sourceBitmap.getHeight()+50,null);
        //添加遮罩
        Paint paint=new Paint();
        Shader.TileMode tileMode= Shader.TileMode.CLAMP;
        LinearGradient shader=new LinearGradient(0,sourceBitmap.getHeight()+50,0,
                groupbBitmap.getHeight(), Color.BLACK,Color.TRANSPARENT,tileMode);
        paint.setShader(shader);
        //这里取矩形渐变区和图片的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0,sourceBitmap.getHeight()+50,sourceBitmap.getWidth(),groupbBitmap.getHeight(),paint);
        return groupbBitmap;
    }








//    mViewPager = (ViewPager) findViewById(R.id.vp_3d_page);
//    imageList = new ArrayList<>();
//    for(int i = 0;i<images.length;i++){
//        ImageView iv = new ImageView(this);
//        iv.setImageResource(images[i]);
//        imageList.add(iv);
//    }
//
//    mViewPager.setOffscreenPageLimit(2);
//    pagerWidth= (int) (getResources().getDisplayMetrics().widthPixels * 0.65f);
//
//    ViewGroup.LayoutParams lp=mViewPager.getLayoutParams();
//    if (lp==null){
//        lp=new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//    }else {
//        lp.width=pagerWidth;
//    }
//    mViewPager.setLayoutParams(lp);
//    mViewPager.setPageMargin(-50);
//
//    findViewById(R.id.ll_main).setOnTouchListener(new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            return mViewPager.dispatchTouchEvent(motionEvent);
//        }
//    });
//
//
//
//    mViewPager.setPageTransformer(true, new MyTransformation());
//
//    mViewPager.setAdapter(new PagerAdapter() {
//
//        @Override
//        public int getCount() {
//            return images.length;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            ImageView iv = imageList.get(position);
//            container.addView(iv);
//            return iv;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(imageList.get(position));
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    });
}