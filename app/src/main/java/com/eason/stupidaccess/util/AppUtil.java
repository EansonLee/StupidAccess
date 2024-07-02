package com.eason.stupidaccess.util;


import android.content.Context;
import android.app.ActivityManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public class AppUtil {


    public static void forceStopPackage(Context context,String packageName) {
        Log.i(Config.DEBUG_TAG, "forceStopPackage packageName = " + packageName);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method forceStopPackage = null;
        try {
            forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (NoSuchMethodException e) {
            Log.e(Config.DEBUG_TAG, "forceStopPackage NoSuchMethodException error = " + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(Config.DEBUG_TAG, "forceStopPackage IllegalAccessException error = " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(Config.DEBUG_TAG, "forceStopPackage InvocationTargetException error = " + e.getMessage());
        }

    }
}

