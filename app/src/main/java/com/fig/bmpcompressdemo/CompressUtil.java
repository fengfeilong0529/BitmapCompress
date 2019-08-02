package com.fig.bmpcompressdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CompressUtil {
    private static final String TAG = "CompressUtil";

    /**
     * 质量压缩
     * <p>
     * 质量压缩不会减少图片的像素，它是在保持像素的前提下改变图片的位深及透明度，来达到压缩图片的目的，
     * 图片的长，宽，像素都不会改变，那么bitmap所占内存大小是不会变的
     * <p>
     * PS：对PNG图片无效，因为png是无损压缩
     *
     * @param bit
     * @param quality 质量
     * @return
     */
    public static Bitmap qualityCompress(Bitmap bit, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Log.i(TAG, "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M  宽度为" + bm.getWidth() + "  高度为" + bm.getHeight()
                + "  bytes.length=" + (bytes.length / 1024) + "KB"
                + "  quality=" + quality);
        return bm;
    }

    /**
     * 将图片压缩到指定size
     * <p>
     * 动态降低图片quality
     *
     * @param bit
     * @param maxFileSize 最大文件大小，单位KB
     */
    public static CompressResult compressBmp2LimitSize(Bitmap bit, int maxFileSize) {
        //进行有损压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bit.compress(Bitmap.CompressFormat.JPEG, quality, baos);    //质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        int baosLength = baos.toByteArray().length;
        while (baosLength / 1024 > maxFileSize) {   //循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
            baos.reset();   //重置baos即让下一次的写入覆盖之前的内容
            quality = Math.max(0, quality - 10);    //图片质量每次减少10
            bit.compress(Bitmap.CompressFormat.JPEG, quality, baos);    //将压缩后的图片保存到baos中
            baosLength = baos.toByteArray().length;
            if (quality == 0)   //如果图片的质量已降到最低，则不再进行压缩
                break;
        }
        Bitmap bm = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);

        String tip = "压缩后图片的大小" + ((float) bm.getByteCount() / 1024 / 1024)
                + "M  宽度为" + bm.getWidth() + "  高度为" + bm.getHeight()
                + "  bytes.length=" + (baos.toByteArray().length / 1024) + "KB"
                + "  quality=" + quality;
        Log.i(TAG, tip);

        File file = new File(Environment.getExternalStorageDirectory() + "/compressTest/" + System.currentTimeMillis() + ".jpg");
        FileUtil.writeBitmap2File(file, bm, quality);

        return new CompressResult(bm, tip);
    }

    /**
     * 采样率压缩
     * <p>
     * 采样率压缩其原理是缩放bitamp的尺寸，通过调节其inSampleSize参数，比如调节为2，宽高会为原来的1/2，内存变回原来的1/4
     * <p>
     *
     * @param context
     * @param sampleSize 采样率
     * @return
     */
    public static Bitmap sampleSizeCompress(Context context, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.animal2, options);

        Log.i(TAG, "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M 宽度为" + bm.getWidth() + " 高度为" + bm.getHeight());
        return bm;
    }

    /**
     * matrix缩放法压缩
     * <p>
     * 通过矩阵对图片进行裁剪，也是通过缩放图片尺寸，来达到压缩图片的效果，和采样率的原理一样
     * <p>
     *
     * @param bit
     * @param sx
     * @param sy
     * @return
     */
    public static Bitmap matrixScaleCompress(Bitmap bit, float sx, float sy) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy);
        Bitmap bm = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);

        Log.i(TAG, "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M 宽度为" + bm.getWidth() + " 高度为" + bm.getHeight());
        return bm;
    }

    /**
     * RGB_565压缩
     * <p>
     * RGB_565压缩是通过改用内存占用更小的编码格式来达到压缩的效果。Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。
     * 一般不建议使用ARGB_4444，因为画质实在是辣鸡，如果对透明度没有要求，建议可以改成RGB_565，相比ARGB_8888将节省一半的内存开销
     * <p>
     *
     * @param context
     * @return
     */
    public static Bitmap RGB565Compress(Context context) {
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.animal2, options2);

        Log.i(TAG, "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M 宽度为" + bm.getWidth() + " 高度为" + bm.getHeight());
        return bm;
    }

    /**
     * 创建缩放Bitmap
     * <p>
     * 将图片压缩成用户所期望的长度和宽度，但是这里要说，如果用户期望的长度和宽度和原图长度宽度相差太多的话，图片会很不清晰
     * <p>
     *
     * @param bit
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    public static Bitmap createScaledBitmapCompress(Bitmap bit, int dstWidth, int dstHeight) {
        Bitmap bm = Bitmap.createScaledBitmap(bit, dstWidth, dstHeight, true);

        Log.i(TAG, "压缩后图片的大小" + (bm.getByteCount() / 1024) + "KB 宽度为"
                + bm.getWidth() + " 高度为" + bm.getHeight());
        return bm;
    }

}
