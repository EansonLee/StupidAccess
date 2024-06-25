package com.eason.stupidaccess.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eason.stupidaccess.databinding.ActivityCustomBinding

class CustomActivity : AppCompatActivity(){

    private val binding by lazy {
        ActivityCustomBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

    }
}