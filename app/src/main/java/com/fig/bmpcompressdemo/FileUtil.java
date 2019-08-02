package com.fig.bmpcompressdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * @param file
     * @param mBitmap
     * @return file path
     */
    //把 bitmap图片存储为文件
    public static void writeBitmap2File(File file, Bitmap mBitmap, int quality) {
        if (file == null)
            return;
        FileOutputStream out = null;
        try {
            createNewFile(file);

            out = new FileOutputStream(file);
            if (!mBitmap.isRecycled()) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            }
            out.flush();
            out.getFD().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //把 数组图片存储为文件
    public static void writePhoto2File(File file, byte[] data) {
        FileOutputStream out = null;
        try {
            createNewFile(file);

            out = new FileOutputStream(file);
            out.write(data, 0, data.length);
            out.flush();
            out.getFD().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取文件到数组
    public static byte[] readFileBytes(File file) {
        byte[] data = null;
        if (file != null && file.exists()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                int buf_size = 1024;
                byte[] buffer = new byte[buf_size];
                int len = 0;
                while (-1 != (len = in.read(buffer, 0, buf_size))) {
                    bos.write(buffer, 0, len);
                }
                data = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {// in 可能会null，将IOException 改成 Exception
                    e.printStackTrace();
                }
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (Exception e) {// in 可能会null，将IOException 改成 Exception
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    //文件 拷贝
    public static void fileCopy(File from, File target) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            createNewFile(target);
            input = new FileInputStream(from);
            output = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            output.getFD().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void createNewFile(File file) throws IOException {
        if (file != null) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            } else if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        }
    }

    //删除文件夹 和文件夹下所有文件
    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists())
            return;
        if (dir.isDirectory()) {//如果是文件夹，删除子项
            for (File file : dir.listFiles()) {
                deleteDirWithFile(file); // 递规的方式删除文件夹
            }
        }
        dir.delete();// 删除本身
    }


    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     *
     * @param mContext
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static String getStoragePath(Context mContext) {
        List<File> list = getStoragePathList(mContext);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0).getAbsolutePath();
        }
    }

    public static List<File> getStoragePathList(Context mContext) {
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        List<File> resultStr = new ArrayList<>();
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Object result = getVolumeList.invoke(mStorageManager);

            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method getState = storageVolumeClazz.getMethod("getState");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");

            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);

                String state = (String) getState.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (state.equals("mounted") && removable) {
                    String path = (String) getPath.invoke(storageVolumeElement);
                    File file = new File(path);
                    if (file.exists() && file.isDirectory()) {
                        resultStr.add(file);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }


    /**
     * 获得SD卡总大小
     *
     * @return gb
     */
    public static double getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long totalBytes = stat.getTotalBytes();
        double totalGB = totalBytes / (1024 * 1024 * 1024d);
        return totalGB;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return gb
     */
    public static double getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBytes = stat.getAvailableBytes();
        double availableGB = availableBytes / (1024 * 1024 * 1024d);
        return availableGB;
    }

    public static double getSDAvailableByteSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBytes = stat.getAvailableBytes();
        return availableBytes;
    }

    // return the size of file
    public static int getFileSize(File fileName){
        FileInputStream fis = null;
        int mFileSize = 0;
        try {
            fis = new FileInputStream(fileName);
            mFileSize = fis.available();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return mFileSize;
    }
}
