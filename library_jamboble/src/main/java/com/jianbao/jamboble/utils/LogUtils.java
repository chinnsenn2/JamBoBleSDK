package com.jianbao.jamboble.utils;


import com.jianbao.jamboble.BuildConfig;
import com.orhanobut.logger.Logger;

public class LogUtils {
    public static void i(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Logger.i(TAG, message);
        }
    }

    public static void v(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Logger.v(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Logger.d(TAG, message);
        }
    }


    public static void w(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Logger.w(TAG, message);
        }
    }


    public static void e(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Logger.e(TAG, message);
        }
    }
    public static void i(String message) {
        if (BuildConfig.DEBUG) {
            Logger.i(message);
        }
    }

    public static void v(String message) {
        if (BuildConfig.DEBUG) {
            Logger.v(message);
        }
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Logger.d(message);
        }
    }


    public static void w(String message) {
        if (BuildConfig.DEBUG) {
            Logger.w(message);
        }
    }


    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Logger.e(message);
        }
    }
}