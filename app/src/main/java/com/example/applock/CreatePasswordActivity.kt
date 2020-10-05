package com.example.applock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.Toast

class CreatePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)

        val editText1: EditText = findViewById(R.id.editTextTextPassword)
        val editText2: EditText = findViewById(R.id.editTextTextPassword2)
        val button: Button = findViewById(R.id.button)

        button.setOnClickListener(View.OnClickListener {
            val text1: String = editText1.getText().toString()
            val text2: String = editText2.getText().toString()

            if (text1.equals("") || text2.equals("")) {
                // No password
                Toast.makeText(this, "No password entered", Toast.LENGTH_SHORT).show()
            }
            else if (text1.equals((text2))) {
                // Password match
                val settings = getSharedPreferences("PREFS", 0)
                val editor = settings.edit()
                editor.putString("password", text1)
                editor.apply()

                // Enter the app
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                // No match
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        })

    }
}