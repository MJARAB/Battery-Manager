package com.mjcoder.batterymanager.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mjcoder.batterymanager.databinding.ActivitySplashBinding
import com.mjcoder.batterymanager.helper.SpManager
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val textArray = arrayOf(
            "Make Your Battery Powerful",
            "Make Your Battery Safe",
            "Make Your Battery Faster",
            "Make Your Battery Powerful",
            "Manage Your Phone Battery",
            "Notify When your Phone Is Full Charge"
        )

        for (i in 1..6) {
            helpTextGenerator((i * 1000).toLong(), textArray[i - 1])
        }
        Timer().schedule(timerTask {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 7000)
    }

    private fun helpTextGenerator(delayTime: Long, helpTxt: String) {
        Timer().schedule(timerTask {
            runOnUiThread(timerTask {
                binding.helpText.text = helpTxt
            })
        }, delayTime)
    }
}