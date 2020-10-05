package com.example.applock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import java.util.ArrayList

class AppList(val name: String, var icon: Drawable, val packages: String)

fun getInstalledApps(context: Context): List<AppList> {
    val pm = context.packageManager
    val apps: MutableList<AppList> = ArrayList()
    val packs = pm.getInstalledPackages(0)
    for (i in packs.indices) {
        val p = packs[i]

        val appName = p.applicationInfo.loadLabel(pm).toString()
        val icon = p.applicationInfo.loadIcon(pm)
        val packages = p.applicationInfo.packageName
        apps.add(AppList(appName, icon, packages))
    }
    return apps
}

fun appLock(app: String, settings: SharedPreferences, value: Boolean) {
    val editor = settings.edit()
    editor.putBoolean(app, value)
    editor.apply()
}

fun toggleAppLock(app: String, settings: SharedPreferences) {
    val isLocked = settings.getBoolean(app, false)
    appLock(app, settings, !isLocked)
}

fun isAppLocked(app: String, settings: SharedPreferences): Boolean {
    return settings.getBoolean(app, false)
}