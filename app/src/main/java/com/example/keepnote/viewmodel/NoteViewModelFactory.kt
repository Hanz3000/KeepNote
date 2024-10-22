package com.example.keepnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao

class NoteViewModelFactory(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val trashDao: TrashDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao, categoryDao, trashDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
