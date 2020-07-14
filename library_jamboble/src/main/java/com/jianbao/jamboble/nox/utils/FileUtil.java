package com.jianbao.jamboble.nox.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;

import com.jianbao.jamboble.utils.TimeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

@SuppressLint("NewApi")
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * 判断Sdcard是否可用
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SDCard剩余容量，单位Byte
     */
    public static long getSDCardFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSizeLong();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocksLong();
        // 返回SD卡空闲大小
        return freeBlocks * blockSize; // 单位Byte
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


    /**
     * 创建文件
     *
     * @param dir      文件目录
     * @param filename 文件名称
     * @return
     */
    public static File createFile(String dir, String filename) {
        try {
            File dirFile = new File(dir);
            if (!dirFile.exists()) dirFile.mkdirs();
            File file = new File(dir, filename);
            file.createNewFile();
            return file;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*删除文件
    *
    * @param dir      文件目录
    * @param filename 文件名称
    * @return
    *
    * */

    public static boolean deleteFile(String dir, String filename) {
        try {
            File file = new File(dir, filename);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void write2File(String dir, String filename, String content, boolean isAppend) {
        OutputStream os = null;
        try {
            File file = createFile(dir, filename);
            os = new FileOutputStream(file, isAppend);
            os.write(content.getBytes());
            os.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void write2File(String dir, String filename, InputStream is, boolean isAppend) {
        OutputStream os = null;
        try {
            File file = createFile(dir, filename);
            os = new FileOutputStream(file, isAppend);
            byte[] buffer = new byte[1024 * 10];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * 把write_str写到外置存储卡里medica/finderror文件夹下里的时间命名的文件里,里面自己起了新线程
     * </p>
     * <p>
     * </p>
     * 2015年7月2日
     */
    public static void writeSDFileThread(final String write_str) {
        new Thread() {
            public void run() {
                FileOutputStream fos = null;
                try {
                    String directoryName = Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + "medica" + File.separator + "finderror";//
                    File file = new File(directoryName);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String fileName = directoryName + "/"
                            + TimeUtil.getTime() + ".txt";
                    File file2 = new File(fileName);
                    if (!file2.exists()) {
                        file2.createNewFile();
                    }
                    fos = new FileOutputStream(file2);

                    byte[] bytes = write_str.getBytes();

                    fos.write(bytes);

                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    /**
     * 获取文件大小
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        long fileSize = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File childFile = files[i];
                    if (childFile.isDirectory()) {
                        fileSize += getFileSize(childFile);
                    } else {
                        fileSize += childFile.length();
                    }
                }
            } else {
                fileSize += file.length();
            }
        }
        return fileSize;
    }

    /**
     * 创建文件名
     *
     * @param suffix
     * @return
     */
    public static String generateFilePath(String prefix) {
        Calendar calendar = Calendar.getInstance();
        long milliseconds = System.currentTimeMillis();
        calendar.setTimeInMillis(milliseconds);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //不能以"/"开头
        return prefix + "_"+ year + "_" + month + "_" + day + "_" + milliseconds;
    }
}













