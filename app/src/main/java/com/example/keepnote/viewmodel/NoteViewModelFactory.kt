package com.example.keepnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao

// Kelas ini adalah factory untuk membuat instance NoteViewModel
class NoteViewModelFactory(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val trashDao: TrashDao
) : ViewModelProvider.Factory {
    // Fungsi ini digunakan untuk membuat instance ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass yang diminta adalah NoteViewModel
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao, categoryDao, trashDao) as T
        }
        // Jika tidak, lemparkan IllegalArgumentException
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
