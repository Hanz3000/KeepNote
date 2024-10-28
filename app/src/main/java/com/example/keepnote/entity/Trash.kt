package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Mendefinisikan entitas Trash yang mewakili tabel "trash" di database
@Entity(tableName = "trash")
data class Trash(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Primary Key, dihasilkan secara otomatis
    val noteId: Long, // ID catatan yang dihapus, untuk referensi ke catatan asli
    val deletedDate: Long, // Tanggal dan waktu ketika catatan dihapus, disimpan dalam format timestamp
    val title: String, // Kolom untuk menyimpan judul catatan yang dihapus
    val content: String, // Kolom untuk menyimpan isi catatan yang dihapus
    val category: String, // Kolom untuk kategori dari catatan yang dihapus
    val isPermanentlyDeleted: Boolean = false, // Status untuk menentukan apakah catatan dihapus secara permanen
)
