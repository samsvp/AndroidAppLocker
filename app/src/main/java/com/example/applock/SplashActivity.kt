package com.example.applock

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Load the password
        val settings: SharedPreferences = getSharedPreferences("PREFS",0)
        password = settings.getString("password", "").toString()

        val handler = Handler()
        handler.postDelayed({
            if (password == "") {
                // No password
                val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // There is a password
                val intent = Intent(applicationContext, EnterPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}