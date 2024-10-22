package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import kotlinx.coroutines.launch

class TrashViewModel(private val trashDao: TrashDao, private val noteDao: NoteDao) : ViewModel() {

    val allTrash: LiveData<List<Trash>> = trashDao.getAllTrash()

    fun recover(trash: Trash) {
        viewModelScope.launch {
            // Memulihkan catatan ke tabel notes
            val recoveredNote = Note(
                id = 0, // Biarkan Room generate ID baru
                title = trash.title,
                content = trash.content,
                category = trash.category
            )
            noteDao.insert(recoveredNote)

            // Menghapus catatan dari tabel trash
            trashDao.deleteById(trash.id)
        }
    }

    fun permanentlyDelete(trash: Trash) {
        viewModelScope.launch {
            trashDao.permanentlyDelete(trash.id)
        }
    }
}