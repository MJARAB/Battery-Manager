package com.mjcoder.batterymanager.activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.mjcoder.batterymanager.R
import com.mjcoder.batterymanager.utils.BatteryUsage
import com.mjcoder.batterymanager.databinding.ActivityMainBinding
import com.mjcoder.batterymanager.helper.SpManager
import com.mjcoder.batterymanager.model.BatteryModel
import com.mjcoder.batterymanager.service.BatteryAlarmService
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDrawer()
        serviceConfig()

        registerReceiver(batteryInfoReciver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))


    }

    private fun initDrawer() {
        binding.imgMenu.setOnClickListener {
            binding.drawer.openDrawer(Gravity.RIGHT)
        }
        binding.incDrawer.txtAppUsage.setOnClickListener {
            startActivity(Intent(this@MainActivity, UsageBatteryActivity::class.java))
            binding.drawer.closeDrawer(Gravity.RIGHT)
        }
    }

    private fun serviceConfig() {
        if (SpManager.isServiceOn(this@MainActivity) == true) {
            startService()
            binding.incDrawer.serviceSwitchText.text = "service is on"
            binding.incDrawer.serviceSwitch.isChecked = true
        } else {
            stopService()
            binding.incDrawer.serviceSwitchText.text = "service is off"
            binding.incDrawer.serviceSwitch.isChecked = false
        }
        binding.incDrawer.serviceSwitch.setOnCheckedChangeListener { switch, isCheck ->
            SpManager.setServiceState(this@MainActivity, isCheck)

            if (isCheck) {
                startService()
                binding.incDrawer.serviceSwitchText.text = "service is on"
                Toast.makeText(applicationContext, "service is turn on", Toast.LENGTH_SHORT).show()
            } else {
                stopService()
                binding.incDrawer.serviceSwitchText.text = "service is off"
                Toast.makeText(applicationContext, "service is turn off", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        stopService(serviceIntent)
    }

    private var batteryInfoReciver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) {
                binding.txtPlug.text = "plug-out"
            } else {
                binding.txtPlug.text = "plug-in"
            }
            binding.txtTemp.text =
                (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10).toString() + " °C"
            binding.txtVoltage.text =
                (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000).toString() + " volt"
            binding.txtTechnology.text = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            binding.circularProgressBar.progressMax = 100F
            binding.circularProgressBar.setProgressWithAnimation(batteryLevel.toFloat())
            binding.txtCharge.text = batteryLevel.toString() + "%"
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            when (health) {
                BatteryManager.BATTERY_HEALTH_DEAD -> {
                    binding.txtHealth.text =
                        "your battery is fully dead, please change your battery"
                    binding.txtHealth.setTextColor(Color.parseColor("#000000"))
                    binding.imgHealth.setImageResource(R.drawable.health_dead)
                }
                BatteryManager.BATTERY_HEALTH_GOOD -> {
                    binding.txtHealth.text = "your battery is good, please take care of that"
                    binding.txtHealth.setTextColor(Color.GREEN)
                    binding.imgHealth.setImageResource(R.drawable.health_good)
                }
                BatteryManager.BATTERY_HEALTH_COLD -> {
                    binding.txtHealth.text = "your battery is cold, it's ok"
                    binding.txtHealth.setTextColor(Color.BLUE)
                    binding.imgHealth.setImageResource(R.drawable.health_cold)
                }
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                    binding.txtHealth.text =
                        "your battery is overheat, please don't work with your phone"
                    binding.txtHealth.setTextColor(Color.RED)
                    binding.imgHealth.setImageResource(R.drawable.health_overheat)
                }
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                    binding.txtHealth.text =
                        "your battery is overvoltage, please don't work with your phone"
                    binding.txtHealth.setTextColor(Color.YELLOW)
                    binding.imgHealth.setImageResource(R.drawable.health_volt)
                }
                else -> {
                    binding.txtHealth.text =
                        "your battery is fully dead, please change your battery"
                    binding.txtHealth.setTextColor(Color.parseColor("#000000"))
                    binding.imgHealth.setImageResource(R.drawable.health_dead)
                }
            }
        }
    }

    override fun onBackPressed() {
        val dialogBuilder =
            AlertDialog.Builder(this).setMessage("Do you want to close application?")
                .setCancelable(true)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
                .setNegativeButton(
                    "Cancel",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = dialogBuilder.create()
        alert.setTitle("Exit App")
        alert.show()
    }
}