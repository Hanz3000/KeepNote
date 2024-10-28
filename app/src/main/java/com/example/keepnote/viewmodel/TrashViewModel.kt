package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash
import kotlinx.coroutines.launch

class TrashViewModel(private val trashDao: TrashDao, private val noteDao: NoteDao) : ViewModel() {

    // LiveData untuk mengambil semua data yang ada di trash, mengambil semua trah di trash dao
    val allTrash: LiveData<List<Trash>> = trashDao.getAllTrash()

    // Fungsi untuk memulihkan catatan dari trash ke notes
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

    // Fungsi untuk menghapus catatan secara permanen dari trash
    fun permanentlyDelete(trash: Trash) {
        viewModelScope.launch {
            trashDao.permanentlyDelete(trash.id)
        }
    }
}
