package com.example.keepnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao

class TrashViewModelFactory(
    private val trashDao: TrashDao, // DAO untuk berinteraksi dengan data trash
    private val noteDao: NoteDao // DAO untuk berinteraksi dengan data notes
) : ViewModelProvider.Factory {

    // Fungsi untuk membuat instance dari ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass yang diminta adalah TrashViewModel
        if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Mengabaikan peringatan tentang casting
            return TrashViewModel(trashDao, noteDao) as T // Mengembalikan instance TrashViewModel
        }
        // Jika modelClass tidak dikenali, lemparkan exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
