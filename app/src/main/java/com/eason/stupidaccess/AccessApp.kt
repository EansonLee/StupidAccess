package com.eason.stupidaccess

import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDexApplication
import com.eason.stupidaccess.service.AccessibilityServiceMonitor
import com.eason.stupidaccess.util.AlarmTaskUtil
import com.eason.stupidaccess.util.Config
import com.eason.stupidaccess.util.ShareUtil

class AccessApp : MultiDexApplication() {

    companion object {
        fun startAlarmTask(mContext: Context?) {
            val mShareUtil = ShareUtil(mContext)
            val hour: Int = mShareUtil.getInt(Config.KEY_HOUR, 7)
            val minute: Int = mShareUtil.getInt(Config.KEY_MINUTE, 0)
            val intent = Intent(mContext, AccessibilityServiceMonitor::class.java)
            intent.action = AccessibilityServiceMonitor.ACTION_ALAM_TIMER
            AlarmTaskUtil.starRepeatAlarmTaskByService(mContext, hour, minute, 0, intent)
        }
    }
}