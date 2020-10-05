package com.example.applock

import android.app.Activity
import android.app.ActivityManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Field
import java.util.*


class MainActivity : AppCompatActivity() {
    private var installedApps: List<AppList>? = null
    private var installedAppAdapter: AppAdapter? = null
    var userInstalledApps: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userInstalledApps = findViewById<View>(R.id.installed_app_list) as ListView
        installedApps = getInstalledApps(this)
        installedAppAdapter = AppAdapter(this@MainActivity, installedApps!!)
        userInstalledApps!!.adapter = installedAppAdapter
        userInstalledApps!!.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                val app = installedApps!![i].packages.split(".").last()

                var colors = arrayOf("")
                if (isAppLocked(app, getSharedPreferences("PREFS",0)))
                    colors = arrayOf ("App Info", "Unlock App")
                else colors = arrayOf("App Info", "Lock App")

                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Choose Action").setItems(colors,
                        DialogInterface.OnClickListener { dialog, which -> // The 'which' argument contains the index position of the selected item
                            if (which == 0) {
                                if (isAppLocked("settings", getSharedPreferences("PREFS",0))) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Settings are locked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    intent.data = Uri.parse(
                                        "package:" + installedApps!![i].packages
                                    )
                                    Toast.makeText(
                                        this@MainActivity,
                                        installedApps!![i].packages,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(intent)
                                }
                            }
                            else if (which == 1) {
                                val app = installedApps!![i].packages.split(".").last()
                                toggleAppLock(app, getSharedPreferences("PREFS",0))
                            }
                        })
                builder.show()
            }

        //Total Number of Installed-Apps(i.e. List Size)
        val abc = userInstalledApps!!.count.toString() + ""
        Toast.makeText(this, "$abc Apps", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, BackAppListenerService::class.java)
        startService(intent)
    }

    inner class AppAdapter(
        context: Context,
        customizedListView: List<AppList>
    ) :
        BaseAdapter() {
        var layoutInflater: LayoutInflater
        var listStorage: List<AppList>
        override fun getCount(): Int {
            return listStorage.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View? {
            var convertView = convertView
            val listViewHolder: ViewHolder
            if (convertView == null) {
                listViewHolder = ViewHolder()
                convertView = layoutInflater.inflate(R.layout.installed_app_list, parent, false)
                listViewHolder.textInListView = convertView.findViewById<View>(R.id.list_app_name) as TextView
                listViewHolder.imageInListView = convertView.findViewById<View>(R.id.app_icon) as ImageView
                listViewHolder.packageInListView = convertView.findViewById<View>(R.id.app_package) as TextView
                convertView.tag = listViewHolder
            } else {
                listViewHolder = convertView.tag as ViewHolder
            }
            listViewHolder.textInListView?.text = listStorage[position].name
            listViewHolder.imageInListView?.setImageDrawable(listStorage[position].icon)
            listViewHolder.packageInListView?.text = listStorage[position].packages
            return convertView
        }

        internal inner class ViewHolder {
            var textInListView: TextView? = null
            var imageInListView: ImageView? = null
            var packageInListView: TextView? = null
        }

        init {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            listStorage = customizedListView
        }
    }

}