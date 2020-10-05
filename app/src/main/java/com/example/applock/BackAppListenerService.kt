package com.example.applock

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import java.util.*


class BackAppListenerService  : Service() {
    private var isRunning = false
    private var lastApp = ""
    private var lockApps = mutableListOf<String>("settings", "mytvapplication")

    override fun onCreate() {
        isRunning = true
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        for (app in lockApps) {
                val settings: SharedPreferences = getSharedPreferences("PREFS",0)
                val lock = settings.getBoolean(app, true)
                if (lock) appLock(app, getSharedPreferences("PREFS",0), true)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(10)
                } catch (e: Exception) {
                }
                val currentApp = getForegroundApp()
                if (currentApp != lastApp) {
                    println(currentApp)
                    // New app on front
                    lastApp = currentApp
                    if (isAppLocked(currentApp, getSharedPreferences("PREFS",0))) showHomeScreen()
                }
            }
        }).start()
        return START_STICKY
    }

    fun showHomeScreen(): Boolean {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(startMain)
        return true
    }

    // Must Have Usage Access Permission
    fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usm = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> =
                    TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey())!!.getPackageName()
                }
            }
        } else {
            val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }
        return currentApp.split(".").last()
    }

    override fun onBind(arg0: Intent): IBinder? {
        println(TAG + "Service onBind")
        return null
    }

    override fun onDestroy() {
        isRunning = false
        println(TAG + "Service onDestroy")
    }

    companion object {
        private const val TAG = "HelloService"
    }
}