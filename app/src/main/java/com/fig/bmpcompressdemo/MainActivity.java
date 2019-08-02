package com.fig.bmpcompressdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ivBeforeCompress)
    ImageView mIvBeforeCompress;
    @BindView(R.id.ivAfterCompress)
    ImageView mIvAfterCompress;
    @BindView(R.id.tvTips)
    TextView mTvTips;
    @BindView(R.id.etQuality)
    EditText mEtQuality;
    @BindView(R.id.btnQualityCompress)
    Button mBtnQualityCompress;
    @BindView(R.id.etSampleSize)
    EditText mEtSampleSize;
    @BindView(R.id.btnSampleSizeCompress)
    Button mBtnSampleSizeCompress;
    @BindView(R.id.etSx)
    EditText mEtSx;
    @BindView(R.id.etSy)
    EditText mEtSy;
    @BindView(R.id.btnMatrixScaleCompress)
    Button mBtnMatrixScaleCompress;
    @BindView(R.id.btnRGB565Compress)
    Button mBtnRGB565Compress;
    @BindView(R.id.etDstWidth)
    EditText mEtDstWidth;
    @BindView(R.id.etDstHeight)
    EditText mEtDstHeight;
    @BindView(R.id.btnCreateScaledBitmapCompress)
    Button mBtnCreateScaledBitmapCompress;

    Bitmap bitmap;
    @BindView(R.id.etQualityLimit)
    EditText mEtQualityLimit;
    @BindView(R.id.btnQualityLimitCompress)
    Button mBtnQualityLimitCompress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.animal2);
    }


    @OnClick({R.id.btnQualityCompress, R.id.btnSampleSizeCompress, R.id.btnMatrixScaleCompress, R.id.btnRGB565Compress, R.id.btnCreateScaledBitmapCompress, R.id.btnQualityLimitCompress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnQualityCompress:
                qualityCompress();
                break;
            case R.id.btnSampleSizeCompress:
                sampleSizeCompress();
                break;
            case R.id.btnMatrixScaleCompress:
                matrixScaleCompress();
                break;
            case R.id.btnRGB565Compress:
                RGB565Compress();
                break;
            case R.id.btnCreateScaledBitmapCompress:
                createScaledBitmapCompress();
                break;
            case R.id.btnQualityLimitCompress:
                compressBmp2LimitSize();
                break;
        }
    }

    /**
     * 质量压缩
     */
    public void qualityCompress() {
        String trim = mEtQuality.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.showToastShort(this, "请设置压缩质量");
            return;
        }
        Bitmap compressedBmp = CompressUtil.qualityCompress(bitmap, Integer.parseInt(trim));
        mIvAfterCompress.setImageBitmap(compressedBmp);
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n压缩后图片的大小 " + ((float) compressedBmp.getByteCount() / 1024 / 1024) + "M 宽度为 " + compressedBmp.getWidth() + " 高度为 " + compressedBmp.getHeight());
    }

    /**
     * 采样率压缩
     */
    public void sampleSizeCompress() {
        String trim = mEtSampleSize.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.showToastShort(this, "请设置采样率");
            return;
        }
        Bitmap compressedBmp = CompressUtil.sampleSizeCompress(this, Integer.parseInt(trim));
        mIvAfterCompress.setImageBitmap(compressedBmp);
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n" + "压缩后图片的大小" + ((float) compressedBmp.getByteCount() / 1024 / 1024) + "M 宽度为" + compressedBmp.getWidth() + " 高度为" + compressedBmp.getHeight());
    }

    /**
     * Matrix缩放法压缩
     */
    public void matrixScaleCompress() {
        String sx = mEtSx.getText().toString().trim();
        String sy = mEtSy.getText().toString().trim();
        if (TextUtils.isEmpty(sx)) {
            ToastUtil.showToastShort(this, "请设置sx");
            return;
        }
        if (TextUtils.isEmpty(sy)) {
            ToastUtil.showToastShort(this, "请设置sy");
            return;
        }
        Bitmap compressedBmp = CompressUtil.matrixScaleCompress(bitmap, Float.parseFloat(sx), Float.parseFloat(sy));
        mIvAfterCompress.setImageBitmap(compressedBmp);
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n" + "压缩后图片的大小" + ((float) compressedBmp.getByteCount() / 1024 / 1024) + "M 宽度为" + compressedBmp.getWidth() + " 高度为" + compressedBmp.getHeight());
    }

    /**
     * RGB_565压缩
     */
    public void RGB565Compress() {
        Bitmap compressedBmp = CompressUtil.RGB565Compress(this);
        mIvAfterCompress.setImageBitmap(compressedBmp);
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n" + "压缩后图片的大小" + ((float) compressedBmp.getByteCount() / 1024 / 1024) + "M 宽度为" + compressedBmp.getWidth() + " 高度为" + compressedBmp.getHeight());
    }

    /**
     * createScaledBitmap压缩法
     */
    public void createScaledBitmapCompress() {
        String dstWidth = mEtDstWidth.getText().toString().trim();
        String dstHeight = mEtDstHeight.getText().toString().trim();
        if (TextUtils.isEmpty(dstWidth)) {
            ToastUtil.showToastShort(this, "请设置 dstWidth");
            return;
        }
        if (TextUtils.isEmpty(dstHeight)) {
            ToastUtil.showToastShort(this, "请设置 dstHeight");
            return;
        }
        Bitmap compressedBmp = CompressUtil.createScaledBitmapCompress(bitmap, Integer.parseInt(dstWidth), Integer.parseInt(dstHeight));
        mIvAfterCompress.setImageBitmap(compressedBmp);
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n" + "压缩后图片的大小" + ((float) compressedBmp.getByteCount() / 1024 / 1024) + "M 宽度为" + compressedBmp.getWidth() + " 高度为" + compressedBmp.getHeight());
    }

    /**
     * 将图片压缩到指定kb
     */
    public void compressBmp2LimitSize() {
        checkWriteStoragePermission();
        String limitSize = mEtQualityLimit.getText().toString().trim();
        if (TextUtils.isEmpty(limitSize)) {
            ToastUtil.showToastShort(this, "请设置 limitSize");
            return;
        }
        CompressResult result = CompressUtil.compressBmp2LimitSize(this.bitmap, Integer.parseInt(limitSize));
        mIvAfterCompress.setImageBitmap(result.getBitmap());
        showTip("压缩前图片的大小 " + ((float) bitmap.getByteCount() / 1024 / 1024) + "M 宽度为 " + bitmap.getWidth() + " 高度为 " + bitmap.getHeight() + "\n" + result.getTip());

        File file = new File(Environment.getExternalStorageDirectory() + "/compressTest/");
        if (file.exists()) {
            ToastUtil.showToastLong(this, "压缩后的图片已保存到：" + file.getPath());
        }
    }

    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }


    public void showTip(String tip) {
        mTvTips.setText(tip);
    }

}
