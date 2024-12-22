package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Mendefinisikan entitas Category yang mewakili tabel "categories" di database
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Long = 0, // Primary Key, dihasilkan secara otomatis
    val name: String // Kolom untuk menyimpan nama kategori
)
