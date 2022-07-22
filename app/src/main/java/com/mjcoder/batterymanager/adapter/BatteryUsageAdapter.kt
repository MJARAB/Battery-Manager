package com.mjcoder.batterymanager.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjcoder.batterymanager.R
import com.mjcoder.batterymanager.model.BatteryModel
import kotlin.math.roundToInt

class BatteryUsageAdapter(
    private val context: Context,
    private val battery: MutableList<BatteryModel>,
    private val totalTime: Long
) :
    RecyclerView.Adapter<BatteryUsageAdapter.ViewHolder>() {
    private var batteryFinalList: MutableList<BatteryModel> = ArrayList()

    init {
        batteryFinalList = calcBatteryUsage(battery)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BatteryUsageAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.item_battery_usage, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: BatteryUsageAdapter.ViewHolder, position: Int) {
        holder.txtpercent.text = batteryFinalList[position].percentUsage.toString() + "%"
        holder.txttime.text = batteryFinalList[position].timeUsage.toString()
        holder.txtAppName.text = getAppName(batteryFinalList[position].packageName.toString())
        holder.progressbar.progress = batteryFinalList[position].percentUsage
        holder.imageview.setImageDrawable(getAppIcon(batteryFinalList[position].packageName.toString()))
    }

    override fun getItemCount(): Int {
        return batteryFinalList.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtpercent: TextView = view.findViewById(R.id.txt_percent)
        var txttime: TextView = view.findViewById(R.id.txt_time)
        var txtAppName: TextView = view.findViewById(R.id.txt_app_name)
        var progressbar: ProgressBar = view.findViewById(R.id.progressbar)
        var imageview: ImageView = view.findViewById(R.id.imageView2)
    }

    fun calcBatteryUsage(batteryPercentArray: MutableList<BatteryModel>): MutableList<BatteryModel> {
        val finalList: MutableList<BatteryModel> = ArrayList()
        var sortedList = batteryPercentArray
            .groupBy { it.packageName }.mapValues { entry -> entry.value.sumBy { it.percentUsage } }
            .toList()
            .sortedWith(compareBy { it.second }).reversed()
        for (item in sortedList) {
            val bm = BatteryModel()
            val timePerApp =
                item.second.toFloat() / 100 * totalTime.toFloat() / 1000 / 60
            val hour = timePerApp / 60
            val min = timePerApp % 60
            bm.packageName = item.first
            bm.percentUsage = item.second
            bm.timeUsage = "${hour.roundToInt()} hour : ${min.roundToInt()} minutes"
            finalList += bm
        }
        return finalList
    }


    fun getAppName(packageName: String): String {
        val pm = context.applicationContext.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
    }

    fun getAppIcon(packageName: String): Drawable? {
        var icon: Drawable? = null
        try {
            icon = context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return icon
    }


}