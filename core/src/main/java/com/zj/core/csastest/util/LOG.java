package com.zj.core.csastest.util;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.zj.csastest.core.BuildConfig;


public class LOG {

    private static final String DEFAULT_TAG = "Default";

    private static boolean sEnabled = BuildConfig.DEBUG;

    @VisibleForTesting public static void disable() {
        sEnabled = false;
    }

    public static String makeTag(Class<?> clazz) {
        return makeTag(clazz.getCanonicalName());
    }

    public static String makeTag(String tag) {
        return tag;
    }

    public static void d(String message) {
        d(DEFAULT_TAG, message);
    }

    public static void i(String message) {
        i(DEFAULT_TAG, message);
    }

    public static void e(String message) {
        e(DEFAULT_TAG, message);
    }

    public static void e(Throwable e) {
        e(DEFAULT_TAG, e);
    }

    public static void w(String message) {
        w(DEFAULT_TAG, message);
    }

    public static void d(String tag, String message) {
        if (sEnabled) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (sEnabled) {
            Log.i(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (sEnabled) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, Throwable e) {
        if (sEnabled) {
            Log.e(tag, e != null ? e.getMessage() : "Throwable is null", e);
        }
    }

    public static void w(String tag, String message) {
        if (sEnabled) {
            Log.w(tag, message);
        }
    }
}
