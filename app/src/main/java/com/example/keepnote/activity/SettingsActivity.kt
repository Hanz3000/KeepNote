package com.example.keepnote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.keepnote.MainActivity
import com.example.keepnote.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val btnLightTheme: Button = findViewById(R.id.btnLightTheme)
        val btnDarkTheme: Button = findViewById(R.id.btnDarkTheme)

        btnLightTheme.setOnClickListener {
            setAppTheme(this, "LIGHT")
            recreate() // Restart Activity untuk menerapkan tema
        }

        btnDarkTheme.setOnClickListener {
            setAppTheme(this, "DARK")
            recreate() // Restart Activity untuk menerapkan tema
        }
    }

    // Fungsi untuk mengganti tema
    private fun setAppTheme(context: Context, theme: String) {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPref.edit().putString("APP_THEME", theme).apply()

        when (theme) {
            "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    // Fungsi untuk mengambil tema yang dipilih
    private fun getAppTheme(context: Context): String {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("APP_THEME", "LIGHT") ?: "LIGHT" // Default ke terang
    }
}