package com.eason.stupidaccess

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.eason.stupidaccess.service.AccessibilityServiceMonitor
import com.eason.stupidaccess.util.AccessibilitUtil
import com.eason.stupidaccess.util.AlarmTaskUtil
import com.eason.stupidaccess.util.Config
import com.eason.stupidaccess.util.ShareUtil

class AccessApp : MultiDexApplication() {

    companion object {
        fun startAlarmTask(mContext: Context?) {
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
            val intent = Intent(mContext, AccessibilityServiceMonitor::class.java)
            intent.action = AccessibilityServiceMonitor.ACTION_ALAM_TIMER
            AlarmTaskUtil.starRepeatAlarmTaskByService(mContext, hour, minute, 0, intent)
        }
    }
}