package com.eason.stupidaccess.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.lifecycle.LiveDataKt;

import com.blankj.utilcode.util.ThreadUtils;
import com.eason.stupidaccess.AccessApp;
import com.eason.stupidaccess.activity.TaskActivity;
import com.eason.stupidaccess.util.AccessibilityHelper;
import com.eason.stupidaccess.util.Config;
import com.eason.stupidaccess.util.DateTimeUtil;
import com.eason.stupidaccess.util.LiveDataBus;
import com.eason.stupidaccess.util.ShareUtil;

import java.util.List;


public class AccessibilityServiceMonitor extends AccessibilityService {

    private static final String TAG = AccessibilityServiceMonitor.class.getSimpleName();

    public static final String ACTION_UPDATE_SWITCH = "action_update_switch";
    public static final String ACTION_ALAM_TIMER = "action_alarm_timer";

    private boolean isNewday;

    /**
     * Keep App 辅助功能
     */
    private boolean isKeepEnable = true;

    /**
     * 支付宝 App 辅助功能
     */
    private boolean isAlipayForest = true;

    /**
     * 联通手机营业厅 辅助功能
     */
    private boolean isLiangTongEnable = true;

    /**
     * 微信运动的自动点赞器
     */
    private boolean isWeChatMotionEnable = true;


    //是否已点击登录
    private boolean mHadChecked = false;
    //是否已点击密码
    private boolean mHadPwd = false;

    //是否已点击工作台
    private boolean mHadWork = false;

    //是否已点击考勤打卡
    private boolean mHadCard = false;

    private H mHandle = new H();
    private static final int MSG_DELAY_ENTER_FOREST = 0;
    private static final int MSG_DELAY_ENTER_LIANGTONG = 1;
    private static final int DEFAULT_DELAY_TIME = 1 * 1000;

    private MyBroadCast myBroadCast;

    private AccessibilityHelper accessibilityHelper;

    private ShareUtil mShareUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        mShareUtil = new ShareUtil(this);
        Log.d(TAG, "onCreate");
//        myBroadCast = new MyBroadCast();
//        myBroadCast.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();
        Log.d(TAG, "onStartCommand Aciton: " + action);

        if (ACTION_UPDATE_SWITCH.equals(action)) {
            updateSwitchStatus();
        } else if (ACTION_ALAM_TIMER.equals(action)) {
            AccessApp.Companion.startAlarmTask(this);
            startUI();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.packageNames = new String[]{"com.alibaba.android.rimet", "com.gotokeep.keep", "com.eg.android.AlipayGphone", "com.sinovatech.unicom.ui", "com.tencent.mm"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);
        accessibilityHelper = new AccessibilityHelper(this);
        LiveDataBus.postInt(Config.KEY_STATE, TaskActivity.SERVICE_STATUS_ON);
        Toast.makeText(this, "源计划已启动！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(Config.TAG, "onAccessibilityEvent：" + event);
        if (event == null) return;
        if (event.getPackageName() == null) {
            return;
        }
        if (event.getClassName() == null) {
            return;
        }
        int eventType = event.getEventType();
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        Log.d(Config.TAG, "packageName = " + packageName + ", className = " + className);

//        switch (eventType) {
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
////            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                if (isAlipayForest) {
//                    if (isNewday) {
//                        AlipayForestMonitor.enterForestUI(getRootInActiveWindow(), packageName, className);
//                    }
//                    AlipayForestMonitor.policy(getRootInActiveWindow(), packageName, className);
//                }
//                Log.e("aada", "TYPE_WINDOW_CONTENT_CHANGED---------");
//                List<AccessibilityNodeInfo> policyNodeList1 = event.getSource().findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/cb_privacy");
//                Log.e("aada","policyNodeList---------"+policyNodeList1);
//                if (!policyNodeList1.isEmpty()) {
//                    policyNodeList1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d(Config.TAG, "点击协议");
//                }
//                break;
//        }
        if (isAlipayForest) {
//            if (isNewday) {
            //启动页面
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && className.equals("android.widget.FrameLayout") && !mHadChecked) {
                List<AccessibilityNodeInfo> policyNodeList = event.getSource().findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/cb_privacy");
                if (!policyNodeList.isEmpty()) {
                    policyNodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(Config.TAG, "点击协议");

                    List<AccessibilityNodeInfo> btnNodeList = event.getSource().findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/ll_next");
                    btnNodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(Config.TAG, "点击按钮");
                    mHadChecked = true;
                }
            }

            //弹出底部弹框
            if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && className.equals("androidx.recyclerview.widget.RecyclerView")) {
                if (event.getSource() == null) {
                    return;
                }
                List<AccessibilityNodeInfo> pwdNodeList = event.getSource().findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/factor_list_view");
                if (!pwdNodeList.isEmpty()) {
                    int count = pwdNodeList.get(0).getChildCount();
                    Log.w(Config.DEBUG_TAG, "count：" + count);
                    for (int i = 0; i < count; i++) {
                        AccessibilityNodeInfo child = pwdNodeList.get(0).getChild(i);
                        Log.w(Config.DEBUG_TAG, "child：" + child);
                        // 有时 child 为空
                        if (child != null && count > 1 && i == 2) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        } else if (child != null && count == 1) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                    Log.d(Config.DEBUG_TAG, "点击密码登录");
//                mHadPwd = true;
                } else {
                    Log.e(Config.TAG, "密码节点为null");
                }
            }

            //进入密码页面
            if (eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED && className.equals("android.widget.EditText")) {
                Log.d(Config.TAG, "输入密码");
                Bundle arguments = new Bundle();
                if (mShareUtil == null) return;
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mShareUtil.getString(Config.KEY_PWD, ""));
                if (event.getSource() == null) {
                    return;
                }
                event.getSource().performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                mHadPwd = true;
            }

