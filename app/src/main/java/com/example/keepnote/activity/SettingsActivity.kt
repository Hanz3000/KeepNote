package com.example.keepnote.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.keepnote.R

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val btnLightTheme: Button = findViewById(R.id.btnLightTheme)
        val btnDarkTheme: Button = findViewById(R.id.btnDarkTheme)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnLightTheme.setOnClickListener {
            setAppTheme(this, "LIGHT")
            recreate() // Restart Activity untuk menerapkan tema
        }

        btnDarkTheme.setOnClickListener {
            setAppTheme(this, "DARK")
            recreate() // Restart Activity untuk menerapkan tema
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Kembali ke activity sebelumnya
                true
            }
            else -> super.onOptionsItemSelected(item)
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