package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Mendefinisikan entitas Note yang mewakili tabel "notes" di database
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Long = 0, // Primary Key, dihasilkan secara otomatis
    val title: String, // Kolom untuk judul catatan
    val content: String, // Kolom untuk isi catatan
    val category: String, // Kolom untuk kategori catatan
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) // Kolom untuk timestamp dengan nilai default
){
}