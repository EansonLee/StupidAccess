package com.eason.stupidaccess.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


import com.eason.stupidaccess.util.Config;

import java.util.List;

public class AlipayForestMonitor {


    /**
     * 启动支付宝界面
     * adb shell am start com.eg.android.AlipayGphone/com.eg.android.AlipayGphone.AlipayLogin
     */
    public static void startAlipay(Context mContext) {
        Intent intent = new Intent();
        intent.setPackage("com.alibaba.android.rimet");
        intent.setClassName("com.alibaba.android.rimet", "com.alibaba.android.rimet.biz.LaunchHomeActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 自动点击进入蚂蚁森林界面
     */
    public static void enterForestUI(AccessibilityNodeInfo nodeInfo, String packageName, String className) {
        if (!packageName.equals("com.alibaba.android.rimet")) {
            return;
        }
        Log.d(Config.TAG, "进入DingTalk登录页 ");
        List<AccessibilityNodeInfo> policyNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/cb_privacy");
        if (!policyNodeList.isEmpty()) {
            policyNodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.d(Config.TAG, "点击协议");
        }
        List<AccessibilityNodeInfo> btnNodeList = nodeInfo.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/ll_next");
        Log.e("bbbbbbbbbbbbbb", btnNodeList.toString());
//        if (btnNodeList != null && btnNodeList.get(0) == null) {
//            btnNodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            Log.d(Config.TAG, "点击按钮");
//        }
//        if (nodeInfo != null) {
//            // 找到界面中蚂蚁森林的文字
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("蚂蚁森林");
//
//            if (list == null) {
//                Log.d(Config.TAG, "enterForestUI finding no");
//                return;
//            } else {
//                Log.d(Config.TAG, "enterForestUI finding yes");
//            }
//
//            for (AccessibilityNodeInfo item : list) {
//                /**
//                 *  蚂蚁森林本身不可点击，但是他的父控件可以点击
//                 */
//                AccessibilityNodeInfo parent = item.getParent();
//                if (null != parent && parent.isClickable()) {
//                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d(Config.TAG, "item = " + item.toString() + ", parent click = " + parent.toString());
//                    break;
//                }
//            }
//        }
    }

    public static void policy(AccessibilityNodeInfo nodeInfo, String packageName, String className) {
        /**
         * 蚂蚁森林界面
         */
        if (packageName.equals("com.eg.android.AlipayGphone") &&
                ("com.alipay.mobile.nebulacore.ui.H5Activity".equals(className)
                        || "com.uc.webkit.bf".equals(className))) {

            if (nodeInfo != null) {
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    AccessibilityNodeInfo child = nodeInfo.getChild(i);
                    if ("com.uc.webview.export.WebView".equals(child.getClassName())) {
                        Log.d(Config.TAG, "找到蚂蚁森林的 webView count = " + child.getChildCount());

                        findEveryViewNode(child);
                        break;
                    }
                }
            } else {
                Log.d(Config.TAG, "alipayPolicy = null");
            }
        }

    }

    public static void findEveryViewNode(AccessibilityNodeInfo node) {
        if (null != node && node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                // 有时 child 为空
                if (child == null) {
                    continue;
                }

                Log.d(Config.DEBUG_TAG, "findEveryViewNode = " + child);
                Log.d(Config.DEBUG_TAG, "findEveryViewNode count= " + child.getChildCount());
//                String className = child.getClassName().toString();
//                if ("android.widget.TextView".equals(className)) {
//                    Log.d(Config.DEBUG_TAG, "Text 的节点数据 text = " + child.getText() + ", descript = " + child.getContentDescription() + ", className = " + child.getClassName() + ", resId = " + child.getViewIdResourceName());
//
//                    boolean isClickable = child.isClickable();
//                    boolean isResIdNull = child.getViewIdResourceName() == null ? true : false;
//
//                    /**
//                     * 好友的能量不能收取，因为支付宝在onTouch事件中return true,导致不会触发OnClick方法
//                     *
//                     * 但是支付宝中的蚂蚁森林可以收取自己的能量
//                     */
//                    if (isClickable && isResIdNull && child.getText() != null && child.getText().toString().contains("考勤打卡")) {
//                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        Log.d(Config.DEBUG_TAG, "考勤打卡 成功点击");
//                    }

//                if (!child.findAccessibilityNodeInfosByText("考勤打卡").isEmpty()) {
//                    Log.w(Config.DEBUG_TAG, "child web count："+child.getChildCount());
                    for (int k = 0; k < child.getChildCount(); k++) {
                        Log.w(Config.DEBUG_TAG, "child web："+child.getChild(k).getChildCount());
                        AccessibilityNodeInfo childNode = child.getChild(k);
                        if (childNode.getText().equals("考勤打卡")) {
                            if (childNode.isClickable()) {
                                childNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                Log.d(Config.DEBUG_TAG, "考勤打卡 成功点击");
                            }else {
                                childNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                Log.d(Config.DEBUG_TAG, "考勤打卡 成功点击");
                            }
                        }
                    }
//                }

                // 递归调用
//                findEveryViewNode(child);
            }
        }
    }

}