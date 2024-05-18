package com.eason.stupidaccess.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * show me the fxxking code
 *
 * onInterrupt() → 服务中断时回调；
 * onAccessibilityEvent() → 接收到系统发送AccessibilityEvent时回调，如：顶部Notification，界面更新，
 * 内容变化等，我们可以筛选特定的事件类型，执行不同的响应。比如：顶部出现WX加好友的Notification Event，跳转到加好友页自动通过。
 *
 * 具体的Event类型可参见文尾附录，另外两个 可选 的重写方法：
 *
 * onServiceConnected() → 当系统成功连接无障碍服务时回调，可在此调用 setServiceInfo() 对服务进行配置调整
 * onUnbind() → 系统将要关闭无障碍服务时回调，可在此进行一些关闭流程，如取消分配的音频管理器
 *
 */
class DingService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        Log.d(DingActivity.TAG, "onAccessibilityEvent: ${event?.eventType}")
    }

    override fun onInterrupt() {
//        Log.d(DingActivity.TAG, "onInterrupt")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}