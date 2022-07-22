package com.mjcoder.batterymanager.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.mjcoder.batterymanager.R
import com.mjcoder.batterymanager.adapter.BatteryUsageAdapter
import com.mjcoder.batterymanager.databinding.ActivityMainBinding
import com.mjcoder.batterymanager.databinding.ActivityUsageBatteryBinding
import com.mjcoder.batterymanager.model.BatteryModel
import com.mjcoder.batterymanager.utils.BatteryUsage
import kotlin.math.roundToInt

class UsageBatteryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsageBatteryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageBatteryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val batteryUsage = BatteryUsage(this)
        val batteryPercentArray: MutableList<BatteryModel> = ArrayList()
        for (item in batteryUsage.getUsageStateList()) {
            if (item.totalTimeInForeground > 0) {
                val bm = BatteryModel()
                bm.packageName = item.packageName
                bm.percentUsage =
                    (item.totalTimeInForeground.toFloat() / batteryUsage.getTotalTime()
                        .toFloat() * 100).toInt()
                batteryPercentArray += bm
            }
        }



        var adapter = BatteryUsageAdapter(this,batteryPercentArray,batteryUsage.getTotalTime())
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter

    }
}