            //进入登录页
            if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                Log.d(Config.TAG, "点击登录");
                List<AccessibilityNodeInfo> loginNodeList = getRootInActiveWindow().findAccessibilityNodeInfosByText("登录");
                if (!loginNodeList.isEmpty()) {
                    AccessibilityNodeInfo btn = loginNodeList.get(0);
                    String text = btn.getText().toString();
                    Log.w(Config.DEBUG_TAG, "进入登录：" + text);
                    if (btn.isClickable()) {
                        btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.w(Config.DEBUG_TAG, "登录按钮可以点击");
                    } else {
                        Log.w(Config.DEBUG_TAG, "登录按钮不可点击");
                        btn.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.w(Config.DEBUG_TAG, "强制点击");
                    }
                } else {
                    Log.w(Config.DEBUG_TAG, "登录节点为null");
                }
            }

            if (!getRootInActiveWindow().findAccessibilityNodeInfosByText("工作台").isEmpty() && !mHadWork) {
                Log.w(Config.DEBUG_TAG, "进入工作台");
                if (event.getSource() == null) {
                    return;
                }
                AccessibilityNodeInfo nodeWork = getRootInActiveWindow().findAccessibilityNodeInfosByText("工作台").get(0);
                if (nodeWork != null) {
                    Log.w(Config.DEBUG_TAG, nodeWork.getText().toString());
                    if (nodeWork.isClickable()) {
                        nodeWork.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.w(Config.DEBUG_TAG, "点击工作台");
                    } else {
                        Log.w(Config.DEBUG_TAG, "工作台不可点击");
                        nodeWork.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    mHadWork = true;
                }
            }
            List<AccessibilityNodeInfo> cardNodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/h5_pc_container");
            if (!cardNodes.isEmpty()) {
                Log.e(Config.DEBUG_TAG, "工作台cardNodes：" + cardNodes.size());
                AccessibilityNodeInfo nodeInfo = cardNodes.get(0);
                if (nodeInfo != null) {
                    for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                        AccessibilityNodeInfo child = nodeInfo.getChild(i);
                        Log.w(Config.DEBUG_TAG, "工作台 child = " + child.toString());
                        if ("com.uc.webview.export.WebView".equals(child.getClassName())) {
                            if (mShareUtil == null) return;
                            int delay = mShareUtil.getInt(Config.KEY_DELAY, 15000);
                            Log.d(Config.DEBUG_TAG, "点击打卡item");
                            accessibilityHelper.click(mShareUtil.getInt(Config.KEY_ITEM_X, 140), mShareUtil.getInt(Config.KEY_ITEM_Y, 1094));
                            ThreadUtils.runOnUiThreadDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(Config.DEBUG_TAG, "点击打卡card");
                                    accessibilityHelper.click(mShareUtil.getInt(Config.KEY_CARD_X, 568),
                                            mShareUtil.getInt(Config.KEY_CARD_Y, 1450));
                                }
                            }, delay);
                            break;
                        }
                    }
                } else {
                    Log.d(Config.DEBUG_TAG, "alipayPolicy = null");
                }
            }
//            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private class H extends Handler {

        public H() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELAY_ENTER_FOREST:
                    break;
                case MSG_DELAY_ENTER_LIANGTONG:
//                    startLiangTongUI();
                    break;
            }
        }
    }

    class MyBroadCast extends BroadcastReceiver {

        public void init(Context mContext) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();
            Log.d(Config.TAG, "action = " + action);

            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                isNewday = isNewDay();
                if (isNewday) {
                    startUI();
                }
            }
        }
    }


    /**
     * 更新开关状态
     */
    private void updateSwitchStatus() {

        isKeepEnable = mShareUtil.getBoolean(Config.APP_KEEP, true);
        isAlipayForest = mShareUtil.getBoolean(Config.APP_ALIPAY_FOREST, true);
        isLiangTongEnable = mShareUtil.getBoolean(Config.APP_LIANG_TONG, true);
        isWeChatMotionEnable = mShareUtil.getBoolean(Config.APP_WECHART_MOTHION, true);
    }

    /**
     * 判断是否新的一天
     */
    private boolean isNewDay() {
        boolean result = false;

        ShareUtil mShareUtil = new ShareUtil(this);
        int saveDay = mShareUtil.getInt(Config.KEY_NEW_DAY, -1);
        int curDay = DateTimeUtil.getDayOfYear();

        if (saveDay != curDay) {
            result = true;
            mShareUtil.setShare(Config.KEY_NEW_DAY, curDay);
        }

        Log.d(Config.TAG, "isNewDay = " + result);
        return result;
    }


    /**
     * 启动UI界面
     */
    private void startUI() {
        startAlipayUI();
    }

    private void startAlipayUI() {
        AlipayForestMonitor.startAlipay(this);
//        mHandle.sendEmptyMessageDelayed(MSG_DELAY_ENTER_LIANGTONG, DEFAULT_DELAY_TIME * 10);
    }

    private void startLiangTongUI() {
//        LiangTongMonitor.startLiangTongUI(this);
    }
}