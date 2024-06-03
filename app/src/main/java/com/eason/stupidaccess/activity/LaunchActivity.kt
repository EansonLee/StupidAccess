package com.eason.stupidaccess.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ThreadUtils
import com.eason.stupidaccess.databinding.ActivityLaunchBinding


class LaunchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLaunchBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val lp: WindowManager.LayoutParams = getWindow().getAttributes()
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        getWindow().setAttributes(lp)

        val decorView: View = getWindow().getDecorView()
        var systemUiVisibility = decorView.systemUiVisibility
        val flags = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏导航栏
                or View.SYSTEM_UI_FLAG_FULLSCREEN) //隐藏状态栏

        systemUiVisibility = systemUiVisibility or flags

        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility)




        ThreadUtils.runOnUiThreadDelayed({
            startActivity(Intent(this, TaskActivity::class.java))
            finish()
        }, 2000)
    }
}