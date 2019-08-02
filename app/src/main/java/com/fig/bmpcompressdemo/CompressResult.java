package com.fig.bmpcompressdemo;

import android.graphics.Bitmap;

public class CompressResult {

    private Bitmap bitmap;
    private String tip;

    public CompressResult(Bitmap bitmap, String tip) {
        this.bitmap = bitmap;
        this.tip = tip;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
