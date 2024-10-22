package com.example.keepnote

import android.app.Application

class NoteApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val noteDao by lazy { database.noteDao() }
    val categoryDao by lazy { database.categoryDao() }
    val trashDao by lazy { database.trashDao() }
}