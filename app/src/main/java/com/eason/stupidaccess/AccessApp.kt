package com.eason.stupidaccess

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.eason.stupidaccess.service.AccessibilityServiceMonitor
import com.eason.stupidaccess.util.AccessibilitUtil
import com.eason.stupidaccess.util.AlarmTaskUtil
import com.eason.stupidaccess.util.Config
import com.eason.stupidaccess.util.ShareUtil
import kotlin.system.exitProcess


class AccessApp : MultiDexApplication() {

    companion object {
        fun startAlarmTask(mContext: Context?) {
            wakeUpAndUnlock(mContext!!)
            if (!AccessibilitUtil.isAccessibilitySettingsOn(
                    mContext!!,
                    AccessibilityServiceMonitor::class.java.canonicalName
                )
            ) {
                Toast.makeText(mContext, "请先开启辅助功能，才能定时启动源计划", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val mShareUtil = ShareUtil(mContext)
            if (mShareUtil.getString(Config.KEY_PWD, "").isEmpty()) {
                Toast.makeText(mContext, "请先设置密码，才能定时启动源计划", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val hour: Int = mShareUtil.getInt(Config.KEY_HOUR, 7)
            val minute: Int = mShareUtil.getInt(Config.KEY_MINUTE, 0)
            val day: Int = mShareUtil.getInt(Config.KEY_DAY, 0)
            val intent = Intent(mContext, AccessibilityServiceMonitor::class.java)
            intent.action = AccessibilityServiceMonitor.ACTION_ALAM_TIMER
            AlarmTaskUtil.starRepeatAlarmTaskByService(mContext, hour, minute, day, 0, intent)
        }



        /**
         * 唤醒手机屏幕并解锁
         */
        @SuppressLint("InvalidWakeLockTag")
        fun wakeUpAndUnlock(mContext: Context) {
            // 获取电源管理器对象
            val pm = mContext.getSystemService(POWER_SERVICE) as PowerManager
            val screenOn = pm.isScreenOn
            if (!screenOn) {
                // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
                val wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"
                )
                wl.acquire(10000) // 点亮屏幕
                wl.release() // 释放
            }
//            // 屏幕解锁
            val keyguardManager = mContext.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            val keyguardLock = keyguardManager.newKeyguardLock("unLock")
            // 屏幕锁定
            keyguardLock.reenableKeyguard()
            keyguardLock.disableKeyguard() // 解锁
        }


        fun exitApp() {
            exitProcess(0)
        }
    }

}