package com.example.keepnote.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.keepnote.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Setup Toolbar with back navigation
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    // Handle toolbar navigation click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Method to open support link
    fun openSupportLink(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/farhanrmdhan__?igsh=Z21yNHh2eG1lcGF6"))
        startActivity(intent)
    }

    // Method to open suggestion link
    fun openSuggestionLink(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/qr/LHDY2C6WBJH2I1"))
        startActivity(intent)
    }
}
