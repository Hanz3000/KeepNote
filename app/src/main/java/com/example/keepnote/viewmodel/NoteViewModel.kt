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

// Kelas ini adalah ViewModel untuk mengelola catatan, kategori, dan tempat sampah
class NoteViewModel(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val trashDao: TrashDao
) : ViewModel() {

    // Mengambil semua catatan dari database di tampilkan ke main actiity
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    // Mengambil catatan berdasarkan kategori
    fun getNotesByCategory(categoryName: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(categoryName)
    }

    // Menambahkan catatan baru
    fun insert(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note) //memanggil dao untuk menyimpan catatan
        }
    }

    // Memperbarui catatan yang sudah ada
    fun update(note: Note) {
        viewModelScope.launch {
            noteDao.updateNote(note.id, note.title, note.content, note.category) //mengupdate dari addnote untuk menyimpannya ke dao
        }
    }

    // Mengambil semua kategori dan mengembalikan nama-nama kategori
    fun getAllCategories(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name } // Mengambil nama dari setiap kategori
        }
    }

    // Fungsi yang sama dengan getAllCategories() untuk mendapatkan nama kategori
    fun getAllCategoryNames(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name } // Mengambil nama dari setiap kategori
        }
    }

    // Menghapus catatan dan memindahkannya ke tempat sampah
    fun delete(note: Note) {
        viewModelScope.launch {
            // Memindahkan catatan ke tempat sampah sebelum menghapus
            trashDao.insert(
                Trash(
                    noteId = note.id,
                    deletedDate = System.currentTimeMillis(),
                    title = note.title,
                    content = note.content,
                    category = note.category,
                )
            )
            noteDao.deleteById(note.id)
        }
    }

    // Menghapus kategori tertentu dan memperbarui catatan terkait
    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            categoryDao.deleteByName(categoryName)

            noteDao.updateCategoryToDefault(categoryName)
        }
    }
}
