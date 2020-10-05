package com.example.applock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class EnterPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_password)

        val settings = getSharedPreferences("PREFS", 0)
        val password: String = settings.getString("password", "").toString()

        val editText: EditText = findViewById(R.id.editTextTextPassword)
        val button: Button = findViewById(R.id.button)

        button.setOnClickListener(View.OnClickListener {
            val text: String = editText.getText().toString()

            if (text.equals(password)) {
                // Enter the app
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
            }
        })
    }
}