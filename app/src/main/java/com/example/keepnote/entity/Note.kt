package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
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
    // Metode ini digunakan untuk menghindari pengiriman id saat menyimpan ke Firebase
    @Exclude
    fun getFirebaseId(): Long? = if (id == 0L) null else id
}