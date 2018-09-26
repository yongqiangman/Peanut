package com.yqman.evan.network.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yqman on 2016/5/9.
 * Bitmap 是经常使用的一类用于图片显示的对象，一个Bitmap真正显示出来往往需要通过一些特定的操作。下面对Bitmap的一些常规操作做一点简单介绍：
 * 0、Bitmap output = Bitmap.createBitmap(tmpBitmap.getWidth(), tmpBitmap.getHeight(), Bitmap.Config.ARGB_8888);
 * 创建了一个指定宽度和高度Bitmap
 * 1、Canvas canvas = new Canvas(output)； 这里就获得了一个对Bitmap进行操作的Canvas
 * canvas.drawBitmap(bitmap, matrix, null);向画布中绘制bitmap视图，
 * 其中Matrix可以通过matrix.postTranslate(translateX, translateY);设定bitmap在canvas(0,0)坐标上对应的坐标
 * 其中Matrix可以通过matrix.postScale(totalRatio, totalRatio);设定bitmap在canvas上的伸缩比例，大于1拉伸、小于1缩小
 * 2、bitmap = Bitmap.createBitmap(bitmap,bt_widthStart,bt_heightStart,bt_width,bt_height); 对bitmap进行裁剪左上角坐标
 * (bt_widthStart,bt_heightStart),右下角坐标(bt_widthStart+bt_width,bt_heightStart+bt_height)
 * 3、tmpBitmap = Bitmap.createScaledBitmap(bmp, radius, radius, false); 对bitmap进行指定比例的缩小
 * 4、Bitmap bitmap = BitmapFactory.decodeXX(SS....,options);通过后面的BitmapFactory.Options options 参数可以获取、设置对应图片资源的压缩比例
 * 其中options.inJustDecodeBounds = true;BitmapFactory.decodeResource(resources,resID,options);用于获取待解析图片资源的高度和宽度
 * 其中options.inJustDecodeBounds = false; options.inSampleSize = ？; BitmapFactory.decodeResource(resources,resID,
 * options);用于获取待解析图片资源进行一定的压缩，inSampleSize值越大，则压缩越厉害
 */
public class DrawableFactory {
    private static final String TAG = "DrawableFactory";
    private static final boolean DEBUG = true;
    private final Resources mResources;
    private final int mScreenWidth;
    private final int mScreenHeight;

    public DrawableFactory(Context context) {
        mResources = context.getResources();
        Point outSize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null && wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getSize(outSize);
        }
        mScreenHeight = outSize.y;
        mScreenWidth = outSize.x;
    }

    /**
     * 以屏幕为基准，压缩sample倍得到Bitmap；如widthSample == heightSample均为1，则获取的图片最大是屏幕的宽高
     *
     * @param resID        资源ID
     * @param widthSample  相对于屏幕宽度的压缩比
     * @param heightSample 相对于屏幕高度的压缩比
     *
     * @return Bitmap值
     */
    public Bitmap getBitmapWithSample(int resID, int widthSample, int heightSample) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mResources, resID, options);
        options.inSampleSize = calculateInSampleSize(options, mScreenWidth / widthSample, mScreenHeight / heightSample);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(mResources, resID, options);
    }

    public Bitmap getBitmapWithSample(String imgPath, int widthSample, int heightSample) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options, mScreenWidth / widthSample, mScreenHeight / heightSample);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    /**
     * @param bytes        需要解码bytes
     * @param widthSample  值如果小于1那么证明宽度是不需要压缩的
     * @param heightSample 值如果小于1那么证明高度是不需要压缩的
     */
    public Bitmap getBitmapWithSample(byte[] bytes, int widthSample, int heightSample) {
        if (widthSample <= 1 && heightSample <= 1) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inSampleSize =
                    calculateInSampleSize(options, mScreenWidth / widthSample, mScreenHeight / heightSample);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
    }

    /**
     * 根据指定的宽度和高度 对目标图片进行压缩处理，保持原图片比例不变
     */
    public Bitmap getBitmapWithValue(int resID, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mResources, resID, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(mResources, resID, options);
    }

    public Bitmap getBitmapWithValue(String imgPath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    /**
     * 根据预期的宽度和高度 计算需要压缩多少倍
     * 最终得到的的高度和宽度两者都不能大于预期的高度和宽度
     *
     * @param options   处理对象
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     *
     * @return 压缩比
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (DEBUG) {
            Log.d(TAG, "Width = " + width);
            Log.d(TAG, "Height = " + height);
        }
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
            if (DEBUG) {
                Log.d(TAG, "inSampleSize = " + inSampleSize);
            }
        }
        return inSampleSize;
    }

    /**
     * 根据指定width和height对Bitmap进行一次裁剪，取居中的区域
     */
    private Bitmap getTrimBitmap(Bitmap bitmap, int width, int height) {
        int bt_height = bitmap.getHeight();
        int bt_width = bitmap.getWidth();
        int bt_widthStart = 0;
        int bt_heightStart = 0;
        float scale = (float) width / height;
        float scaleTmp = (float) bt_width / bt_height;
        if (scaleTmp > scale) {  //Bitmap的宽度超标，需要裁剪，取中间部分
            int tmp = (int) (scale * bt_height);
            bt_widthStart = (bt_width - tmp) / 2;
            bt_width = tmp;
        } else { //Bitmap的高度超标，需要裁剪，取中间部分
            int tmp = (int) (bt_width / scale);
            bt_heightStart = (bt_height - tmp) / 2;
            bt_height = tmp;
        }
        bitmap = Bitmap.createBitmap(bitmap, bt_widthStart, bt_heightStart, bt_width, bt_height);
        return bitmap;
    }

    /**
     * 初始Bitmap对象的缩放裁剪过程
     *
     * @param bmp    初始Bitmap对象
     * @param radius 圆形图片直径大小
     *
     * @return 返回一个圆形的缩放裁剪过后的Bitmap对象
     */
    public Bitmap getRoundBitmap(Bitmap bmp, int radius) {
        Bitmap tmpBitmap;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            bmp = getTrimBitmap(bmp, radius, radius);
            tmpBitmap = Bitmap.createScaledBitmap(bmp, radius, radius, false);
            /**这个方法很酷炫，你可以忽略bmp的大小，然后给定后面的width和height系统就会返回你想要的大小的bitmap
             * 底层是通过对图片进行拉伸处理，因此如果原图和目标图形比例差距过大容易导致图片变形。
             * 注意：它不会裁剪只是拉伸,因此我们使用getTrimBitmap方法进行了预先的裁剪
             * */
        } else {
            tmpBitmap = bmp;
        }
        Bitmap output = Bitmap.createBitmap(tmpBitmap.getWidth(), tmpBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight());//tmpBitmap是预期大小的Bitmap

        paint.setAntiAlias(true); //消除锯齿
        paint.setFilterBitmap(true); //对位图进行滤波处理

        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399")); //画笔的颜色

        canvas.drawARGB(0, 0, 0, 0);//alpha无色透明
        canvas.drawCircle(tmpBitmap.getWidth() / 2 + 0.7f,
                tmpBitmap.getHeight() / 2 + 0.7f, tmpBitmap.getWidth() / 2 + 0.1f, paint);//x、y坐标 半径 画笔

        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//混合模式
        canvas.drawBitmap(tmpBitmap, rect, rect, paint);
        return output;
    }

    /**
     * @param bmp 操作的bitmap目标文件
     *
     * @return 返回当前bitmap的对应filePath得到的Uri
     */
    public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(context.getCacheDir(),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}