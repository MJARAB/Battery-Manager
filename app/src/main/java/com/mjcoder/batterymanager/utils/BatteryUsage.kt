package com.mjcoder.batterymanager.utils

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.util.*

class BatteryUsage(context: Context) {

    private val myContext = context

    init {
        if (getUsageStateList().isEmpty()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }
    }


    fun getUsageStateList(): List<UsageStats> {
        val usm = getUsageStateManager(myContext)
        val endTime = System.currentTimeMillis()
        val startTime = System.currentTimeMillis() - 24 * 3600 * 1000
        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    }

    fun getTotalTime(): Long {
        var totalTime: Long = 0
        for (item in getUsageStateList()) {
            totalTime += item.totalTimeInForeground
        }
        return totalTime
    }

    private fun getUsageStateManager(context: Context): UsageStatsManager {
        return context.getSystemService("usagestats") as UsageStatsManager
    }
}