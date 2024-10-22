package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao, private val categoryDao: CategoryDao, private val trashDao: TrashDao) : ViewModel() {

    // Fungsi untuk mengambil semua catatan
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    // Fungsi untuk mengambil catatan berdasarkan kategori
    fun getNotesByCategory(categoryName: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(categoryName)
    }

    // Fungsi untuk menambahkan catatan
    fun insert(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }

    // Fungsi untuk mengupdate catatan
    fun update(note: Note) {
        viewModelScope.launch {
            // Memperbarui catatan berdasarkan ID, judul, dan konten
            noteDao.updateNote(note.id, note.title, note.content, note.category)
        }
    }

    // Fungsi untuk mengambil semua kategori
    fun getAllCategories(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name } // Mengambil nama dari setiap kategori
        }
    }

    fun getAllCategoryNames(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name } // Mengambil nama dari setiap kategori
        }
    }


    // Fungsi untuk menghapus catatan dan memindahkannya ke tempat sampah
    fun delete(note: Note) {
        viewModelScope.launch {
            // Masukkan catatan ke dalam tempat sampah sebelum menghapusnya
            trashDao.insert(
                Trash(
                noteId = note.id,
                deletedDate = System.currentTimeMillis(),
                title = note.title,
                content = note.content,
                category = note.category // Tambahkan kategori
            )
            )
            noteDao.deleteById(note.id)
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            categoryDao.deleteByName(categoryName)
            // Opsional: Pindahkan catatan dengan kategori yang dihapus ke kategori default atau hapus
            noteDao.updateCategoryToDefault(categoryName)
        }
    }

}
