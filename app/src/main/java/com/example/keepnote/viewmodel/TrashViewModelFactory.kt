package com.example.keepnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao

class TrashViewModelFactory(private val trashDao: TrashDao, private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrashViewModel(trashDao, noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}