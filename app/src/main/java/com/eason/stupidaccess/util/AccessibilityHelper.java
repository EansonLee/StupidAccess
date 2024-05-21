package com.eason.stupidaccess.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AccessibilityHelper {

    private AccessibilityService accessibilityService;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;

    public AccessibilityHelper(AccessibilityService service) {
        this.accessibilityService = service;
        this.windowManager = (WindowManager) service.getSystemService(AccessibilityService.WINDOW_SERVICE);
        this.displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    }

    public boolean click(float x, float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Path clickPath = new Path();
            clickPath.moveTo(x, y);

            GestureDescription.StrokeDescription clickStroke = new GestureDescription.StrokeDescription(clickPath, 0, 100);
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(clickStroke);

            return accessibilityService.dispatchGesture(gestureBuilder.build(), null, null);
        } else {
            // 低于 Android N 的版本不支持 dispatchGesture
            return false;
        }
    }

    public int getScreenWidth() {
        return displayMetrics.widthPixels;
    }

    public int getScreenHeight() {
        return displayMetrics.heightPixels;
    }
}