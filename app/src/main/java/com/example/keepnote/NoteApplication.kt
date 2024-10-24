package com.example.keepnote

import android.app.Application

class NoteApplication : Application() {
    // Menggunakan lazy initialization untuk menginisialisasi database
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    // DAO untuk catatan, diambil dari database
    val noteDao by lazy { database.noteDao() }
    // DAO untuk kategori, diambil dari database
    val categoryDao by lazy { database.categoryDao() }
    // DAO untuk tempat sampah, diambil dari database
    val trashDao by lazy { database.trashDao() }
}
