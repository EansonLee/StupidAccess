package com.eason.stupidaccess.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.eason.stupidaccess.databinding.ActivityTestBinding
import com.eason.stupidaccess.service.AccessibilityServiceMonitor
import com.eason.stupidaccess.service.AlipayForestMonitor

class TestActivity : AppCompatActivity() {

    private val binding = ActivityTestBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.btnStartService.setOnClickListener {
            startService()
        }

        binding.btnShell.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnDing.setOnClickListener {
            AlipayForestMonitor.startAlipay(this)
        }
    }

    private fun startService() {
        val mIntent = Intent(this, AccessibilityServiceMonitor::class.java)
        startService(mIntent)
    }

}