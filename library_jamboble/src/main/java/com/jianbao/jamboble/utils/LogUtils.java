package com.jianbao.jamboble.utils;


import com.orhanobut.logger.Logger;

public class LogUtils {
    public static boolean isPrint = false;
    public static String TAG = "JamBoBle";

    public static void i(String tag, String message) {
        if (isPrint) {
            Logger.i(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isPrint) {
            Logger.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isPrint) {
            Logger.d(tag, message);
        }
    }


    public static void w(String tag, String message) {
        if (isPrint) {
            Logger.w(tag, message);
        }
    }


    public static void e(String tag, String message) {
        if (isPrint) {
            Logger.e(tag, message);
        }
    }

    public static void i(String message) {
        if (isPrint) {
            Logger.i(message);
        }
    }

    public static void v(String message) {
        if (isPrint) {
            Logger.v(message);
        }
    }

    public static void d(String message) {
        if (isPrint) {
            Logger.d(message);
        }
    }


    public static void w(String message) {
        if (isPrint) {
            Logger.w(message);
        }
    }


    public static void e(String message) {
        if (isPrint) {
            Logger.e(message);
        }
    }
}